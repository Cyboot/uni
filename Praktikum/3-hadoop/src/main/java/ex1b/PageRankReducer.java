package ex1b;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankReducer extends Reducer<Text, MapWritable, Text, MapWritable> {

	private static final double	INITIAL_PAGERANK	= 100;

	@Override
	protected void reduce(Text key, Iterable<MapWritable> values, Context context)
			throws IOException, InterruptedException {
		boolean inialRun = true;
		double sumPageRank = 0;

		List<String> friendList = null;

		for (MapWritable map : values) {
			if (map.containsKey(new Text("friendList"))) {
				String commaJoinedList = map.get(new Text("friendList")).toString();

				friendList = Arrays.asList(commaJoinedList.split(","));
			} else {
				int nrOfFriends = ((IntWritable) map.get(new Text("nrFriends"))).get();
				double pageRank = ((DoubleWritable) map.get(new Text("pageRank"))).get();

				inialRun = false;
				sumPageRank += pageRank / nrOfFriends;
			}
		}

		// skip user with no friends
		if (friendList == null)
			return;

		int fiendCount = friendList.size();

		// if its the first run use a initial pagerank
		if (inialRun)
			sumPageRank = INITIAL_PAGERANK / fiendCount;


		MapWritable valueOUTMap = new MapWritable();
		valueOUTMap.put(new Text("key"), key);
		valueOUTMap.put(new Text("nrFriends"), new IntWritable(fiendCount));
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
