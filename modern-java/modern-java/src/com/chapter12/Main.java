package com.chapter12;

import java.sql.Date;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.TimeZone;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public class Main {

    public static void main(String[] args) {
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");

        LocalDateTime dateTime =  LocalDateTime.of(2017, 9, 21, 13, 45, 20);
        OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(dateTime, newYorkOffset);

        LocalDate date = LocalDate.of(2021, 4, 30);
        JapaneseDate japaneseDate = JapaneseDate.from(date);

        Chronology japaneseChronology = Chronology.ofLocale(Locale.JAPAN);
        ChronoLocalDate now = japaneseChronology.dateNow();

    }
}
