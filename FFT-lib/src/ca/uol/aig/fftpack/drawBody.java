package ca.uol.aig.fftpack;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.graphics.PorterDuff.Mode;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by leotardn on 2017-03-21.
 */

public class drawBody extends ImageView {

    Context mContext;

    int width;
    int height;
    int forearm_y;
    int forearm_x;
    DisplayMetrics displayM;
    public Paint paintBicep;
    public Paint paintTriceps;
    public Paint paintForearm;


    Bitmap bitmapBody;
    Bitmap bitmapBicep;
    Bitmap bitmapTriceps;
    Bitmap bitmapForearm;
    Canvas canvasBicep;
    Canvas canvasTriceps;
    Canvas canvasForearm;
    Handler handler;

    public drawBody(Context context){
        super(context);
        this.mContext = context;
        initialize();
    }

    private void initialize(){

        if (!isInEditMode()) {

            displayM = this.getResources().getDisplayMetrics();
            width = displayM.widthPixels;
            height = (displayM.heightPixels);

            bitmapBody = Bitmap.createBitmap(width, 800, Bitmap.Config.ARGB_8888);
//            bitmapBicep = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);
//            bitmapTriceps = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);
//            bitmapForearm = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);

            paintBicep = new Paint();
            paintBicep.setColor(Color.GREEN);
            paintBicep.setStyle(Paint.Style.FILL_AND_STROKE);
            paintBicep.setStrokeWidth(15);

            paintTriceps = new Paint();
            paintTriceps.setColor(Color.GREEN);
            paintTriceps.setStyle(Paint.Style.FILL_AND_STROKE);
            paintTriceps.setStrokeWidth(15);

            paintForearm = new Paint();
            paintForearm.setColor(Color.GREEN);
            paintForearm.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForearm.setStrokeWidth(20);

            canvasBicep = new Canvas(bitmapBody);
            canvasTriceps = new Canvas(bitmapBody);
            canvasForearm = new Canvas(bitmapBody);
//            canvasBicep = new Canvas(bitmapBicep);
//            canvasTriceps = new Canvas(bitmapTriceps);
//            canvasForearm = new Canvas(bitmapForearm);

            setImageBitmap(bitmapBody);
//            setImageBitmap(bitmapBicep);
//            setImageBitmap(bitmapTriceps);
//            setImageBitmap(bitmapForearm);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x=width/3;
        int y=0;

        if (canvasBicep == null || canvasTriceps == null || canvasForearm == null) {
            return;
        }

        canvasBicep.drawLine(x + 80,y,(x+240),y+350,paintBicep);


        canvasTriceps.drawLine(x,y,(x+160),y+350,paintTriceps);


        canvasForearm.drawLine(x+240,y+355,x+550,y+400,paintForearm);

        forearm_y=y+355;
        forearm_x=x+240;
    }

//    public void dRotate(float degrees){
//        canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        canvasForearm.save();
//        canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear everything drawn to the bitmap
//        canvasForearm.rotate(degrees, forearm_x, forearm_y);
//        canvasForearm.drawLine(forearm_x, forearm_y, forearm_x+330, forearm_y, paintForearm);
//        canvasForearm.restore();
//    }



    public void setPaintBicep(boolean active) {
        if(active) {
            paintBicep.setColor(Color.RED);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintBicep,"color", new ArgbEvaluator(),  paintBicep.getColor() , Color.RED);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
//            canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            canvasForearm.save();
//            Animation anim = new RotateAnimation(0f , -45f,forearm_x , forearm_y);
//            anim.setInterpolator(new LinearInterpolator());
//            anim.setDuration(4000);
//            startAnimation(anim);

//            canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear everything drawn to the bitmap
//            canvasForearm.rotate(-45, forearm_x, forearm_y);
//            canvasForearm.restore();
        }
        else {
            paintBicep.setColor(Color.GREEN);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintBicep,"color", new ArgbEvaluator(),  paintBicep.getColor() , Color.GREEN);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
//            canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            canvasForearm.save();
//            Animation anim = new RotateAnimation(-45f , 0f,forearm_x , forearm_y);
//            anim.setInterpolator(new LinearInterpolator());
//            anim.setDuration(4000);
//            startAnimation(anim);

//            canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear everything drawn to the bitmap
//            canvasForearm.rotate(45, forearm_x, forearm_y);
//            canvasForearm.restore();
        }
}

    public void setPaintTriceps(boolean active){
        if(active){
            paintTriceps.setColor(Color.RED);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintTriceps,"color", new ArgbEvaluator(),  paintTriceps.getColor() , Color.RED);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
        else{
            paintTriceps.setColor(Color.GREEN);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintTriceps,"color", new ArgbEvaluator(),  paintTriceps.getColor() , Color.GREEN);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
    }

    public void setPaintForearm(boolean active){
        if(active){
            paintForearm.setColor(Color.RED);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.RED);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
        else{
            paintForearm.setColor(Color.GREEN);
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.GREEN);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
    }
}

