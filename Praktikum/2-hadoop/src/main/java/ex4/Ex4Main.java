package ex4;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

public class Ex4Main extends Configured implements Tool {
	private static final int	MAX_MR_JOBS	= 10;

	public static enum Counters {
		COMPLETE_CHAINS;
		private Counters() {
		}
	}

	private Job createInitialJob(Configuration conf, Path outpath) throws IOException {
		Job job = Job.getInstance(conf);
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");

		job.setJarByClass(Ex4Main.class);
		job.setJobName("FriendChains:Initial");

		// input Paths
		job.setInputFormatClass(TextInputFormat.class);
		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));

		// use SequenceFile as output
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(job, outpath);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// we only need a mapper
		job.setMapperClass(InitialMapper.class);
		job.setNumReduceTasks(0);

		return job;
	}

	private Job createRecurringJob(Configuration conf, Path initFriendship, Path previousChains,
			Path outputPath) throws IOException {
		Job job = Job.getInstance(conf);
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");

		job.setInputFormatClass(SequenceFileInputFormat.class);
		if (previousChains != null) {
			// Input: from first Mapper + chains from previous M/R-Job
			SequenceFileInputFormat.setInputPaths(job, initFriendship, previousChains);
		} else {
			SequenceFileInputFormat.setInputPaths(job, new Path[] { initFriendship });
		}

		// use SequenceFile as output
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(job, outputPath);

		job.setJarByClass(Ex4Main.class);
		job.setJobName("FriendChains:" + new Date().toString());

		// we only need a reducer
		job.setOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setReducerClass(FriendsChainReducer.class);

		return job;
	}

	private Job createReformatJob(Configuration conf, Path inpath, Path outpath) throws IOException {
		Job job = Job.getInstance(conf);
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");

		job.setJarByClass(Ex4Main.class);
		job.setJobName("FriendChains:Formatting");

		job.setInputFormatClass(SequenceFileInputFormat.class);
		SequenceFileInputFormat.setInputPaths(job, new Path[] { inpath });

		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		TextOutputFormat.setOutputPath(job, outpath);

		// we only need a reducer
		job.setReducerClass(ResultReducer.class);
		job.setNumReduceTasks(1);

		return job;
	}

	private Path getTemporaryPath(Configuration conf) {
		return new Path("/tmp/" + Long.toHexString(new Date().getTime()));
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage: Ex4Main <input dir> <output dir> <userA> <userB>");
			System.exit(-1);
		}
		Const.PATH_INPUT = args[0];
		Const.PATH_OUTPUT = args[1];


		Configuration conf = getConf();
		conf.set("userA", args[2]);
		conf.set("userB", args[3]);
		conf.set("mapred.used.genericoptionsparser", "true");

		Path initFriendshipPath = getTemporaryPath(conf);
		Job initialJob = createInitialJob(conf, initFriendshipPath);
		initialJob.waitForCompletion(true);


		// run the recurring Jobs
		Job currentJob = null;
		Path currentIn = null;
		for (int i = 0;; i++) {
			if (i == MAX_MR_JOBS) {
				// did not find any connection on 10 M/R jobs
				return 1;
			}

			Path currentOut = getTemporaryPath(conf);
			currentJob = createRecurringJob(conf, initFriendshipPath, currentIn, currentOut);

			// run the job
			currentJob.waitForCompletion(true);
			currentIn = currentOut;

			if (currentJob.getCounters().findCounter(Counters.COMPLETE_CHAINS).getValue() != 0L) {
				break;
			}
		}

		// run a job to fetch the results
		Utils.deleteOutputDirectory(conf, "out");
		createReformatJob(conf, currentIn, new Path("out")).waitForCompletion(true);

		// print the result
		Utils.printOutputFile("out");

		return 0;
	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new Ex4Main(), args));
	}
}
