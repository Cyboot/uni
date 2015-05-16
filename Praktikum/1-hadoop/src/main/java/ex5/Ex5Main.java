package ex5;

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


public class Ex5Main {
	private static final String	OUTPUT_PATH2	= "/out2";
	private static final String	OUTPUT_PATH3	= "/out3";

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

	private static void setIOPaths(Job job, String pathInput, String pathOutput) throws Exception {
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
			setMapRed(job, FoaF1Mapper.class, FoaF1Reducer.class);

			return runJob(job, Const.PATH_OUTPUT);
		}
	}

	private static class Job2 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());

			setIOPaths(job, Const.PATH_OUTPUT, OUTPUT_PATH2);
			setMapRed(job, FoaF2Mapper.class, FoaF2Reducer.class);

			return runJob(job, OUTPUT_PATH2);
		}
	}

	private static class Job3 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());

			setIOPaths(job, OUTPUT_PATH2, OUTPUT_PATH3);
			setMapRed(job, FoaF3Mapper.class, FoaF3Reducer.class);

			return runJob(job, OUTPUT_PATH3);
		}
	}

	public static void main(String[] args) throws Exception {
		int exitCode = 0;

		/**
		 * <pre>
		 * Outputs a list of friends for each user:
		 * A: B, C, X
		 * B: A, Y,
		 * X: Z
		 * </pre>
		 */
		exitCode += ToolRunner.run(new Job1(), args);

		/**
		 * <pre>
		 * Outputs a common friends for friends:
		 * Mapper				->	Reducer
		 * 
		 * AB: C, X, Y 			->	A: C, X, Y		B: C, X, Y
		 * AX: Z				->	A: Z			X: Z
		 * </pre>
		 */
		exitCode += ToolRunner.run(new Job2(), args);

		/**
		 * <pre>
		 * Simple 'Group by'-Operation
		 * A: C, X, Y  +  A: Z	-> A: C, X, Y, Z
		 * </pre>
		 */
		exitCode += ToolRunner.run(new Job3(), args);

		exitCode = Math.min(exitCode, 1);
		System.exit(exitCode);
	}
}
