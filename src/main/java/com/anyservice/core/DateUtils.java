package com.anyservice.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MONTHS;

public class DateUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * Конвертирует {@link LocalDate} в {@link Date} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link LocalDate}
     * @return Результативная дата в {@link Date}
     */
    public static Date convertLocalDateToDate(LocalDate source) {
        if (source == null) return null;
        return Date.from(source.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Конвертирует {@link Date} в {@link LocalDate} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link Date}
     * @return Результативная дата в {@link LocalDateTime}
     */
    public static LocalDate convertDateToLocalDate(Date source) {
        if (source == null) return null;
        return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Возвращает количество дней с начала эпохи по определенной дате
     *
     * @param date Дата
     * @return Дата в днях с начала эпохи
     */
    public static long convertLocalDateToEpochDays(LocalDate date) {
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
    }

    /**
     * Возвращает количество дней с начала эпохи по определенной дате
     *
     * @param date Дата
     * @return Дата в днях с начала эпохи
     */
    public static long convertLocalDateTimeToEpochDays(LocalDateTime date) {
        return date.atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
    }

    public static long convertDateToEpochDays(Date date) {
        return convertLocalDateToEpochDays(convertDateToLocalDate(date));
    }

    /**
     * Конвертирует {@link Date} в {@link LocalDateTime} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link Date}
     * @return Результативная дата в {@link LocalDateTime}
     */
    public static LocalDateTime convertDateToLocalDateTime(Date source) {
        if (source == null) return null;
        return LocalDateTime.ofInstant(new Date(source.getTime()).toInstant(), ZoneId.systemDefault());
    }

    /**
     * Конвертирует {@link LocalDateTime} в {@link Date} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link LocalDateTime}
     * @return Результативная дата в {@link Date}
     */
    public static Date convertLocalDateTimeToDate(LocalDateTime source) {
        if (source == null) return null;
        return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Конвертирует {@link LocalDate} в {@link LocalDateTime}
     *
     * @param source исходная дата в {@link LocalDate}
     * @return результат в виде {@link LocalDateTime}
     */
    public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate source) {
        if (source == null) return null;
        return Timestamp.valueOf(source.atStartOfDay()).toLocalDateTime();
    }

    /**
     * Конвертирует количество дней с начала эпохи {@link Long} в {@link LocalDate} с применением текущей локали
     *
     * @param epochDay Дата в днях с начала эпохи {@link Long}
     * @return Результат в {@link LocalDate}
     */
    public static LocalDate convertEpochDaysToLocalDate(long epochDay) {
        return convertDateToLocalDate(new Date(LocalDate.ofEpochDay(epochDay)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000));
    }

    /**
     * Конвернтируем дату в милисекундах {@link Long} в {@link LocalDate}
     *
     * @param mills Дата в милисекундах
     * @return Результат в {@link LocalDate}
     */
    public static LocalDate convertDateInMillsToLocalDate(Long mills) {
        return Optional.ofNullable(mills)
                .map(Date::new)
                .map(DateUtils::convertDateToLocalDate)
                .orElseThrow(RuntimeException::new);
    }

    /**
     * Конвертация указанной даты в милисекунды
     *
     * @param date исходная дата
     * @return
     */
    public static long convertLocalDateToLong(LocalDate date) {
        ZoneId zoneId = ZoneId.systemDefault();
        long dateMillis = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        return dateMillis;
    }


    /**
     * Конвертирует json {@link String} в необходимый нам формат {@link F}
     *
     * @param s   json в виде {@link String}
     * @param <F> Тип в который мы конвертируем
     * @return необходимый нам формат {@link F}
     */
    public static <F> F convertJsonToObject(String s) throws Exception {
        return mapper.readValue(s, new TypeReference<F>() {
        });
    }

    /**
     * Превращаем какой то обьект в json строку
     *
     * @param o исходный обьект
     * @return строка json
     */
    public static String convertObjectToJson(Object o) throws Exception {
        return mapper.writeValueAsString(o);
    }

    /**
     * Конвертируем дни с начала эпохи в миллисекунды с начала эпохи
     *
     * @param epochDay количество дней с начала эпохи
     * @return количество миллисекунд с начала эпохи
     */
    public static long convertEpochDayToEpochMills(long epochDay) {
        return LocalDate.ofEpochDay(epochDay).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    /**
     * Конвертируем дни с начала эпохи в месяцы с начала эпохи
     *
     * @param epochDays дни с начала эпохи
     * @return месяцы с начала эпохи
     */
    public static int convertEpochDaysToEpochMonth(long epochDays) {
        final LocalDate localDate = convertEpochDaysToLocalDate(epochDays);
        return convertLocalDateToEpochMonth(localDate);
    }

    /**
     * Конвертируем {@link LocalDate} в месяцы с начала эпохи
     *
     * @param localDate дата в {@link LocalDate}
     * @return количество месяцев с начала эпохи
     */
    public static int convertLocalDateToEpochMonth(LocalDate localDate) {
        return Math.toIntExact(MONTHS.between(LocalDate.ofEpochDay(0), localDate));
    }

    /**
     * Превращаем {@link Date} в {@link java.sql.Date}
     *
     * @param date java формат даты в {@link Date}
     * @return jdbc (sql) формат даты в {@link java.sql.Date}
     */
    public static java.sql.Date convertJavaDateToSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }

    /**
     * Get milliseconds from {@link OffsetDateTime}
     *
     * @param dateTime date and time with offset
     * @return milliseconds from the beginning of epoch
     */
    public static long convertOffsetDateTimeToMills(OffsetDateTime dateTime) {
        return dateTime.toInstant().toEpochMilli();
    }

    /**
     * Converts time in mills to {@link OffsetDateTime} for chosen zone
     *
     * @param mills      time in milliseconds from the beginning of epoch
     * @param zoneOffset zone offset in hours from UTC+0
     *                   For example: to get CET time we need to pass zoneOffset=1 UTC+1, because CET == UTC+1
     * @return {@link OffsetDateTime} for specified zone
     */
    public static OffsetDateTime convertLongToOffsetDateTime(Long mills, int zoneOffset) {
        return new Date(mills).toInstant().atOffset(ZoneOffset.ofHours(zoneOffset));
    }
}
