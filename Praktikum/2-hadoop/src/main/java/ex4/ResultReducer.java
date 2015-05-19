package ex4;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ResultReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {

		if (key.toString().equals("result")) {
			final int MAX = 10;
			context.write(new Text("Writing max. " + MAX + " paths."), new Text());

			int counter = 1;
			for (Text text : values) {
				String valueOut = formatOutput(text);
				context.write(new Text(Integer.toString(counter) + "."), new Text(valueOut));

				counter++;
				if (counter > MAX)
					break;
			}
		}
	}

	/**
	 * make a nice looking output
	 * 
	 * @return
	 */
	private String formatOutput(Text text) {
		StringBuilder sb = new StringBuilder();

		boolean isOdd = false;
		String distancePart = null;
		for (String str : text.toString().split(",")) {
			if (str.contains("\t")) {
				sb.setLength(sb.length() - 3);
				distancePart = str;
				break;
			}
			String userPadded = StringUtils.rightPad(str, 10);
			sb.append(userPadded).append("-> ");

			isOdd = !isOdd;
		}

		// fill the "missing" friend for odd-sized friendchains
		if (!isOdd)
			sb.append("          ");

		sb.append("\t " + distancePart);

		return sb.toString();
	}
}
