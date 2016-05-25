package se.unlogic.standardutils.date;

import se.unlogic.standardutils.string.Stringyfier;

import java.sql.Date;


public class DateStringyfier implements Stringyfier {

	public String format(Object bean) {

		return DateUtils.DATE_FORMATTER.format((Date)bean);
	}

}
