package ex3;

import java.io.IOException;
import java.util.HashMap;

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
	private final int	maxIterations;
	private int			currentIterationIndex;
	private PigServer	pigServer;


	public TransitiveClosure(String input, int iterations) throws ExecException {
		pigServer = new PigServer(ExecType.LOCAL);

		currentIn = input;
		currentOut = "tmp-0";
		this.maxIterations = iterations;
	}

	// Do the steps iterations-times
	public void start() throws IOException {
		long filesize = -1;

		boolean didBreak = false;
		for (currentIterationIndex = 0; currentIterationIndex < maxIterations; currentIterationIndex++) {
			long fileSizeNew = doIteration();

			if (fileSizeNew == filesize) {
				didBreak = true;
				break;
			}
			filesize = fileSizeNew;
		}

		System.out.println("==========================================");
		System.out.println("============    RESULTS    ===============");
		System.out.println("==========================================");

		long degreeOfSeperaton = currentIterationIndex + 2;

		if (didBreak) {
			System.out.println("The maximum degree of separation for this Dataset is: "
					+ degreeOfSeperaton);
		} else {
			System.out.println("Reached the maximum number of Iterations");
			System.out.println("The maximum degree of seperation is over: " + degreeOfSeperaton);
		}

	}

	private long doIteration() throws IOException {
		executeEmbeddedScript();
		long fileLength = getFileSize();

		// if (currentIterationIndex != 0) {
		// deleteFilesFromInputDirectory();
		// }

		currentIn = currentOut;
		currentOut = "tmp-" + (currentIterationIndex + 1);
		pigServer.deleteFile(currentOut);

		return fileLength;
	}

	// Execute the embedded script transforming the contents of in/* to out/*.
	private void executeEmbeddedScript() throws IOException {
		PigServer pigServer = new PigServer(ExecType.LOCAL);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("input", currentIn);
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

		TransitiveClosure transitiveClosure = new TransitiveClosure(args[0], ITERATIONS);
		transitiveClosure.start();
	}
}
