package ex3;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ClassMateReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		String organization = null;
		String year = null;
		String firstName = null;
		String lastName = null;

		for (Text text : values) {
			String str = text.toString();

			if (str.contains("foaf:organization"))
				organization = str.split(">")[1];
			if (str.contains("sib:class_year"))
				year = str.split(">")[1];
			if (str.contains("foaf:firstName"))
				firstName = str.split(">")[1];
			if (str.contains("foaf:lastName"))
				lastName = str.split(">")[1];
		}

		organization = StringUtils.rightPad(StringUtils.abbreviate(organization, 43), 45);

		if (organization != null && year != null) {
			context.write(new Text(organization + year + " ->"), new Text(firstName + " "
					+ lastName));
		}
	}
}
