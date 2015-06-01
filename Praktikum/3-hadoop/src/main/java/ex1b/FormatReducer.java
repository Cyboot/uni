package ex1b;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FormatReducer extends Reducer<Text, MapWritable, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<MapWritable> values, Context context)
			throws IOException, InterruptedException {
		double sumPageRank = 0;

		for (MapWritable map : values) {
			if (map.containsKey(new Text("nrFriends"))) {
				int nrOfFriends = ((IntWritable) map.get(new Text("nrFriends"))).get();
				double pageRank = ((DoubleWritable) map.get(new Text("pageRank"))).get();

				sumPageRank += pageRank / nrOfFriends;
			}
		}

		String valueOUT = String.format("PageRank: %.3f", sumPageRank);

		context.write(new Text(key.toString() + "  "), new Text(valueOUT));
	}
}
