package ex3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ClassMateReducer2 extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		String[] split = key.toString().split(";;;");

		// just consider the element if there is a Oranization AND a year
		if (split.length < 2) {
			return;
		}

		String organization = split[0];
		String year = split[1];

		String users = "";
		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			Text next = it.next();

			users += next.toString();
			if (it.hasNext())
				users += ", ";
		}

		organization = StringUtils.rightPad(StringUtils.abbreviate(organization, 43), 45);
		year = StringUtils.rightPad(year, 5);


		context.write(new Text(organization + year), new Text(users));
	}


	public static void main(String[] args) {
		String organization = StringUtils.rightPad("Uni Freiburg", 50);
		String year = StringUtils.rightPad("1987", 5);

		System.out.println(organization + year);
	}
}
