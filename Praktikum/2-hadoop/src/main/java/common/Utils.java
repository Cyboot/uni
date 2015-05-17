package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Utils {
	public static void printOutputFile(String outputPath) throws IOException {
		Path pt = new Path(outputPath + "/part-r-00000");
		FileSystem fs = FileSystem.get(new Configuration());
		BufferedReader br = new BufferedReader(new InputStreamReader(
				fs.open(pt)));
		String line = br.readLine();
		while (line != null) {
			System.out.println(line);
			line = br.readLine();
		}
	}

	public static void deleteOutputDirectory(Configuration conf,
			String outputPath) throws IllegalArgumentException, IOException {
		new Path(outputPath).getFileSystem(conf).delete(new Path(outputPath),
				true);
	}
}
