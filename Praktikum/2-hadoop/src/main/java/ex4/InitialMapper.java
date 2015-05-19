package ex4;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InitialMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");

		if (checkEdge(splits)) {
			// emit Friends (in bidirectional direction)
			// context.write(new Text(splits[0]), new Text(splits[2]));
			context.write(new Text(splits[2]), new Text(splits[0]));
		}
	}

	private boolean checkEdge(String[] splits) {
		if (splits.length < 3)
			return false;

		if (splits[1].equals("foaf:knows")) {
			return true;
		}
		return false;
	}
}
