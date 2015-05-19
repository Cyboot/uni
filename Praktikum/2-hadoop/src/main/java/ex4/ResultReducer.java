package ex4;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ResultReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {

		if (key.toString().equals("result")) {
			int counter = 1;
			final int MAX = 25_000;
			for (Text t : values) {
				context.write(new Text(Integer.toString(counter) + ". \t"), t);
				counter++;

				if (counter > MAX)
					break;
			}
		}
	}
}
