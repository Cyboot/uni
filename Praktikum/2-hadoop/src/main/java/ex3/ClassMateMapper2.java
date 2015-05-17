package ex3;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ClassMateMapper2 extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String line = value.toString();
		String[] split = line.split("\t");

		String user = split[0];
		String concatValue = split[1];

		String organization = "";
		String year = "";


		for (String str : concatValue.split(";;;")) {
			String prefix = str.split(">")[0];
			String val = str.split(">")[1];

			if (prefix.equals("foaf:organization")) {
				organization = val;
			}
			if (prefix.equals("sib:class_year")) {
				year = val;
			}
		}

		context.write(new Text(organization + ";;;" + year), new Text(user));
	}
}
