package ex5;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoaF3Reducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// join all the Lists
		Set<String> commonFriends = new HashSet<String>();
		for (Text user : values) {
			commonFriends.addAll(Arrays.asList(user.toString().split(",")));
		}

		// exclude the user himself (A is not friend of A)
		commonFriends.remove(key.toString());

		// transform the Set into a comma-seperated List
		String commaJoinedList = StringUtils.join(commonFriends, ',');

		// emit the final result: User (Key) and a List with friends of friends
		context.write(key, new Text(commaJoinedList));

		context.write(key,
				new Text("" + StringUtils.countMatches(commaJoinedList, ",")));
	}
}
