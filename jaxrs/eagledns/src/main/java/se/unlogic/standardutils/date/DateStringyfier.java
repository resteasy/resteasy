package se.unlogic.standardutils.date;

import java.sql.Date;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.string.Stringyfier;


public class DateStringyfier implements Stringyfier {

	public String format(Object bean) {

		return DateUtils.DATE_FORMATTER.format((Date)bean);
	}

}
