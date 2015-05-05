package ex2;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserLikeMapper extends Mapper<Object, Text, Text, LongWritable> {
	private static final LongWritable	ONE	= new LongWritable(1);

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");

		if (splits != null && splits.length >= 3) {
			// check if the predicate is a sib:like edge
			if (checkEdge(splits)) {
				// emit the person with value 1
				context.write(new Text(splits[0]), ONE);
			}

		}
	}

	private boolean checkEdge(String[] splits) {
		if (splits[1].equals("sib:like")) {
			return true;
		}
		return false;
	}
}
