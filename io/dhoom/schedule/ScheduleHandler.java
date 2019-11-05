package io.dhoom.schedule;

import io.dhoom.util.*;
import org.bukkit.*;
import java.util.*;

public class ScheduleHandler
{
    public static List<Schedule> schedules;
    
    public static void setSchedules(final ConfigFile config) {
        ScheduleHandler.schedules.clear();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(config.getString("schedule-timezone")));
        calendar.setFirstDayOfWeek(2);
        calendar.set(11, 0);
        calendar.clear(12);
        calendar.clear(13);
        calendar.clear(14);
        calendar.set(7, calendar.getFirstDayOfWeek());
        final long calendarMillis = calendar.getTimeInMillis();
        if (config.getConfiguration().contains("schedule-monday")) {
            readSchedule(config, "monday", 0, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for monday.");
        }
        if (config.getConfiguration().contains("schedule-tuesday")) {
            readSchedule(config, "tuesday", 1, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for tuesday.");
        }
        if (config.getConfiguration().contains("schedule-wednesday")) {
            readSchedule(config, "wednesday", 2, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for wednesday.");
        }
        if (config.getConfiguration().contains("schedule-thursday")) {
            readSchedule(config, "thursday", 3, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for thursday.");
        }
        if (config.getConfiguration().contains("schedule-friday")) {
            readSchedule(config, "friday", 4, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for friday.");
        }
        if (config.getConfiguration().contains("schedule-saturday")) {
            readSchedule(config, "saturday", 5, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for saturday.");
        }
        if (config.getConfiguration().contains("schedule-sunday")) {
            readSchedule(config, "sunday", 6, calendarMillis);
            Bukkit.getLogger().info("[Practice] (Schedule): Loaded schedule for sunday.");
        }
    }
    
    public static void readSchedule(final ConfigFile config, final String day, final int dayInId, final long calendarMillis) {
        try {
            final String[] events = config.getString("schedule-" + day).split("#");
            for (int i = 0; i < events.length; ++i) {
                final String[] event = events[i].split("/");
                final String time = event[0];
                int hours = 0;
                int minute = 0;
                if (time.contains(":")) {
                    final String[] build = time.split(":");
                    hours = Integer.parseInt(build[0]);
                    minute = Integer.parseInt(build[1].replaceAll("[a-zA-Z]", ""));
                }
                else {
                    hours = Integer.parseInt(time.replaceAll("[a-zA-Z]", ""));
                }
                if (time.endsWith("PM")) {
                    hours += 12;
                }
                long eventTime;
                if ((eventTime = calendarMillis + dayInId * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000 + minute * 60 * 1000) < System.currentTimeMillis()) {
                    eventTime += 604800000L;
                }
                ScheduleHandler.schedules.add(new Schedule(eventTime));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Schedule getNextEvent() {
        Schedule ret = null;
        for (final Schedule sched : ScheduleHandler.schedules) {
            if (ret == null) {
                ret = sched;
            }
            else {
                if (sched.getEventTime() >= ret.getEventTime()) {
                    continue;
                }
                ret = sched;
            }
        }
        return ret;
    }
    
    public static Schedule getNextEvent(final String arenaName) {
        Schedule schedule = null;
        for (final Schedule sched : ScheduleHandler.schedules) {
            if (schedule == null) {
                schedule = sched;
            }
            else {
                if (sched.getEventTime() >= schedule.getEventTime()) {
                    continue;
                }
                schedule = sched;
            }
        }
        return schedule;
    }
    
    public static void runSchedule() {
        for (final Schedule schedule : ScheduleHandler.schedules) {
            schedule.runEvent();
        }
    }
    
    static {
        ScheduleHandler.schedules = new ArrayList<Schedule>();
    }
}
