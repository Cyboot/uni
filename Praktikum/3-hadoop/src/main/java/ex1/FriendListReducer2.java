package ex1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendListReducer2 extends Reducer<Text, MapWritable, Text, MapWritable> {

	@Override
	protected void reduce(Text key, Iterable<MapWritable> values, Context context)
			throws IOException, InterruptedException {
		double sumPageRank = 0;

		List<String> friendList = null;

		for (MapWritable map : values) {
			if (map.containsKey(new Text("frienList"))) {
				String commaJoinedList = map.get(new Text("frienList")).toString();

				friendList = Arrays.asList(commaJoinedList.split(","));
			} else {
				int nrOfFriends = ((IntWritable) map.get(new Text("nrOutgoingFriends"))).get();
				double pageRank = ((DoubleWritable) map.get(new Text("pageRank"))).get();

				sumPageRank += pageRank / nrOfFriends;
			}
		}

		// skip user with no friends
		if (friendList == null)
			return;

		int nrOutgoingFriends = friendList.size();

		MapWritable valueOUTMap = new MapWritable();
		valueOUTMap.put(new Text("key"), key);
		valueOUTMap.put(new Text("nrOutgoingFriends"), new IntWritable(nrOutgoingFriends));
		valueOUTMap.put(new Text("pageRank"), new DoubleWritable(sumPageRank));

		// propagate Nr of friends for key to all friends
		for (String user : friendList) {
			context.write(new Text(user), valueOUTMap);
		}

		// also emit the list of friends for the user
		MapWritable valueOUTList = new MapWritable();
		String commaJoinedList = StringUtils.join(friendList, ",");
		valueOUTList.put(new Text("friendList"), new Text(commaJoinedList));

		context.write(key, valueOUTList);
	}
}
