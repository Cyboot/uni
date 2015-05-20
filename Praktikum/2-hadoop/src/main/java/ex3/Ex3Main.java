package ex3;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
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
		if (mapper != null)
			job.setMapperClass(mapper);
		if (reducer != null)
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


	private static class Job1 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			if (args.length != 2) {
				System.out.println("Usage: Ex1Main <input dir> <output dir>");
				System.exit(-1);
			}
			Const.PATH_INPUT = args[0];
			Const.PATH_OUTPUT = args[1];


			Job job = Job.getInstance(getConf());

			setIOPaths(job, Const.PATH_INPUT, Const.PATH_OUTPUT);
			setMapRed(job, ClassMateMapper.class, ClassMateReducer.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);

			return job.waitForCompletion(true) ? 0 : 1;
		}
	}

	private static class Job2 extends Configured implements Tool {
		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());

			setIOPaths(job, Const.PATH_OUTPUT, "out2");
			setMapRed(job, null, ClassMateReducer2.class);
			job.setInputFormatClass(SequenceFileInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			if (job.waitForCompletion(true)) {
				// print the output file
				Utils.printOutputFile("out2");
				return 0;
			}
			return 1;
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
