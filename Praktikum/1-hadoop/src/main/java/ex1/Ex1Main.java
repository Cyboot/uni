package ex1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
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

public class Ex1Main extends Configured implements Tool {
	public static final String	RELEVANT_USER	= "sibu:u107";

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		conf.set("RELEVANT_USER", RELEVANT_USER);
		conf.set("mapreduce.client.genericoptionsparser.used", "true");

		Job job = Job.getInstance(conf);

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// delete old output directory
		new Path("/out/").getFileSystem(getConf()).delete(new Path("/out/"), true);

		FileInputFormat.setInputPaths(job, new Path("/input200"));
		FileOutputFormat.setOutputPath(job, new Path("/out"));

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		// Set Mapper and Reducer Class
		job.setMapperClass(FriendMapper.class);
		job.setReducerClass(FriendReducer.class);

		// Set the Number of Reduce Tasks
		job.setNumReduceTasks(1);

		job.setJarByClass(Ex1Main.class);
		job.setJobName("WordCount");

		if (job.waitForCompletion(true)) {
			// print the output file
			printOutputFile();

			return 0;
		}
		return 1;
	}

	private void printOutputFile() throws IOException {
		Path pt = new Path("/out/part-r-00000");
		FileSystem fs = FileSystem.get(new Configuration());
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
		String line = br.readLine();
		while (line != null) {
			System.out.println(line);
			line = br.readLine();
		}
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex1Main(), args);
		System.exit(exitCode);
	}
}
