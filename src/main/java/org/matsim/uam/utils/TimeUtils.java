package org.matsim.uam.utils;

import org.matsim.core.utils.misc.Time;

public class TimeUtils {
    public interface CustomTimeWriter {
        String writeTime(double time);
    }

    public static class SecondsFromMidnightTimeWriter implements CustomTimeWriter {
        @Override
        public String writeTime(double time) {
            return Long.toString((long) time);
        }
    }

    public static class DefaultTimeWriter implements CustomTimeWriter {
        @Override
        public String writeTime(double time) {
            return Time.writeTime(time);
        }
    }
}
