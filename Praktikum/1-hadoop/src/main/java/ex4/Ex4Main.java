package ex4;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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

public class Ex4Main extends Configured implements Tool {
	public static final int	DEFAULT_THRESHOLD	= 40;


	@Override
	public int run(String[] args) throws Exception {
		int threshold = DEFAULT_THRESHOLD;
		if (args.length >= 1) {
			threshold = Integer.parseInt(args[0]);
		}
		getConf().setInt("user.threshold", threshold);
		Job job = Job.getInstance(getConf());

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		FileOutputFormat.setOutputPath(job, new Path(Const.PATH_OUTPUT));

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		// Set Mapper and Reducer Class
		job.setMapperClass(PopularMapper.class);
		job.setReducerClass(PopularReducer.class);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(1);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex4");

		if (job.waitForCompletion(true)) {
			// print the output file
			Utils.printOutputFile(Const.PATH_OUTPUT);

			return 0;
		}
		return 1;
	}


	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex4Main(), args);
		System.exit(exitCode);
	}
}
