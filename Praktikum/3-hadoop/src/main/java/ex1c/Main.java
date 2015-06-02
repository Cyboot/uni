package ex1c;

import org.apache.hadoop.util.ToolRunner;

import ex1a.Ex1AMain;
import ex1b.Ex1BMain;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: Main <input dir> <output dir>");
			System.exit(-1);
		}

		String input = args[0];
		String tmpOut = "/tmp/out";
		String output = args[1];

		Ex1AMain toolA = new Ex1AMain(false);
		int exitCode = ToolRunner.run(toolA, new String[] { input, tmpOut });

		Ex1BMain toolB = new Ex1BMain(toolA.getNrUsers());
		toolB.useDeltaFinish();

		exitCode = ToolRunner.run(toolB, new String[] { tmpOut, output, "20" });

		System.exit(exitCode);
	}
}
