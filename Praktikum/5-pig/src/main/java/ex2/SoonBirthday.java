package ex2;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;

public class SoonBirthday extends FilterFunc {
	private DateFormat	df	= new SimpleDateFormat("yyyy-MM-dd");


	@SuppressWarnings("deprecation")
	@Override
	public Boolean exec(Tuple input) throws IOException {
		try {
			Date refDate = df.parse(input.get(1).toString());
			Date refDatePlus2Weeks = DateUtils.addWeeks(refDate, 2);

			Date birthday = parseDate(input.get(0).toString());
			birthday = DateUtils.setYears(birthday, refDate.getYear() + 1900);

			if (birthday.after(refDate) && birthday.before(refDatePlus2Weeks)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			// return true;
			throw new IOException(e);
		}
	}

	private Date parseDate(String str) throws ParseException {
		str = str.substring(1, 12);

		return df.parse(str);
	}
}
