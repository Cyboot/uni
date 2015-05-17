package ex2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FullNameMapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] splits = value.toString().split(" ");

		Text subject = new Text(splits[0]);
		String edge = splits[1];
		String object = splits[2];

		if (splits != null && splits.length >= 3) {
			object = object.replace("\"", "");
			object = object.replace("^^xsd:date", "");

			if (edge.equals("foaf:firstName")) {
				context.write(subject, new Text("firstName>" + object));
			}
			if (edge.equals("foaf:lastName")) {
				context.write(subject, new Text("lastName>" + object));
			}
			if (edge.equals("foaf:birthday")) {
				context.write(subject, new Text("birthday>" + object));
			}
			if (edge.equals("foaf:gender")) {
				context.write(subject, new Text("gender>" + object));
			}
		}
	}
}
