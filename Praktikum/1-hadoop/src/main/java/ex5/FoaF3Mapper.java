package ex5;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FoaF3Mapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String[] splits = value.toString().split("\t");

		context.write(new Text(splits[0]), new Text(splits[1]));
	}
}
