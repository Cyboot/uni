package ex1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import common.KeyValueWritable;

public class FriendListMapper extends Mapper<Object, Text, Text, KeyValueWritable> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");
		String subject = splits[0];
		String edge = splits[1];
		String object = splits[2];

		if (splits != null && splits.length >= 3) {
			if (edge.equals("foaf:knows")) {
				KeyValueWritable valueOut1 = new KeyValueWritable(FriendListReducer.OUTGOING, object);
				KeyValueWritable valueOut2 = new KeyValueWritable(FriendListReducer.INCOMING, subject);
				
				context.write(new Text(subject), valueOut1);
				context.write(new Text(object), valueOut2);
			}
		}
	}
}
