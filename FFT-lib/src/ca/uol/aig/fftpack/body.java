package ca.uol.aig.fftpack;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class body {


    int BICEP_FRQ;
    int TRICEPS_FRQ;
    int FOREARM_FRQ;
    int DIST_SENS_FRQ;


    public static boolean isBicepActive;
    public static boolean isTricepsActive;
    public static boolean isForearmActive;
    public static boolean isDist_sensorActive;

    private int bicep_counter;
    private int triceps_counter;
    private int forearm_counter;
    private int dist_sensor_counter;

    public Drawable BICEP;
    public Drawable TRICEPS;
    public Drawable FOREARM;
    public Paint paintBicep;
    public Paint paintTriceps;
    public Paint paintForearm;
    public ImageView imageViewBicep;
    public ImageView imageViewTriceps;
    public ImageView imageViewForearm;
    public int width;
    public int height;
    ImageView imageViewDrawBody;
    public int Bicep_textColor;
    public int Triceps_textColor;
    public int Forearm_textColor;
    //drawBody drawBody;
    //Context context;


    public body(int BICEP_FRQ, int TRICEPS_FRQ, int FOREARM_FRQ,int DIST_SENS_FRQ, Drawable BICEP,
                Drawable TRICEPS, Drawable FOREARM , int Bicep_textColor, int Triceps_textColor, int Forearm_textColor/*, ImageView imageViewBicep, ImageView imageViewTriceps, ImageView imageViewForearm,
                Paint paintBicep, Paint paintTriceps, Paint paintForearm, int width, int height*/){

        this.BICEP_FRQ=BICEP_FRQ;
        this.TRICEPS_FRQ=TRICEPS_FRQ;
        this.FOREARM_FRQ=FOREARM_FRQ;
        this.DIST_SENS_FRQ= DIST_SENS_FRQ;

        this.BICEP=BICEP;
        this.TRICEPS=TRICEPS;
        this.FOREARM=FOREARM;

        this.Bicep_textColor = Bicep_textColor;
        this.Triceps_textColor = Triceps_textColor;
        this.Forearm_textColor = Forearm_textColor;

        isBicepActive=false;
        isTricepsActive=false;
        isForearmActive=false;
        isDist_sensorActive=false;

        bicep_counter=0;
        triceps_counter=0;
        forearm_counter=0;
        dist_sensor_counter=0;



    }


    public int getBicep_counter() {
        return bicep_counter;
    }

    public int getForearm_counter() {
        return forearm_counter;
    }

    public int getTriceps_counter() {
        return triceps_counter;
    }

    public int getDist_Sensor_counter() {return dist_sensor_counter; }

    //if mode is equal to 1 then counters are incremented else --> reset

    public boolean setBicep_counter(int mode) {
        if (mode==1 && bicep_counter<2)
            bicep_counter++;
        else if (mode==0 && bicep_counter>0)
            bicep_counter--;

        if(bicep_counter==0)
            return true;
        else
            return false;
    }

    public boolean setForearm_counter(int mode) {
        if (mode==1 && forearm_counter<2)
            forearm_counter++;
        else if (mode==0 && forearm_counter>0)
            forearm_counter--;

        if(forearm_counter==0)
            return true;
        else
            return false;
    }


    public boolean setTriceps_counter(int mode) {
        if (mode==1 && triceps_counter<2)
            triceps_counter++;
        else if (mode==0 && triceps_counter>0)
            triceps_counter--;


        if(triceps_counter==0 )
            return true;
        else
            return false;
    }


    public boolean setDist_Sensor_counter(int mode) {
        if (mode==1 && dist_sensor_counter<9)
            dist_sensor_counter++;
        else if (mode==0 && dist_sensor_counter>0)
            dist_sensor_counter--;


        if(dist_sensor_counter==0 )
            return true;
        else
            return false;
    }


}


