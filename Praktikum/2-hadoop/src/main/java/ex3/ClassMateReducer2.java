package ex3;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ClassMateReducer2 extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {

		StringBuilder sb = new StringBuilder();

		for (Text text : values) {
			sb.append(text.toString()).append(", ");
		}
		sb.setLength(sb.length() - 2);

		if (StringUtils.countMatches(sb.toString(), ',') >= 1)
			context.write(key, new Text(sb.toString()));
	}
}
