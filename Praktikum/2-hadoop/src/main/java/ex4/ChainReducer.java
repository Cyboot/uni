package ex4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ChainReducer extends Reducer<Text, Text, Text, Text> {
	public static boolean isChainCycleFree(List<String> chain) {
		for (String s : chain) {
			if (chain.indexOf(s) != chain.lastIndexOf(s)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFinished(List<String> chain, String userA,
			String userB) {
		return (((String) chain.get(0)).equals(userA))
				&& (((String) chain.get(chain.size() - 1)).equals(userB));
	}

	public static boolean isChainEndingInOneOfThese(List<String> chain,
			String... strings) {
		String lastElement = (String) chain.get(chain.size() - 1);
		for (String s : strings) {
			if (lastElement.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static String stringifyChain(List<String> chain) {
		StringBuilder sb = new StringBuilder();
		for (String s : chain) {
			if (sb.length() != 0) {
				sb.append(' ');
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public static String stringifyChainWithDistance(List<String> chain) {
		return stringifyChain(chain) + ". distance = " + (chain.size() - 1);
	}

	public static List<String> reverseAndConcatWithOther(String[] a,
			String middle, String[] b) {
		ArrayList<String> res = new ArrayList(a.length + 1 + b.length);
		List<String> la = Arrays.asList(a);
		List<String> lb = Arrays.asList(b);

		Collections.reverse(la);

		res.addAll(la);
		res.add(middle);
		res.addAll(lb);

		return res;
	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		LinkedList<String[]> tails = new LinkedList<String[]>();

		String userA = "sibu:u163";
		String userB = "sibu:u14";

		String tmp = "";

		for (Text t : values) {
			tmp += t.toString() + ",";

			tails.add(t.toString().split(" "));
		}
		String[] tailA;

		for (Iterator<String[]> it = tails.iterator(); it.hasNext();) {

			tailA = (String[]) it.next();
			for (String[] tailB : tails) {
				List<String> resultingChain = reverseAndConcatWithOther(tailA,
						key.toString(), tailB);
				if (isChainCycleFree(resultingChain)) {
					if (isFinished(resultingChain, userA, userB)) {
						context.getCounter(Ex4Main.Counters.FOUND_CHAINS)
								.increment(1L);
						context.write(new Text("_result"), new Text(
								stringifyChainWithDistance(resultingChain)));
					} else if (isChainEndingInOneOfThese(resultingChain,
							new String[] { userA, userB })) {
						String first = (String) resultingChain.get(0);
						resultingChain.remove(0);
						context.write(new Text(first), new Text(
								stringifyChain(resultingChain)));
					}
				}
			}

		}
	}
}
