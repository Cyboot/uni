package a;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import common.PageRankCounter;

public class FriendListReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		List<String> friendList = new ArrayList<String>();

		for (Text text : values) {
			friendList.add(text.toString());
		}

		String commaJoinedList = StringUtils.join(friendList, ",");
		context.write(key, new Text(commaJoinedList));

		// increment the User counter
		context.getCounter(PageRankCounter.USER).increment(1);
	}
}
