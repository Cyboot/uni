package ex1;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
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
		Const.PATH_OUTPUT = "out0";

		Job job = Job.getInstance(getConf());

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		SequenceFileOutputFormat
				.setOutputPath(job, new Path(Const.PATH_OUTPUT));

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
				Job job2 = Job.getInstance(getConf());

				// Define Input and Output Format
				job2.setInputFormatClass(SequenceFileInputFormat.class);
				job2.setOutputFormatClass(SequenceFileOutputFormat.class);

				// delete old output directory
				Utils.deleteOutputDirectory(getConf(), "out" + i);

				SequenceFileInputFormat.setInputPaths(job2, new Path("out"
						+ (i - 1)));
				SequenceFileOutputFormat.setOutputPath(job2,
						new Path("out" + i));

				// set types of Input/Output Objects
				job2.setMapOutputKeyClass(Text.class);
				job2.setMapOutputValueClass(Text.class);
				job2.setOutputKeyClass(Text.class);
				job2.setOutputValueClass(Text.class);

				job2.setReducerClass(FriendListReducer2.class);

				job2.setJarByClass(Ex1Main.class);
				job2.setJobName("Ex1:MR2");

				if (job2.waitForCompletion(true)) {
					// SequenceFile.Reader reader = new SequenceFile.Reader(
					// getConf(), SequenceFile.Reader.file(new Path("out"
					// + i + "/part-r-00000")));
					// Text key = new Text();
					// Text val = new Text();
					//
					// while (reader.next(key, val)) {
					// System.err.println(key + "\t" + val);
					// }
					//
					// reader.close();
				}
			}

		}
		return 1;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Ex1Main(), args);
		System.exit(exitCode);
	}
}
