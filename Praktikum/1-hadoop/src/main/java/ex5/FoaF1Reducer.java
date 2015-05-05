package ex5;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoaF1Reducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		Set<String> friendSet = new HashSet<String>();
		for (Text friend : values) {
			friendSet.add(friend.toString());
		}

		// combine all friends of the user to a Comma-separated List
		String commaJoinedList = StringUtils.join(friendSet, ',');

		// emit User (Key) with all his friends (Value)
		context.write(key, new Text(commaJoinedList));
	}
}
