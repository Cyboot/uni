package ex4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendsChainReducer extends Reducer<Text, Text, Text, Text> {
	private String	userA;
	private String	userB;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		userA = context.getConfiguration().get("userA");
		userB = context.getConfiguration().get("userB");
	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		List<String[]> tails = new ArrayList<String[]>();

		for (Text t : values) {
			tails.add(t.toString().split(" "));
		}

		/**
		 * <pre>
		 * Example:
		 * 	 key = z
		 * 	 tail = a, [b1, b2]
		 * 
		 * 	 resultChain[0] = a 		z 	a
		 * 	 resultChain[1] = a 		z 	[b1, b2]
		 * 	 resultChain[2] = [b2, b1] 	z 	a
		 * 	 resultChain[3] = [b2, b1] 	z 	[b1, b2]
		 * 
		 * Output:
		 *   a 	:=	z  b1  b2 
		 *   b2 :=	b1 z   a
		 * 
		 * </pre>
		 */
		for (String[] tailA : tails) {
			for (String[] tailB : tails) {
				// create a FriendChain with: <tailA - middle - tailB>
				List<String> resultChain = concatChains(tailA, key.toString(), tailB);

				// only conside cyclefree Chains (no A - B - A)
				if (isCycleFree(resultChain)) {

					// check if we found a complete chain (A - ... - B)
					if (isFinished(resultChain)) {
						// increment Counter to show that we have found a
						// complete chain
						context.getCounter(Ex4Main.Counters.COMPLETE_CHAINS).increment(1L);

						// print the result
						context.write(new Text("result"), new Text(
								printChainWithDistance(resultChain)));
					}

					// check if the end is USER_A or USER_B
					else if (checkChainEnd(resultChain)) {
						// split friendChain: use first element as key,
						// remaining as value
						String first = resultChain.get(0);
						resultChain.remove(0);

						context.write(new Text(first), new Text(printChain(resultChain)));
					}

				}
			}
		}
	}

	public static boolean isCycleFree(List<String> chain) {
		for (String str : chain) {
			// check if there are two occurences of str
			if (chain.indexOf(str) != chain.lastIndexOf(str)) {
				return false;
			}
		}
		return true;
	}

	public boolean isFinished(List<String> chain) {
		String firstElement = chain.get(0);
		String lastElement = chain.get(chain.size() - 1);

		return (firstElement.equals(userA)) && (lastElement.equals(userB));
	}

	public boolean checkChainEnd(List<String> chain) {
		String lastElement = chain.get(chain.size() - 1);
		return lastElement.equals(userA) || lastElement.equals(userB);
	}

	public String printChain(List<String> chain) {
		return StringUtils.join(chain, ' ');
	}

	public String printChainWithDistance(List<String> chain) {
		return printChain(chain) + ",\t distance = " + (chain.size() - 1);
	}

	public List<String> concatChains(String[] a, String middle, String[] b) {
		List<String> res = new ArrayList<String>();
		List<String> left = Arrays.asList(a);
		List<String> right = Arrays.asList(b);

		// reverse left List
		Collections.reverse(left);

		// combine left + middle + right
		res.addAll(left);
		res.add(middle);
		res.addAll(right);

		return res;
	}
}
