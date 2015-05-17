package ex1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserPostMapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");
		String subject = splits[0];
		String edge = splits[1];
		String object = splits[2];


		if (splits != null && splits.length >= 3) {


			if (checkEdge(splits)) {
				// emit the person with value 1
				context.write(new Text(splits[0]), new Text(splits[2]));
			}

		}
	}

	private boolean checkEdge(String[] splits) {
		if (splits[1].equals("foaf:knows")) {
			return true;
		}
		return false;
	}
}
