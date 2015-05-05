package ex5;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoaF2Reducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		// split the composite key
		String user1 = key.toString().split(",")[0];
		String user2 = key.toString().split(",")[1];

		// create a set for all commonFriends of user1 and user2 (the Set
		// removes dublicates)
		Set<String> commonFriends = new HashSet<String>();
		for (Text user : values) {
			commonFriends.addAll(Arrays.asList(user.toString().split(",")));
		}

		// transform the Set into a comma-seperated List
		String commaJoinedList = StringUtils.join(commonFriends, ',');

		// emit the comma-seperated List of common Friends for both users
		context.write(new Text(user1), new Text(commaJoinedList));
		context.write(new Text(user2), new Text(commaJoinedList));
	}

}
