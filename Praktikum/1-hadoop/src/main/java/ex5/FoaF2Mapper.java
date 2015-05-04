package ex5;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FoaF2Mapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String friendRaw = value.toString().replace("\t", "");

		String[] friends = friendRaw.split(",");
		String user = friends[0];

		for (String friend : friends) {
			// ignore the actual user
			if (friend.equals(user))
				continue;

			String commonKey = getConcatKey(user, friend);

			context.write(new Text(commonKey), new Text(friendRaw));
		}
	}

	private String getConcatKey(String user, String friend) {
		return friend.compareTo(user) > 0 ? friend + "," + user : user + "," + friend;
	}
}
