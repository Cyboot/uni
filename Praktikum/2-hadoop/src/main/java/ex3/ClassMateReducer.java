package ex3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ClassMateReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		String result = "";

		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			Text next = it.next();

			result += next.toString();

			// seperate the values
			if (it.hasNext())
				result += ";;;";
		}

		context.write(key, new Text(result));
	}
}
