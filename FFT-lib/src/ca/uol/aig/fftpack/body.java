package ca.uol.aig.fftpack;

import android.graphics.drawable.Drawable;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class body extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.body_display,container,false);

        return view;
    }

    int BICEP_FRQ;
    int TRICEPS_FRQ;
    int FOREARM_FRQ;

    public static boolean isBicepActive=false;
    public static boolean isTricepsActive=false;
    public static boolean isForearmActive=false;

    private int bicep_counter;
    private int triceps_counter;
    private int forearm_counter;

    public Drawable BICEP;
    public Drawable TRICEPS;
    public Drawable FOREARM;



    public body(int BICEP_FRQ, int TRICEPS_FRQ, int FOREARM_FRQ, Drawable BICEP, Drawable TRICEPS, Drawable FOREARM){

        this.BICEP_FRQ=BICEP_FRQ;
        this.TRICEPS_FRQ=TRICEPS_FRQ;
        this.FOREARM_FRQ=FOREARM_FRQ;

        this.BICEP=BICEP;
        this.TRICEPS=TRICEPS;
        this.FOREARM=FOREARM;

        bicep_counter=0;
        triceps_counter=0;
        forearm_counter=0;

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

    //if mode is equal to 1 then counters are incremented else --> reset

    public void setBicep_counter(int mode) {
        if (mode==1)
            bicep_counter++;
        else if (mode==0)
            bicep_counter=0;
    }

    public void setForearm_counter(int mode) {
        if (mode==1)
            forearm_counter++;
        else if (mode==0)
            forearm_counter=0;
    }


    public void setTriceps_counter(int mode) {
        if (mode==1)
            triceps_counter++;
        else if (mode==0)
            triceps_counter=0;
    }
}

