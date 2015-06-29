package snippet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class Snippet {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date refDate = df.parse("2015-03-01");
		Date refDAtePlus = DateUtils.addWeeks(refDate, 2);
		Date birthday = df.parse("1903-02-02");

		birthday = DateUtils.setYears(birthday, refDate.getYear() + 1900);

		if (birthday.after(refDate) && birthday.before(refDAtePlus)) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}
	}
}
