package ex2;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FullNameReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		String firstName = "";
		String lastName = "";
		String gender = "";
		String birthday = "";


		for (Text text : values) {
			String[] split = text.toString().split(">");

			String prefix = split[0];
			String value = split[1];

			if (prefix.equals("firstName")) {
				firstName = value;
			}
			if (prefix.equals("lastName")) {
				lastName = value;
			}
			if (prefix.equals("birthday")) {
				birthday = value;
			}
			if (prefix.equals("gender")) {
				gender = value;
			}
		}
		firstName = StringUtils.rightPad(firstName, 10, " ");
		lastName = StringUtils.rightPad(lastName, 12, " ");
		gender = StringUtils.rightPad(gender + ")", 10, " ");

		String user = StringUtils.rightPad(key.toString(), 10, " ");

		context.write(new Text(user), new Text(firstName + "\t " + lastName + "\t (" + gender
				+ "\t " + birthday));
	}
}
