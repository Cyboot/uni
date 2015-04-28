package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		for (String string : args) {
			System.out.println("arg: " + string);
		}
		/*
		 * Validate that two arguments were passed from the command line.
		 */
		if (args.length != 4) {
			System.out
					.printf("Usage: WordCountDriver <input dir> <output dir> <num of reducers>\n");
			System.exit(-1);
		}
		System.out.println("Arguments");

		// Configuration processed by ToolRunner
		Configuration conf = getConf();
		// Notify Hadoop that application uses GenericOptionsParser
		// This is not required but prevents that a warning is printed during
		// execution
		conf.set("mapreduce.client.genericoptionsparser.used", "true");

		// Create a Job using the processed conf
		Job job = Job.getInstance(conf);

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		// Define Map Output Classes (Key, Value)
		// We don't have to define this as it is the same as the Job Output.
		// But if it is not the same, you have to define it!
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		// Define Job Output Classes (Key, Value)
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// Set Mapper and Reducer Class
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		// For WordCount we can use the Reducer class also as Combiner class
		job.setCombinerClass(WordCountReducer.class);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(Integer.parseInt(args[3]));

		/*
		 * Specify the jar file that contains your driver, mapper, and reducer.
		 * Hadoop will transfer this jar file to nodes in your cluster running
		 * mapper and reducer tasks.
		 */
		job.setJarByClass(WordCountDriver.class);

		/*
		 * Specify an easily-decipherable name for the job. This job name will
		 * appear in reports and logs.
		 */
		job.setJobName("WordCount");

		/*
		 * Start the MapReduce job and wait for it to finish. If it finishes
		 * successfully, return 0. If not, return 1.
		 */
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new WordCountDriver(), args);
		System.exit(exitCode);
	}
}
