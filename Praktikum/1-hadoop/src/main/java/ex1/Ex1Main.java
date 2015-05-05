package ex1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.Utils;

public class Ex1Main extends Configured implements Tool {
	// User with likes
	public static final String	RELEVANT_USER	= "sibu:u107";

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		String relevantUser = RELEVANT_USER;
		if (args.length > 0) {
			relevantUser = args[0];
		}
		// set the relevant user into the config
		conf.set("RELEVANT_USER", relevantUser);

		Job job = Job.getInstance(conf);

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		FileOutputFormat.setOutputPath(job, new Path(Const.PATH_OUTPUT));

		// set types of Input/Output Objects
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		// Set Mapper and Reducer Class (the identity Reducer is fine here )
		job.setMapperClass(FriendMapper.class);
		job.setReducerClass(Reducer.class);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(1);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex1");

		if (job.waitForCompletion(true)) {
			// print the output file
			Utils.printOutputFile(Const.PATH_OUTPUT);

			return 0;
		}
		return 1;
	}


	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex1Main(), args);
		System.exit(exitCode);
	}
}
