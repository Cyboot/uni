package ex1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendListReducer2 extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		double sumPageRank = 0;

		List<String> friendList = null;

		for (Text text : values) {
			String[] str = text.toString().split("->");

			if (str[0].equals(FriendListReducer.PAGE_RANK)) {
				String[] split = str[1].split(" > ");

				int nrOfFriends = Integer.parseInt(split[1]);
				double pageRank = Double.parseDouble(split[2]);

				sumPageRank += pageRank / nrOfFriends;
			}
			if (str[0].equals(FriendListReducer.FRIENDLIST) && str.length > 1) {
				friendList = Arrays.asList(str[1].split(","));
			}
		}

		// skip user with no friends
		if (friendList == null)
			return;

		int nrOutgoingFriends = friendList.size();
		String valueOut = FriendListReducer.PAGE_RANK + "->" + key.toString()
				+ " > " + nrOutgoingFriends + " > " + sumPageRank;

		// propagate Nr of friends for key to all friends
		for (String user : friendList) {
			context.write(new Text(user), new Text(valueOut));
		}

		// also emit the list of friends for the user
		String commaJoinedList = StringUtils.join(friendList, ",");
		context.write(key, new Text(FriendListReducer.FRIENDLIST + "->"
				+ commaJoinedList));
	}
}
