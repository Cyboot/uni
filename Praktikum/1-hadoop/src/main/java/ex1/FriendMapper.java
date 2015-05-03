package ex1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FriendMapper extends Mapper<Object, Text, Text, Text> {
	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String[] splits = value.toString().split(" ");

		if (splits != null && splits.length >= 3) {

			// check if the predicate is a foaf:knows edge
			// also check the user
			if (checkEdge(splits) && checkUser(splits)) {
				// emit the person with value 1
				context.write(new Text(splits[0]), new Text(splits[2]));
			}

		}
	}

	private boolean checkUser(String[] splits) {
		if (splits[0].equals(Ex1Main.RELEVANT_USER)) {
			return true;
		}
		return false;
	}

	private boolean checkEdge(String[] splits) {
		if (splits[1].equals("foaf:knows")) {
			return true;
		}
		return false;
	}
}
