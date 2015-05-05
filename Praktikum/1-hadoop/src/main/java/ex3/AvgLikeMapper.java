package ex3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AvgLikeMapper extends Mapper<Object, Text, NullWritable, LongWritable> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		// split the line again into Key-Value
		String[] splits = value.toString().split("\t");

		long parsed = Long.parseLong(splits[1]);
		context.write(NullWritable.get(), new LongWritable(parsed));
	}

}
