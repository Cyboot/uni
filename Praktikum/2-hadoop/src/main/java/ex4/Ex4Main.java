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
	public static enum Counters {
		FOUND_CHAINS;

		private Counters() {
		}
	}

	private Job createInitialJob(Configuration conf, Path outpath)
			throws IOException {
		Job job = Job.getInstance(conf);
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");
		job.setJarByClass(Ex4Main.class);
		job.setNumReduceTasks(0);
		job.setJobName("FriendChains:Initial");
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		SequenceFileOutputFormat.setOutputPath(job, outpath);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(InitialFileMapper.class);

		return job;
	}

	private Job createRecurringJob(Configuration conf, Path allFriendships,
			Path previousChains, Path outputPath) throws IOException {
		Job job = Job.getInstance(conf);
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		if (previousChains != null) {
			SequenceFileInputFormat.setInputPaths(job, new Path[] {
					allFriendships, previousChains });
		} else {
			SequenceFileInputFormat.setInputPaths(job,
					new Path[] { allFriendships });
		}
		SequenceFileOutputFormat.setOutputPath(job, outputPath);

		job.setJarByClass(Ex4Main.class);
		job.setJobName("FriendChains:" + new Date().toString());

		job.setOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);

		job.setReducerClass(ChainReducer.class);

		return job;
	}

	private Job createReformatJob(Configuration conf, Path inpath, Path outpath)
			throws IOException {
		Job job = Job.getInstance(conf);
		job.setJarByClass(Ex4Main.class);
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");
		job.setJobName("FriendChains:Reformat");
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		SequenceFileInputFormat.setInputPaths(job, new Path[] { inpath });
		TextOutputFormat.setOutputPath(job, outpath);
		job.setReducerClass(ResultFilterReducer.class);
		return job;
	}

	private Path getTemporaryPath(Configuration conf) {
		return new Path("/tmp/" + Long.toHexString(new Date().getTime()));
	}

	@Override
	public int run(String[] strings) throws Exception {
		Configuration conf = getConf();
		conf.set("mapred.used.genericoptionsparser", "true");

		Path initialDataPath = getTemporaryPath(conf);
		Job initialJob = createInitialJob(conf, initialDataPath);
		initialJob.waitForCompletion(true);

		Job currentJob = null;
		Path currentIn = null;
		for (int i = 0; i <= 10; i++) {
			if (i == 10) {
				return 1;
			}
			Path currentOut = getTemporaryPath(conf);
			currentJob = createRecurringJob(conf, initialDataPath, currentIn,
					currentOut);
			currentJob.waitForCompletion(true);
			currentIn = currentOut;
			if (currentJob.getCounters().findCounter(Counters.FOUND_CHAINS)
					.getValue() != 0L) {
				break;
			}
		}
		Utils.deleteOutputDirectory(conf, "/out");
		createReformatJob(conf, currentIn, new Path("/out")).waitForCompletion(
				true);

		return 0;
	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new Ex4Main(), args));
	}
}
