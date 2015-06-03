package c;

import org.apache.hadoop.util.ToolRunner;

import a.AMain;
import b.BMain;

public class CMain {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: Main <input dir> <output dir>");
			System.exit(-1);
		}

		String input = args[0];
		String tmpOut = "/tmp/out";
		String output = args[1];

		AMain toolA = new AMain(false);
		int exitCode = ToolRunner.run(toolA, new String[] { input, tmpOut });

		BMain toolB = new BMain(toolA.getNrUsers());
		toolB.useDeltaFinish();

		exitCode = ToolRunner.run(toolB, new String[] { tmpOut, output, "0" });

		System.exit(exitCode);
	}
}
