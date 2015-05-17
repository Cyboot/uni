package ex3;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ClassMateMapper extends Mapper<Object, Text, Text, Text> {
	private Pattern	p;

	@Override
	protected void setup(Mapper<Object, Text, Text, Text>.Context context) throws IOException,
			InterruptedException {
		p = Pattern.compile(".*\\\"(.*)\\\".*");
	}

	@Override
	protected void map(Object key, Text value, Context context) throws IOException,
			InterruptedException {
		String line = value.toString();
		String[] splits = line.split(" ");

		String subject = splits[0];
		String edge = splits[1];
		String object = splits[2];


		// check the edge
		if (edge.equals("foaf:organization") || edge.equals("sib:class_year")) {
			// if there is an object in quote use it
			Matcher m = p.matcher(line);
			if (m.find()) {
				object = m.group(1);
			}

			context.write(new Text(subject), new Text(edge + ">" + object));
		}
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile(".*\\\"(.*)\\\".*");

		Matcher m = p.matcher("your \"string\" here \"asdf\"");
		if (m.find()) {
			System.out.println(m.group(1));
		}
	}

}
