package ex1b;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

import ex1a.Ex1AMain;

public class Ex1BMain extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: Ex1BMain <input dir> <output dir>");
			System.exit(-1);
		}
		Const.PATH_INPUT = args[0];
		Const.PATH_OUTPUT = args[1];

		// create the initial Job
		Job job = createInitalJob(getConf(), Const.PATH_INPUT, "/tmp/out-0");

		String output = null;
		String input = null;

		// run the recurring jobs
		if (job.waitForCompletion(true)) {
			for (int i = 1; i <= 2; i++) {
				input = "/tmp/out-" + (i - 1);
				output = "/tmp/out-" + i;

				Job tmpJob = createRecurringJob(getConf(), input, output);
				tmpJob.setJobName("PageRank: Job" + i);

				tmpJob.waitForCompletion(true);
			}

		}

		Job formatJob = createFormatJob(getConf(), input, Const.PATH_OUTPUT);

		if (formatJob.waitForCompletion(true)) {
			Utils.printOutputFile(Const.PATH_OUTPUT);

			return 0;
		}

		return 1;
	}

	private Job createFormatJob(Configuration conf, String input, String output)
			throws IllegalArgumentException, IOException {
		Job job = createRecurringJob(conf, input, output);

		// use TextOutputFormat for the last Job
		job.setOutputFormatClass(TextOutputFormat.class);

		SequenceFileInputFormat.setInputPaths(job, new Path(input));
		TextOutputFormat.setOutputPath(job, new Path(output));

		// use FormatReduce and Text as OutputValue
		job.setReducerClass(FormatReducer.class);
		job.setOutputValueClass(Text.class);

		return job;
	}

	private Job createInitalJob(Configuration conf, String input, String output) throws IOException {
		Job job = createRecurringJob(conf, input, output);

		// use TextInputFormat for initial job
		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.setInputPaths(job, new Path(input));

		job.setMapperClass(InitialMapper.class);

		return job;
	}

	private Job createRecurringJob(Configuration conf, String input, String output)
			throws IllegalArgumentException, IOException {
		Job job = Job.getInstance(getConf());

		// Define Input and Output Format
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), output);

		SequenceFileInputFormat.setInputPaths(job, new Path(input));
		SequenceFileOutputFormat.setOutputPath(job, new Path(output));

		// set types of Input/Output Objects
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(MapWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MapWritable.class);

		job.setReducerClass(PageRankReducer.class);

		job.setJarByClass(Ex1BMain.class);
		return job;
	}

	public static void main(String[] args) throws Exception {
		String input = args[0];
		String tmpOut = "/tmp/out";
		String output = args[1];

		int exitCode = ToolRunner.run(new Ex1AMain(), new String[] { input, tmpOut });
		exitCode = ToolRunner.run(new Ex1BMain(), new String[] { tmpOut, output });
		System.exit(exitCode);
	}
}
