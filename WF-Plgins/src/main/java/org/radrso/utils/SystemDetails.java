package org.radrso.utils;

/**
 * Created by raomengnan on 16-12-2.
 */
import lombok.Data;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Data
@ToString
public class SystemDetails {
    private String timeZone;
    private String currentTime;
    private String os;
    private String osArch;
    private String osVersion;

    public SystemDetails(){
        setTimeZone();
        setCurrentTime();
        setOS();
    }

    public void setTimeZone(){
        Calendar cal = Calendar.getInstance();
        TimeZone timeZ = cal.getTimeZone();
        timeZone =  timeZ.getDisplayName();
    }

    public void setCurrentTime(){
        String fromFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(fromFormat);
        Date myDate = new Date();
        currentTime = format.format(myDate);
    }

    public void setOS(){
        os = System.getProperty("os.name"); //操作系统名称
        osArch = System.getProperty("os.arch"); //操作系统构架
        osVersion = System.getProperty("os.version"); //操作系统版本
    }


    public static void outputDetails() {
        SystemDetails systemDetails = new SystemDetails();
        System.out.println("系统时区:" + systemDetails.getTimeZone());
        System.out.println("系统时间:" + systemDetails.getCurrentTime());

        System.out.println("当前系统:" + systemDetails.getOs());
        System.out.println("当前系统架构" + systemDetails.getOsArch());
        System.out.println("当前系统版本:" + systemDetails.getOsVersion());
    }
}
