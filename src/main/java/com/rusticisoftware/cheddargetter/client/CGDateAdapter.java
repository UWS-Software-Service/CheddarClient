package com.rusticisoftware.cheddargetter.client;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CGDateAdapter extends XmlAdapter<String, Date> {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public Date unmarshal(String val) throws Exception {
        if (val == null || val.length() == 0)
            return null;
        SimpleDateFormat sdf = createFormatter();
        return sdf.parse(fixDateFormat(val));
    }

    public String marshal(Date val) throws Exception {
        String date = createFormatter().format(val);
        return unfixDateFormat(date);
    }

    protected SimpleDateFormat createFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(UTC);
        return sdf;
    }

    public static String fixDateFormat(String cgDate) {
        //CG's dates have annoying ':' symbol in middle of timezone part
        //So here we take it out
        int tzIndex = cgDate.lastIndexOf("+");
        String tz = cgDate.substring(tzIndex, cgDate.length());
        String modifiedTz = tz.replace(":", "");
        return cgDate.substring(0, tzIndex) + modifiedTz;
    }

    public static String unfixDateFormat(String cgDate) {
        //CG's dates have annoying ':' symbol in middle of timezone part
        //So here we take it out
        int tzIndex = cgDate.lastIndexOf("+");
        if (tzIndex > 0) {
            return cgDate.substring(0, tzIndex + 3) +":" + cgDate.substring(tzIndex + 3, cgDate.length());
        } else {
            return cgDate;
        }
    }
}