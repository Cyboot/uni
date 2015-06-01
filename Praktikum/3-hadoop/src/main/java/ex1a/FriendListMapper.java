package ex1a;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FriendListMapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");
		String subject = splits[0];
		String edge = splits[1];
		String object = splits[2];

		if (splits != null && splits.length >= 3) {
			if (edge.equals("foaf:knows")) {
				context.write(new Text(subject), new Text(object));
			}
		}
	}
}
