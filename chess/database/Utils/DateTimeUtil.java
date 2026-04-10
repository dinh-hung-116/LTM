package com.chess.database.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter SQLITE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Convert LocalDateTime -> String (for SQLite)
    // YYYY:MM:DDTHH:MM:SS -> YYYY:MM:DD HH:MM:SS
    public static String toSQLiteString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(SQLITE_FORMATTER);
    }

    // Convert String -> LocalDateTime (from SQLite)
    // ngược lại
    public static LocalDateTime fromSQLiteString(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, SQLITE_FORMATTER);
    }
}