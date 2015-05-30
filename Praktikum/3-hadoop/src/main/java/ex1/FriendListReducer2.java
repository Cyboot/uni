package ex1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
			if (str[0].equals(FriendListReducer.FRIENDLIST)) {
				friendList = Arrays.asList(str[1].split(","));
			}
		}

	}
}
