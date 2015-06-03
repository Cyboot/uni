package b;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankReducer extends Reducer<Text, MapWritable, Text, MapWritable> {

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
		if (inialRun) {
			// for the initial PageRank use 1/(#USER)
			Configuration conf = context.getConfiguration();
			sumPageRank = 1.0 / conf.getInt("NR_USER", 1);
		}

		// set the pagerank for a subset of users
		String keyStr = key.toString();
		if (keyStr.startsWith("sibu:u9") && keyStr.length() < 9) {
			Counter counter = context.getCounter("USER", keyStr);

			// convert from double to long (pagerank to CounterValue)
			long longBits = Double.doubleToLongBits(sumPageRank);
			counter.increment(longBits);
		}

		MapWritable valueOUTMap = new MapWritable();
		valueOUTMap.put(new Text("key"), key);
		valueOUTMap.put(new Text("nrFriends"), new IntWritable(fiendCount));
		valueOUTMap.put(new Text("pageRank"), new DoubleWritable(sumPageRank));

		// propagate pagerank for key to all friends
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