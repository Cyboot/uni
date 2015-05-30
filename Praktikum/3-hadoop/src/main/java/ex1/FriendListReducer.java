package ex1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendListReducer extends Reducer<Text, Text, Text, Text> {
	private static final int INITIAL_PAGERANK = 100;
	public static final String PAGE_RANK = "pagerank";
	public static final String FRIENDLIST = "friendlist";

	public static final String INCOMING = "incoming";
	public static final String OUTGOING = "outgoing";

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		List<String> outgoingList = new ArrayList<String>();
		List<String> incomingList = new ArrayList<String>();

		// check if user are incoing or outgoing links
		for (Text text : values) {
			String[] split = text.toString().split(" > ");

			if (split[0].equals(OUTGOING)) {
				outgoingList.add(split[1]);
			} else {
				incomingList.add(split[1]);
			}
		}

		int nrOutgoingFriends = outgoingList.size();
		double pageRank = INITIAL_PAGERANK / nrOutgoingFriends;
		String valueOut = PAGE_RANK + "->" + key.toString() + " > "
				+ nrOutgoingFriends + " > " + pageRank;

		// propagate Nr of friends for key to all friends
		for (String user : outgoingList) {
			context.write(new Text(user), new Text(valueOut));
		}

		// also emit the list of friends for the user
		String commaJoinedList = StringUtils.join(outgoingList, ",");
		context.write(key, new Text(FRIENDLIST + "->" + commaJoinedList));
	}
}
