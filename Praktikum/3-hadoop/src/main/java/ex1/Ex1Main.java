package ex1;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
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
		Const.PATH_OUTPUT = "/tmp/out-0";

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
		job.setOutputValueClass(Text.class);

		job.setMapperClass(FriendListMapper.class);
		job.setReducerClass(FriendListReducer.class);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("Ex1:MR1");

		if (job.waitForCompletion(true)) {

			for (int i = 1; i <= 5; i++) {
				String input = "/tmp/out-" + (i - 1);
				String output = "/tmp/out-" + i;

				Job tmpJob = createRecurringJob(getConf(), input, output);
				tmpJob.setJobName("PageRank: Job" + i);

				if (tmpJob.waitForCompletion(true)) {
					SequenceFile.Reader reader = new SequenceFile.Reader(getConf(),
							SequenceFile.Reader.file(new Path("/tmp/out-" + i + "/part-r-00000")));
					Text key = new Text();
					Text val = new Text();

					while (reader.next(key, val)) {
						System.err.println(key + "\t" + val);
					}

					reader.close();
				}
			}

		}
		return 1;
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
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setReducerClass(FriendListReducer2.class);

		job.setJarByClass(Ex1Main.class);
		return job;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex1Main(), args);
		System.exit(exitCode);
	}
}
