package ca.uol.aig.fftpack;

/**
 * Created by leotardn on 2017-02-07.
 */

public class body {
    
    final static int BICEP_FRQ=90;
    final static int TRICEPS_FRQ=98;
    final static int FOREARM_FRQ= 897;

    public static boolean isBicepActive=false;
    public static boolean isTricepsActive=false;
    public static boolean isForearmActive=false;

    private static int bicep_counter=0;
    private static int triceps_counter=0;
    private static int forearm_counter=0;

    public static int getBicep_counter() {
        return bicep_counter;
    }

    public static int getForearm_counter() {
        return forearm_counter;
    }

    public static int getTriceps_counter() {
        return triceps_counter;
    }

    public static void setBicep_counter() {
        bicep_counter++;
    }

    public static void setForearm_counter() {
        forearm_counter++;
    }

    public static void setTriceps_counter() {
        triceps_counter++;
    }
}

