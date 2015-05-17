package ex3;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

import ex1.Ex1Main;

public class Ex3Main {

	@SuppressWarnings("rawtypes")
	private static void setMapRed(Job job, Class<? extends Mapper> mapper,
			Class<? extends Reducer> reducer) {
		// local JobTracker (for DEBUGGING)
		if (Const.DEBUG)
			job.getConfiguration().set("mapred.job.tracker", "local");

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// set key/values types
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Set Mapper and Reducer Class
		job.setMapperClass(mapper);
		job.setReducerClass(reducer);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(1);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex5");
	}

	private static void setIOPaths(Job job, String pathInput, String pathOutput)
			throws Exception {
		// delete old output directory
		Utils.deleteOutputDirectory(job.getConfiguration(), pathOutput);

		FileInputFormat.setInputPaths(job, new Path(pathInput));
		FileOutputFormat.setOutputPath(job, new Path(pathOutput));
	}

	private static int runJob(Job job, String pathOutput) throws Exception {
		if (job.waitForCompletion(true)) {
			// print the output file
			Utils.printOutputFile(pathOutput);
			return 0;
		}
		return 1;
	}

	private static class Job1 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());

			setIOPaths(job, Const.PATH_INPUT, Const.PATH_OUTPUT);
			setMapRed(job, ClassMateMapper.class, ClassMateReducer.class);

			return runJob(job, Const.PATH_OUTPUT);
		}
	}

	private static class Job2 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());

			setIOPaths(job, Const.PATH_OUTPUT, "/out2");
			setMapRed(job, ClassMateMapper2.class, ClassMateReducer2.class);

			return runJob(job, "/out2");
		}
	}

	public static void main(String[] args) throws Exception {
		int exitCode = 0;

		exitCode += ToolRunner.run(new Job1(), args);
		exitCode += ToolRunner.run(new Job2(), args);

		exitCode = Math.min(exitCode, 1);
		System.exit(exitCode);
	}
}
