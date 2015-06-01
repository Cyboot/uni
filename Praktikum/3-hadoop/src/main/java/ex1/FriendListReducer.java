package ex1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import common.KeyValueWritable;

public class FriendListReducer extends Reducer<Text, KeyValueWritable, Text, MapWritable> {
	private static final int	INITIAL_PAGERANK	= 100;
	public static final String	PAGE_RANK			= "pagerank";
	public static final String	FRIENDLIST			= "friendlist";

	public static final String	INCOMING			= "incoming";
	public static final String	OUTGOING			= "outgoing";

	@Override
	protected void reduce(Text key, Iterable<KeyValueWritable> values, Context context)
			throws IOException, InterruptedException {
		List<String> outgoingList = new ArrayList<String>();
		List<String> incomingList = new ArrayList<String>();

		// check if user are incoing or outgoing links
		for (KeyValueWritable tmp : values) {
			String value = tmp.getValue();
			String tmpKey = tmp.getKey();

			if (tmpKey.equals(OUTGOING)) {
				outgoingList.add(value);
			} else {
				incomingList.add(value);
			}
		}

		int nrOutgoingFriends = outgoingList.size();
		double pageRank = INITIAL_PAGERANK / nrOutgoingFriends;

		// prepare the first output Value (infos about pagerank)
		MapWritable valueOUTMap = new MapWritable();
		valueOUTMap.put(new Text("key"), key);
		valueOUTMap.put(new Text("nrOutgoingFriends"), new IntWritable(nrOutgoingFriends));
		valueOUTMap.put(new Text("pageRank"), new DoubleWritable(pageRank));

		// propagate Nr of friends for key to all friends
		for (String user : outgoingList) {
			context.write(new Text(user), valueOUTMap);
		}

		// also emit the list of friends for the user
		MapWritable valueOUTList = new MapWritable();
		String commaJoinedList = StringUtils.join(outgoingList, ",");
		valueOUTList.put(new Text("friendList"), new Text(commaJoinedList));

		context.write(key, valueOUTList);
	}
}
