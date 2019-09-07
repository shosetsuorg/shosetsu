package com.github.doomsdayrs.apps.shosetsu;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    public static void format(DateTime dateTime) {
        long time = dateTime.getMillis();

        time -= dateTime.getHourOfDay();
        time -= dateTime.getMinuteOfHour();
        time -= dateTime.getSecondOfMinute();
        time -= dateTime.getMillisOfSecond();

        System.out.println("Formatted TIME " + time);
        System.out.println("Formatted DATETIME " + new DateTime(time));

    }

    @Test
    public void date() {
        long time = System.currentTimeMillis();
        System.out.println("Raw TIME " + time);

        DateTime dateTime = new DateTime(time);
        System.out.println("Raw DateTime " + dateTime);

        int divided = (int) (time / 86400);
        System.out.println("Days since the beginning of TIME: " + divided);
        format(dateTime);
    }
}