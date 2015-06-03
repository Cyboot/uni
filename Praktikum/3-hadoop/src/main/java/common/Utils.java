package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class Utils {
	public static void printOutputFile(String outputPath) throws IOException {
		System.out.println("\n");
		System.out.println("===========================================");
		System.out.println("==========      Results      ==============");
		System.out.println("===========================================");

		FileSystem fs = FileSystem.get(new Configuration());
		RemoteIterator<LocatedFileStatus> it = fs.listFiles(new Path(outputPath), false);

		while (it.hasNext()) {
			LocatedFileStatus fileStatus = it.next();

			printFile(fileStatus.getPath(), fs);
		}

	}

	private static void printFile(Path path, FileSystem fs) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));
		String line = br.readLine();

		while (line != null) {
			System.out.println(line);
			line = br.readLine();
		}
	}

	public static void deleteOutputDirectory(Configuration conf, String outputPath)
			throws IllegalArgumentException, IOException {
		new Path(outputPath).getFileSystem(conf).delete(new Path(outputPath), true);
	}
}
