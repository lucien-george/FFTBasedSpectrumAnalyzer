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

    int width , height; // width and height of screen
    int forearm_y , forearm_x; // forearm x and y coordinates
    DisplayMetrics displayM; // device display
    public Paint paintBicep , paintTriceps , paintForearm; // paint object for muscles

    Bitmap bitmapBody , bitmapBicep , bitmapTriceps , bitmapForearm; // bitmap for entire body and each individual muscles
    Canvas canvasBicep , canvasTriceps , canvasForearm; // canvas for each muscle
    Handler handler;

    // constructor
    public drawBody(Context context){
        super(context);
        this.mContext = context;
        initialize();
    }

    private void initialize(){

        if (!isInEditMode()) {

            displayM = this.getResources().getDisplayMetrics();
            width = displayM.widthPixels; // get screen width
            height = (displayM.heightPixels); // get screen height

            bitmapBody = Bitmap.createBitmap(width, 800, Bitmap.Config.ARGB_8888); // create bitmap for body
//            bitmapBicep = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);
//            bitmapTriceps = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);
//            bitmapForearm = Bitmap.createBitmap(width/3,800, Bitmap.Config.ARGB_8888);

            paintBicep = new Paint(); // initialize paint
            paintBicep.setColor(Color.GREEN); // set color to green
            paintBicep.setStyle(Paint.Style.FILL_AND_STROKE); // set style
            paintBicep.setStrokeWidth(15); // set width

            paintTriceps = new Paint(); // initialize paint
            paintTriceps.setColor(Color.GREEN); // set color to green
            paintTriceps.setStyle(Paint.Style.FILL_AND_STROKE); // set style
            paintTriceps.setStrokeWidth(15); // set width

            paintForearm = new Paint(); // initialize paint
            paintForearm.setColor(Color.GREEN); // set color to green
            paintForearm.setStyle(Paint.Style.FILL_AND_STROKE); // set style
            paintForearm.setStrokeWidth(20); // set width

            canvasBicep = new Canvas(bitmapBody); // add canvas to bitmap
            canvasTriceps = new Canvas(bitmapBody); // add canvas to bitmap
            canvasForearm = new Canvas(bitmapBody); // add canvas to bitmap
//            canvasBicep = new Canvas(bitmapBicep);
//            canvasTriceps = new Canvas(bitmapTriceps);
//            canvasForearm = new Canvas(bitmapForearm);

            setImageBitmap(bitmapBody); // display bitmap?
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

        canvasBicep.drawLine(x + 80,y,(x+240),y+350,paintBicep); // draw bicep at specific coordinates


        canvasTriceps.drawLine(x,y,(x+160),y+350,paintTriceps); // draw tricep at specific coordinates


        canvasForearm.drawLine(x+240,y+355,x+550,y+400,paintForearm); // draw forearm at specific coordinates

        forearm_y = y + 355;
        forearm_x = x + 240;
    }

    public void setPaintBicep(boolean active) {
        if(active) {
            paintBicep.setColor(Color.RED); // set color of bicep to red
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
            paintBicep.setColor(Color.GREEN); // set color of bicep to green
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
            paintTriceps.setColor(Color.RED); // set color of triceps to red
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintTriceps,"color", new ArgbEvaluator(),  paintTriceps.getColor() , Color.RED);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
        else{
            paintTriceps.setColor(Color.GREEN); // set color of triceps to green
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
            paintForearm.setColor(Color.RED); // set color of forearm to red
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.RED);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
        else{
            paintForearm.setColor(Color.GREEN); // set color of forearm to green
//            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.GREEN);
//            colorFade.setInterpolator(new LinearInterpolator());
//            colorFade.setDuration(4000);
//            AnimatorSet t = new AnimatorSet();
//            t.play(colorFade);
//            t.start();
        }
    }
}