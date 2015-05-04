package ex5;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

import ex1.Ex1Main;

public class Ex5Main extends Configured {
	private static final String	OUTPUT_PATH2	= "/out2";
	private static final String	OUTPUT_PATH3	= "/out3";


	private static class Job1 extends Configured implements Tool {

		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());
			getConf().set("mapred.job.tracker", "local");

			// Define Input and Output Format
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			// delete old output directory
			Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

			FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
			FileOutputFormat.setOutputPath(job, new Path(Const.PATH_OUTPUT));

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			// Set Mapper and Reducer Class
			job.setMapperClass(FoaF1Mapper.class);
			job.setReducerClass(FoaF1Reducer.class);

			// Set the Number of Reduce Tasks
			job.setNumReduceTasks(1);

			job.setJarByClass(Ex1Main.class);
			job.setJobName("Ex5");

			if (job.waitForCompletion(true)) {
				// print the output file
				Utils.printOutputFile(Const.PATH_OUTPUT);

				return 0;
			}
			return 1;
		}
	}

	private static class Job2 extends Configured implements Tool {

		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());
			getConf().set("mapred.job.tracker", "local");

			// Define Input and Output Format
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			// delete old output directory
			Utils.deleteOutputDirectory(getConf(), OUTPUT_PATH2);

			FileInputFormat.setInputPaths(job, new Path(Const.PATH_OUTPUT));
			FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH2));

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			// Set Mapper and Reducer Class
			job.setMapperClass(FoaF2Mapper.class);
			job.setReducerClass(FoaF2Reducer.class);

			// Set the Number of Reduce Tasks
			job.setNumReduceTasks(1);

			job.setJarByClass(Ex1Main.class);
			job.setJobName("Ex5");

			if (job.waitForCompletion(true)) {
				// print the output file
				Utils.printOutputFile(OUTPUT_PATH2);

				return 0;
			}
			return 1;
		}
	}

	private static class Job3 extends Configured implements Tool {

		@Override
		public int run(String[] args) throws Exception {
			Job job = Job.getInstance(getConf());
			getConf().set("mapred.job.tracker", "local");

			// Define Input and Output Format
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			// delete old output directory
			Utils.deleteOutputDirectory(getConf(), OUTPUT_PATH3);

			FileInputFormat.setInputPaths(job, new Path(OUTPUT_PATH2));
			FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH3));

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			// Set Mapper and Reducer Class
			job.setMapperClass(FoaF3Mapper.class);
			job.setReducerClass(FoaF3Reducer.class);

			// Set the Number of Reduce Tasks
			job.setNumReduceTasks(1);

			job.setJarByClass(Ex1Main.class);
			job.setJobName("Ex5");

			if (job.waitForCompletion(true)) {
				// print the output file
				Utils.printOutputFile(OUTPUT_PATH3);

				return 0;
			}
			return 1;
		}
	}


	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Job1(), args);
		exitCode = ToolRunner.run(new Job2(), args);
		exitCode = ToolRunner.run(new Job3(), args);
		System.exit(exitCode);
	}
}
