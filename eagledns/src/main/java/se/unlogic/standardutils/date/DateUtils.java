/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat YEAR_FORMATTER = new SimpleDateFormat("yyyy");

	public static boolean isValidDate(DateFormat sdf, String date) {

		try {
			sdf.parse(date);
		} catch (ParseException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	public static Date getDate(DateFormat sdf, String date) {

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static long daysBetween(Date startDate, Date endDate) {

		Calendar start = Calendar.getInstance();
		start.setTime(startDate);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		return daysBetween(start, end);
	}

	public static long daysBetween(Calendar startDate, Calendar endDate) {

		startDate = (Calendar) startDate.clone();

		long daysBetween = 0;

		while (startDate.get(Calendar.YEAR) < endDate.get(Calendar.YEAR)) {

			if(startDate.get(Calendar.DAY_OF_YEAR) != 1){

				int diff = startDate.getMaximum(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

				diff++;

				startDate.add(Calendar.DAY_OF_YEAR, diff);

				daysBetween += diff;

			}else{

				daysBetween += startDate.getMaximum(Calendar.DAY_OF_YEAR);

				startDate.add(Calendar.YEAR, 1);
			}
		}

		daysBetween += endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

		return daysBetween;
	}
}
