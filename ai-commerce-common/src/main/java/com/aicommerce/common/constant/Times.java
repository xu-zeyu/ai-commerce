package com.aicommerce.common.constant;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Times {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final ZoneId GLOBAL_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        .withZone(GLOBAL_ZONE_ID);

    private Times() {
    }

}
