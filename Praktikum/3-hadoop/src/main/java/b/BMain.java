package b;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import a.AMain;

import common.Const;
import common.Utils;

public class BMain extends Configured implements Tool {
	private static final double	MIN_DELTA		= 1e-6;

	private int					nrUsers			= 1;
	private boolean				useDelta;

	private Map<String, Double>	previousValues	= new HashMap<String, Double>();


	public BMain(int nrUsers) {
		this.nrUsers = nrUsers;
	}

	public void useDeltaFinish() {
		useDelta = true;
	}

	@Override
	public int run(String[] args) throws Exception {
		Const.PATH_INPUT = args[0];
		Const.PATH_OUTPUT = args[1];
		int maxIterations = Integer.parseInt(args[2]);

		if (useDelta) {
			maxIterations = 20;
		}

		getConf().setInt("NR_USER", nrUsers);

		// create the initial Job
		Job job = createInitalJob(getConf(), Const.PATH_INPUT, "/tmp/out-0");

		String output = null;
		String input = null;

		double delta = 0;
		int lastIteration = 0;
		if (job.waitForCompletion(true)) {

			// run the recurring jobs
			for (int i = 1; i <= maxIterations; i++) {
				input = "/tmp/out-" + (i - 1);
				output = "/tmp/out-" + i;

				Job tmpJob = createRecurringJob(getConf(), input, output);
				tmpJob.setJobName("PageRank: Job" + i);


				tmpJob.waitForCompletion(true);

				delta = getAverageDelta(tmpJob);
				if (delta < MIN_DELTA && useDelta) {
					lastIteration = i;
					break;
				}
			}
		}

		// create the format Job for prettier Output
		Job formatJob = createFormatJob(getConf(), input, Const.PATH_OUTPUT);

		if (formatJob.waitForCompletion(true)) {
			Utils.printOutputFile(Const.PATH_OUTPUT);

			if (useDelta) {
				System.out.println();
				System.out.println("Break after " + lastIteration + " iteration.");
				System.out.println("Delta between last two PageRank-Runs: " + delta);
			}

			return 0;
		}

		return 1;
	}

	/**
	 * calculate the differences (deltas) between the pageranks of two runs
	 */
	private double getAverageDelta(Job tmpJob) throws IOException {
		double sumPagerankDelta = 0;
		boolean firstRun = true;

		// get the Counter group "USER"
		CounterGroup counters = tmpJob.getCounters().getGroup("USER");
		for (Counter counter : counters) {
			String name = counter.getName();
			// convert from long to double (Countervalue to pagerank)
			double pageRank = Double.longBitsToDouble(counter.getValue());

			if (previousValues.containsKey(name)) {
				Double previousRank = previousValues.get(name);

				sumPagerankDelta += Math.abs(pageRank - previousRank);
				firstRun = false;
			}
			previousValues.put(name, pageRank);
		}
		int size = previousValues.size();

		if (firstRun)
			return Double.MAX_VALUE;
		else
			return sumPagerankDelta / size;
	}

	/**
	 * create the formatting Job
	 */
	private Job createFormatJob(Configuration conf, String input, String output)
			throws IllegalArgumentException, IOException {
		Job job = createRecurringJob(conf, input, output);

		// use TextOutputFormat for the last Job
		job.setOutputFormatClass(TextOutputFormat.class);

		SequenceFileInputFormat.setInputPaths(job, new Path(input));
		TextOutputFormat.setOutputPath(job, new Path(output));

		// use FormatReduce and Text as OutputValue
		job.setReducerClass(FormatReducer.class);
		job.setOutputValueClass(Text.class);

		return job;
	}

	/**
	 * create the initial Job, it uses the TextFileInformat (ie the output from
	 * {@link a.AMain})
	 */
	private Job createInitalJob(Configuration conf, String input, String output) throws IOException {
		Job job = createRecurringJob(conf, input, output);

		// use TextInputFormat for initial job
		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.setInputPaths(job, new Path(input));

		job.setMapperClass(InitialMapper.class);

		return job;
	}


	/**
	 * create the recurring Job
	 */
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
		job.setMapOutputValueClass(MapWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MapWritable.class);

		job.setReducerClass(PageRankReducer.class);

		job.setJarByClass(BMain.class);
		return job;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Usage: BMain <input dir> <output dir> <number of iteration>");
			System.exit(-1);
		}

		String input = args[0];
		String tmpOut = "/tmp/out";
		String output = args[1];
		String iterations = args[2];

		AMain toolRunA = new AMain(false);
		int exitCode = ToolRunner.run(toolRunA, new String[] { input, tmpOut });
		exitCode = ToolRunner.run(new BMain(toolRunA.getNrUsers()), new String[] { tmpOut, output,
				iterations });
		System.exit(exitCode);
	}
}
