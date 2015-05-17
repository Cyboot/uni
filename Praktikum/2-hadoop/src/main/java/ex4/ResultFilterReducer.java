package ex4;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ResultFilterReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		int counter;
		if (key.toString().equals("_result")) {
			counter = 1;
			for (Text t : values) {
				context.write(new Text(Integer.toString(counter) + ") "), t);
				counter++;
			}
		}
	}
}
