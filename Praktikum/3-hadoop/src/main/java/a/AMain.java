package a;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import common.Const;
import common.PageRankCounter;
import common.Utils;

public class AMain extends Configured implements Tool {
	private boolean	verbose;
	private int		nrUsers;

	public AMain() {
		this(true);
	}

	public AMain(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 2 || args.length > 3) {
			System.out.println("Usage: AMain <input dir> <output dir> [verbose]");
			System.exit(-1);
		}
		Const.PATH_INPUT = args[0];
		Const.PATH_OUTPUT = args[1];
		if (args.length > 2)
			verbose = "true".equals(args[2]);


		Job job = Job.getInstance(getConf());

		// Define Input and Output Format
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// delete old output directory
		Utils.deleteOutputDirectory(getConf(), Const.PATH_OUTPUT);

		FileInputFormat.setInputPaths(job, new Path(Const.PATH_INPUT));
		TextOutputFormat.setOutputPath(job, new Path(Const.PATH_OUTPUT));

		// set types of Input/Output Objects
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(FriendListMapper.class);
		job.setReducerClass(FriendListReducer.class);

		job.setJarByClass(AMain.class);
		job.setJobName("Ex1:MR1");

		if (job.waitForCompletion(true)) {
			if (verbose)
				Utils.printOutputFile(Const.PATH_OUTPUT);

			Counter counter = job.getCounters().findCounter(PageRankCounter.USER);
			nrUsers = (int) counter.getValue();

			return 0;
		}
		return 1;
	}

	public int getNrUsers() {
		return nrUsers;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new AMain(), args);
		System.exit(exitCode);
	}
}
