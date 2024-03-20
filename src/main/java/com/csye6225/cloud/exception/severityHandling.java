package com.csye6225.cloud.exception;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import com.google.cloud.logging.Severity;

import java.util.Map;

public class severityHandling extends JsonLayout {
    private static final String SEVERITY_FIELD = "severity";
    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        map.put(SEVERITY_FIELD, severityFor(event.getLevel()));
    }
    private static Severity severityFor(Level level) {
        return switch (level.toInt()) {
            case 5000 -> Severity.DEBUG;
            case 20000 -> Severity.INFO;
            case 30000 -> Severity.WARNING;
            case 40000 -> Severity.ERROR;
            default -> Severity.DEFAULT;
        };
    }
}
