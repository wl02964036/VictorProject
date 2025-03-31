package com.rx.core.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.DecimalStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

	public static Date convert(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}

	public static LocalDateTime parse(String timestamp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(timestamp, formatter);
	}

	public static LocalDateTime parse2(String timestamp) {
		if (timestamp != null && !timestamp.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			return LocalDateTime.parse(timestamp, formatter);
		} else {
			return null;
		}
	}

	public static String getDate(LocalDateTime localDateTime) {
		if (localDateTime != null) {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return localDateTime.format(dateFormatter);
		} else {
			return "";
		}

	}

	public static String getDateTime(LocalDateTime localDateTime) {
		if (localDateTime != null) {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return localDateTime.format(dateFormatter);
		} else {
			return "";
		}
	}

	public static String getDateTime2(LocalDateTime localDateTime) {
		if (localDateTime != null) {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
			return localDateTime.format(dateFormatter);
		} else {
			return "";
		}
	}

	public static String getChina(LocalDateTime localDateTime, boolean needYear) {
		if (localDateTime != null) {
			String pattern = "yyy-MM-dd";
			if (!needYear) {
				pattern = "MM/dd";
			}
			Chronology chrono = MinguoChronology.INSTANCE;
			DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient().appendPattern(pattern).toFormatter()
					.withChronology(chrono).withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
			return localDateTime.format(df);
		} else {
			return "";
		}
	}

	public static String transferMinguoDateToADDate(String dateString) {
		if (dateString != null && !dateString.isEmpty()) {
			Chronology chrono = MinguoChronology.INSTANCE;
			DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyMMdd").toFormatter()
					.withChronology(chrono).withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
			ChronoLocalDate chDate = chrono.date(df.parse(dateString));
			return LocalDate.from(chDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else {
			return "";
		}
	}

	// 將 "2020-11-19T10:54:01+08:00" offset 時間字串轉成 LocalDateTime
	public static LocalDateTime offsetParse(String offsetTime) {
		try {
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(offsetTime);
			return offsetDateTime.toLocalDateTime();
		} catch (DateTimeParseException e) {
			log.error("", e);
			return null;
		}
	}

	/***
	 * 偏移天數
	 * 
	 * @param localDateTime LocalDateTime日期
	 * @param offset        偏移值(負往前推,反之)
	 * @return
	 */
	public static LocalDateTime localDateTimeOffset(LocalDateTime localDateTime, long offset) {
		return Objects.requireNonNullElseGet(localDateTime, LocalDateTime::now).plusDays(offset);
	}

	/**
	 * 比較第二個日期是否大於第一個日期
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 * @return true-大於;false-不大於
	 */
	public static boolean localDateTimeIsBefore(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		return firstDateTime.isBefore(secondDateTime);
	}

	/**
	 * 比較第二個日期是否小於第一個日期
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 * @return true-小於;false-大於
	 */
	public static boolean localDateTimeIsAfter(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		return firstDateTime.isAfter(secondDateTime);
	}

	/**
	 * 比較兩個日期是否相等
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 * @return true-相等;false-不相等
	 */
	public static boolean localDateTimeIsEqual(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		return firstDateTime.isEqual(secondDateTime);
	}

	/**
	 * 兩個日期相差天數
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 */
	public static long localDateTimeBetween(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		Duration duration = Duration.between(firstDateTime, secondDateTime);
		return duration.toDays();
	}

	/**
	 * 兩個日期相差小時數
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 */
	public static long localDateTimeBetweenHour(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		Duration duration = Duration.between(firstDateTime, secondDateTime);
		return duration.toHours();
	}

	/**
	 * 兩個日期相差分鐘數
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 */
	public static long localDateTimeBetweenMinutes(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		Duration duration = Duration.between(firstDateTime, secondDateTime);
		return duration.toMinutes();
	}

	/**
	 * 兩個日期相差分鐘數
	 *
	 * @param firstDateTime  第一個日期
	 * @param secondDateTime 第二個日期
	 */
	public static long localDateTimeBetweenSeconds(LocalDateTime firstDateTime, LocalDateTime secondDateTime) {
		Duration duration = Duration.between(firstDateTime, secondDateTime);
		return duration.toSeconds();
	}

	/**
	 * 取得加一天日期
	 * 
	 * @Method: plusDate
	 * @author Hank_Chuang
	 * @param inputDate 日期
	 * @param plusType  日:Calendar.DATE,月:Calendar.MONTH,年:Calendar.YEAR)
	 * @param unit      增加(減少)數值(日, 月, 年)
	 * @return
	 * @date 2018-02-12 10:48:03
	 */
	public static Date plusDate(Date inputDate, int plusType, int unit) {
		if (inputDate == null) {
			return null;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(inputDate);
		c.add(plusType, unit);

		return c.getTime();
	}

	/**
	 * 日期格式轉為字串 - 民國格式 ( 1911 年以前則回傳空白 )
	 * 
	 * @Method: dateFormatROC
	 * @author Hank_Chuang
	 * @param iDate  來源日期
	 * @param format 格式化 PS. yyy or yyy/MM/dd or yyy-MM-dd HH:mm:ss
	 * @return
	 * @date 2018-02-12 10:42:17
	 */
	public static String dateFormatROC(Date iDate, String format) {
		String strDate = "";
		if (iDate == null) {
			return "";
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(iDate);

			try {
				if (cal.get(Calendar.YEAR) > 1911) {
					String pattern1 = format.substring(0, format.lastIndexOf('y') + 1);
					String pattern2 = format.substring(format.lastIndexOf('y') + 1);
					String pattern3 = format.substring(format.indexOf('y'), format.lastIndexOf('y') + 1);
					String yearRoc = ""
							+ String.format("%0" + pattern3.length() + "d", (cal.get(Calendar.YEAR) - 1911));

					SimpleDateFormat sdf = new SimpleDateFormat(pattern2);
					strDate = pattern1.replace(pattern3, yearRoc.substring(yearRoc.length() - pattern3.length()))
							+ sdf.format(cal.getTime());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return strDate;
		}
	}
}
