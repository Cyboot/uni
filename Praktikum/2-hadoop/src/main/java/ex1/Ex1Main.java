package ex1;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

public class Ex1Main extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: Ex1Main <input dir> <output dir>");
			System.exit(-1);
		}
		Const.PATH_INPUT = args[0];
		Const.PATH_OUTPUT = args[1];


		Job job = Job.getInstance(getConf());

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		SequenceFileOutputFormat.setOutputPath(job, new Path(Const.PATH_OUTPUT));

		// set types of Input/Output Objects
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(UserPostMapper.class);
		job.setReducerClass(UserPostReducer.class);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex1:MR1");

		if (job.waitForCompletion(true)) {
			Job job2 = Job.getInstance(getConf());

			// Define Input and Output Format
			job2.setInputFormatClass(SequenceFileInputFormat.class);
			job2.setOutputFormatClass(TextOutputFormat.class);

			// delete old output directory
			Utils.deleteOutputDirectory(getConf(), "out2");

			SequenceFileInputFormat.setInputPaths(job2, new Path(Const.PATH_OUTPUT));
			TextOutputFormat.setOutputPath(job2, new Path("out2"));

			// set types of Input/Output Objects
			job2.setMapOutputKeyClass(Text.class);
			job2.setMapOutputValueClass(IntWritable.class);
			job2.setOutputKeyClass(Text.class);
			job2.setOutputValueClass(IntWritable.class);

			job2.setReducerClass(IntSumReducer.class);

			job2.setJarByClass(Ex1Main.class);
			job2.setJobName("Ex1:MR2");

			if (job2.waitForCompletion(true)) {
				Utils.printOutputFile("out2");
				return 0;
			}
		}
		return 1;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex1Main(), args);
		System.exit(exitCode);
	}
}
