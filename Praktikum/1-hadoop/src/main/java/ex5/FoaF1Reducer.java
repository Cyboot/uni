package ex5;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoaF1Reducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		StringBuilder builder = new StringBuilder();

		for (Text user : values) {
			builder.append(user.toString()).append(",");
		}

		context.write(key, new Text(builder.toString()));
	};

}
