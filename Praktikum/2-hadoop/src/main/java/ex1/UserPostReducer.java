package ex1;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UserPostReducer extends Reducer<Text, Text, Text, IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,
			InterruptedException {
		String creator = null;
		boolean isPost = false;

		for (Text text : values) {
			String str = text.toString();

			if (str.contains("sioc:creator_of"))
				creator = str.split(">")[1];
			if (str.contains("sib:Post"))
				isPost = true;
		}

		if (isPost && creator != null) {
			creator = StringUtils.rightPad(creator, 10);
			context.write(new Text(creator + "= "), new IntWritable(1));
		}
	}
}
