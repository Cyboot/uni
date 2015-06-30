package ex3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;

/**
 * Usage example: java -cp
 * /opt/cloudera/parcels/CDH-4.6.0-1.cdh4.6.0.p0.26/lib/pig/pig.jar:`hadoop
 * classpath`:closure-1.jar dbis.TransitiveClosure /data/sib/sibdataset2000.nt
 */
public class TransitiveClosure {
	private String		currentIn;
	private String		currentOut;
	private String		orignalIn;
	private final int	maxIterations;
	private int			currentIterationIndex;
	private PigServer	pigServer;


	public TransitiveClosure(String input, int iterations) throws ExecException {
		pigServer = new PigServer(ExecType.LOCAL);

		currentIn = input;
		orignalIn = input;
		currentOut = "tmp-0";
		this.maxIterations = iterations;
	}

	// Do the steps iterations-times
	public void start() throws IOException {
		long filesize = -1;

		boolean didBreak = false;
		int degreeOfSeperaton = 1;
		for (currentIterationIndex = 0; currentIterationIndex < maxIterations; currentIterationIndex++) {
			System.out.printf("Current Iteration: %d\n", currentIterationIndex);
			long fileSizeNew = doIteration();

			if (fileSizeNew == filesize) {
				didBreak = true;
				break;
			}
			filesize = fileSizeNew;
			degreeOfSeperaton++;
		}

		System.out.println("==========================================");
		System.out.println("============    RESULTS    ===============");
		System.out.println("==========================================");


		if (didBreak) {
			System.out.println("The maximum degree of separation for this Dataset is: "
					+ degreeOfSeperaton);
		} else {
			System.out.println("Reached the maximum number of Iterations");
			System.out.println("The maximum degree of seperation is over: " + degreeOfSeperaton);
		}

	}

	private long doIteration() throws IOException {
		FileUtils.deleteDirectory(new File(currentOut));
		executeEmbeddedScript();
		long fileLength = getFileSize();

		if (currentIterationIndex != 0) {
			deleteFilesFromInputDirectory();
		}

		currentIn = currentOut;
		currentOut = "tmp-" + (currentIterationIndex + 1);

		return fileLength;
	}

	// Execute the embedded script transforming the contents of in/* to out/*.
	private void executeEmbeddedScript() throws IOException {
		PigServer pigServer = new PigServer(ExecType.LOCAL);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("inputOrigin", orignalIn);
		parameters.put("inputIterate", currentIn);
		pigServer.registerScript(getClass().getResourceAsStream("/closure.pig"), parameters);

		pigServer.store("distincted", currentOut, "PigStorage(' ')");
	}

	/**
	 * Returns the sum of bytes that are occupied by the files in the given
	 * directory.
	 */
	private long getFileSize() throws IOException {
		String[] files = pigServer.listPaths(currentOut);
		long sum = 0;
		for (String s : files) {
			if (!s.contains("_")) {
				sum += pigServer.fileSize(s);
			}
		}
		return sum;
	}

	/**
	 * remove tmp output to save space
	 */
	private void deleteFilesFromInputDirectory() throws IOException {
		String[] contents = pigServer.listPaths(currentIn);
		for (String s : contents) {
			if (!s.contains("_")) {
				pigServer.deleteFile(s);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		final int ITERATIONS = 7;
		if (args.length < 1) {
			System.err.println("Usage: TransitiveClosure <input>");
			System.exit(1);
		}

		String inputPath = "tmp-in/data.nt";
		FileUtils.deleteDirectory(new File("tmp-in"));

		try {
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://sydney.informatik.privat:8020");
			conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
			FileSystem fs = FileSystem.get(conf);

			System.out.println("Downloading File " + args[0] + " from HDFS...");

			fs.copyToLocalFile(false, new Path(args[0]), new Path(inputPath));
		} catch (IOException e) {
			e.printStackTrace();
		}


		TransitiveClosure transitiveClosure = new TransitiveClosure(inputPath, ITERATIONS);
		transitiveClosure.start();
	}
}
