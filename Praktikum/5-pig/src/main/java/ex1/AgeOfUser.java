package ex1;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class AgeOfUser extends EvalFunc<Float> {
	private DateFormat	df	= new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Float exec(Tuple input) throws IOException {
		if (input == null || input.size() == 0)
			return null;


		try {
			String obj = input.get(0).toString();

			Date date = parseDate(obj);

			return (new Date().getTime() - date.getTime()) / 3.15569e10f;
		} catch (Exception e) {
			return null;
		}
	}

	private Date parseDate(String str) throws ParseException {
		str = str.substring(1, 12);

		return df.parse(str);
	}
}