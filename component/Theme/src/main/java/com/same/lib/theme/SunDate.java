package com.same.lib.theme;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class SunDate {
    private static final double DEGRAD = 0.017453292519943295D;
    private static final double RADEG = 57.29577951308232D;
    private static final double INV360 = 0.002777777777777778D;

    public SunDate() {
    }

    private static long days_since_2000_Jan_0(int y, int m, int d) {
        return 367L * (long) y - (long) (7 * (y + (m + 9) / 12) / 4) + (long) (275 * m / 9) + (long) d - 730530L;
    }

    private static double revolution(double x) {
        return x - 360.0D * Math.floor(x * 0.002777777777777778D);
    }

    private static double rev180(double x) {
        return x - 360.0D * Math.floor(x * 0.002777777777777778D + 0.5D);
    }

    private static double GMST0(double d) {
        return revolution(818.9874D + 0.985647352D * d);
    }

    private static double sind(double x) {
        return Math.sin(x * 0.017453292519943295D);
    }

    private static double cosd(double x) {
        return Math.cos(x * 0.017453292519943295D);
    }

    private static double tand(double x) {
        return Math.tan(x * 0.017453292519943295D);
    }

    private static double acosd(double x) {
        return 57.29577951308232D * Math.acos(x);
    }

    private static double atan2d(double y, double x) {
        return 57.29577951308232D * Math.atan2(y, x);
    }

    private static void sunposAtDay(double p, double[] ot, double[] d) {
        double S = revolution(356.047D + 0.9856002585D * p);
        double l = 282.9404D + 4.70935E-5D * p;
        double a = 0.016709D - 1.151E-9D * p;
        double V = a * 57.29577951308232D * sind(S) * (1.0D + a * cosd(S)) + S;
        double k = cosd(V) - a;
        double i = Math.sqrt(1.0D - a * a) * sind(V);
        d[0] = Math.sqrt(k * k + i * i);
        i = atan2d(i, k);
        ot[0] = i + l;
        if (ot[0] >= 360.0D) {
            ot[0] -= 360.0D;
        }

    }

    private static void sun_RA_decAtDay(double d, double[] RA, double[] dec, double[] r) {
        double[] lon = new double[1];
        sunposAtDay(d, lon, r);
        double xs = r[0] * cosd(lon[0]);
        double ys = r[0] * sind(lon[0]);
        double obl_ecl = 23.4393D - 3.563E-7D * d;
        double ye = ys * cosd(obl_ecl);
        double ze = ys * sind(obl_ecl);
        RA[0] = atan2d(ye, xs);
        dec[0] = atan2d(ze, Math.sqrt(xs * xs + ye * ye));
    }

    private static int sunRiseSetHelperForYear(int year, int month, int day, double lon, double lat, double altit, int upper_limb, double[] sun) {
        double[] sRA = new double[1];
        double[] sdec = new double[1];
        double[] sr = new double[1];
        int rc = 0;
        double d = (double) days_since_2000_Jan_0(year, month, day) + 0.5D - lon / 360.0D;
        double sidtime = revolution(GMST0(d) + 180.0D + lon);
        sun_RA_decAtDay(d, sRA, sdec, sr);
        double tsouth = 12.0D - rev180(sidtime - sRA[0]) / 15.0D;
        double sradius = 0.2666D / sr[0];
        if (upper_limb != 0) {
            altit -= sradius;
        }

        double cost = (sind(altit) - sind(lat) * sind(sdec[0])) / (cosd(lat) * cosd(sdec[0]));
        double t;
        if (cost >= 1.0D) {
            rc = -1;
            t = 0.0D;
        } else if (cost <= -1.0D) {
            rc = 1;
            t = 12.0D;
        } else {
            t = acosd(cost) / 15.0D;
        }

        sun[0] = tsouth - t;
        sun[1] = tsouth + t;
        return rc;
    }

    private static int sunRiseSetForYear(int year, int month, int day, double lon, double lat, double[] sun) {
        return sunRiseSetHelperForYear(year, month, day, lon, lat, -0.5833333333333334D, 1, sun);
    }

    public static int[] calculateSunriseSunset(double lat, double lon) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        double[] sun = new double[2];
        sunRiseSetForYear(calendar.get(1), calendar.get(2) + 1, calendar.get(5), lon, lat, sun);
        int timeZoneOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000 / 60;
        int sunrise = (int) (sun[0] * 60.0D) + timeZoneOffset;
        int sunset = (int) (sun[1] * 60.0D) + timeZoneOffset;
        if (sunrise < 0) {
            sunrise += 1440;
        } else if (sunrise > 1440) {
            sunrise -= 1440;
        }

        if (sunset < 0) {
            sunset += 1440;
        } else if (sunset > 1440) {
            sunset += 1440;
        }

        return new int[]{sunrise, sunset};
    }
}
