package ex4;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PopularReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

	private int	threshold;

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {

		long sum = 0;
		for (LongWritable val : values) {
			sum += val.get();
		}

		// only write if User is above threshold
		if (sum >= threshold)
			context.write(key, new LongWritable(sum));
	};

	@Override
	protected void setup(Reducer<Text, LongWritable, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
		threshold = context.getConfiguration().getInt("user.threshold", Ex4Main.DEFAULT_THRESHOLD);
	}
}
