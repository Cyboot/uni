package ex1b;

import java.io.IOException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InitialMapper extends Mapper<Object, Text, Text, MapWritable> {
	@Override
	protected void map(Object k, Text value, Context context) throws IOException,
			InterruptedException {
		String key = value.toString().split("\t")[0];
		String friendList = value.toString().split("\t")[1];

		MapWritable valueOUT = new MapWritable();
		valueOUT.put(new Text("friendList"), new Text(friendList));

		context.write(new Text(key), valueOUT);
	}
}
