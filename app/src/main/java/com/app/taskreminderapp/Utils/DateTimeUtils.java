package com.app.taskreminderapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Colinares on 11/7/2017.
 */
public class DateTimeUtils {

    public static String getDateTime() {
        Calendar calendar = Calendar.getInstance(Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-2017 HH:mm a", Locale.US);

        return format.format(calendar.getTimeInMillis());
    }

}
