package ex3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
import ex2.Ex2Main;

public class Ex3Main extends Configured implements Tool {
	private final String	OUTPUT_PATH2	= "/out2";

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		Job job = Job.getInstance(conf);

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// delete outputpath2
		Utils.deleteOutputDirectory(conf, OUTPUT_PATH2);

		// use the output path of previous job as Input
		FileInputFormat.setInputPaths(job, new Path(Const.PATH_OUTPUT));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH2));

		// set types of Input/Output Objects
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		// Set Mapper and Reducer Class
		job.setMapperClass(AvgLikeMapper.class);
		job.setReducerClass(AvgLikeReducer.class);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(1);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex3");

		if (job.waitForCompletion(true)) {
			// print the output file
			Utils.printOutputFile(OUTPUT_PATH2);

			return 0;
		}
		return 1;
	}


	public static void main(String[] args) throws Exception {
		// run the Ex2-Job before (likes per User)
		int exitCode = ToolRunner.run(new Ex2Main(), args);

		if (exitCode == 0) {
			// run the second Job
			exitCode = ToolRunner.run(new Ex3Main(), args);
		}

		System.exit(exitCode);
	}
}
