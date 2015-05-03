package ex3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AvgLikeReducer extends Reducer<NullWritable, LongWritable, NullWritable, Text> {

	@Override
	protected void reduce(NullWritable key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		long nrUsers = 0;
		long sum = 0;
		for (LongWritable val : values) {
			sum += val.get();
			nrUsers++;
		}

		double avg = ((double) sum) / nrUsers;

		String msg = "Avg: " + String.format("%.2f", avg) + " (" + nrUsers + " Users)";

		context.write(key, new Text(msg));
	};
}
