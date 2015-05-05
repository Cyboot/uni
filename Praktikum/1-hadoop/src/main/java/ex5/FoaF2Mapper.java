package ex5;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FoaF2Mapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		// split the current line from TextOutputFormat
		String friendList = value.toString().replace("\t", ",");

		String[] friends = friendList.split(",");
		String user = friends[0];

		for (String friend : friends) {
			// ignore the actual user
			if (friend.equals(user))
				continue;

			String compositeKey = getConcatKey(user, friend);

			// emit composite Key (Key) and all common friends as List (Value)
			// Example A: B, C -> AB: A, B, C & AC: A, B, C
			context.write(new Text(compositeKey), new Text(friendList));
		}
	}

	/**
	 * create a concat key out of two friends
	 * 
	 * @return the composite key in alphabetical order
	 */
	private String getConcatKey(String user, String friend) {
		return friend.compareTo(user) > 0 ? friend + "," + user : user + "," + friend;
	}
}
