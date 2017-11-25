package ca.uol.aig.fftpack;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
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
    public Paint paintForearm0;
    public Paint paintForearm1;
    public Paint paintForearm2;
    public Paint paintForearm3;


    Bitmap bitmapBody;
    Canvas canvasBicep;
    Canvas canvasTriceps;
    Canvas canvasForearm;
    Canvas canvasForearm1;
    Canvas canvasForearm2;
    Canvas canvasForearm3;
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

            paintForearm0 = new Paint();
            paintForearm0.setColor(Color.GREEN);
            paintForearm0.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForearm0.setStrokeWidth(15);

            paintForearm1 = new Paint();
            paintForearm1.setColor(Color.BLACK);
            paintForearm1.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForearm1.setStrokeWidth(20);

            paintForearm2 = new Paint();
            paintForearm2.setColor(Color.BLACK);
            paintForearm2.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForearm2.setStrokeWidth(20);

            paintForearm3 = new Paint();
            paintForearm3.setColor(Color.BLACK);
            paintForearm3.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForearm3.setStrokeWidth(20);


            canvasBicep = new Canvas(bitmapBody);
            canvasTriceps = new Canvas(bitmapBody);
            canvasForearm = new Canvas(bitmapBody);

            canvasForearm1 = new Canvas(bitmapBody);

            canvasForearm2 = new Canvas(bitmapBody);

            canvasForearm3 = new Canvas(bitmapBody);
            setImageBitmap(bitmapBody);
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


        canvasForearm.drawLine(x+240,y+355,x+550,y+400,paintForearm0);

        //canvasForearm1.drawLine(x+240,y+355,x+500,y+310,paintForearm1);

        //canvasForearm2.drawLine(x+240,y+355,x+450,y+200,paintForearm2);

        //canvasForearm3.drawLine(x+240,y+355,x+320,y+100,paintForearm3);

        //setVisible(0);
        forearm_y=y+355;
        forearm_x=x+240;
    }

    public void dRotate(float degrees){
        canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvasForearm.save();
        canvasForearm.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear everything drawn to the bitmap
        canvasForearm.rotate(degrees, forearm_x, forearm_y);
        canvasForearm.drawLine(forearm_x, forearm_y, forearm_x+330, forearm_y, paintForearm);
        canvasForearm.restore();
    }

    public void setVisible(int position){
        int x=width/3;
        int y=0;

        paintForearm1 = new Paint();
        paintForearm1.setColor(Color.BLACK);
        paintForearm1.setStyle(Paint.Style.FILL_AND_STROKE);
        paintForearm1.setStrokeWidth(0);

        if(position==0){


            canvasForearm1.drawLine(x+240,y+355,x+500,y+310,paintForearm1);

            canvasForearm2.drawLine(x+240,y+355,x+450,y+200,paintForearm1);

            canvasForearm3.drawLine(x+240,y+355,x+320,y+100,paintForearm1);
            canvasForearm.drawLine(x+240,y+355,x+550,y+400,paintForearm);
        }else if(position==1) {

           /* canvasForearm.save();
            canvasForearm2.save();
            canvasForearm3.save();*/

            canvasForearm.drawLine(x+240,y+355,x+500,y+310,paintForearm1);

            canvasForearm2.drawLine(x+240,y+355,x+450,y+200,paintForearm1);

            canvasForearm3.drawLine(x+240,y+355,x+320,y+100,paintForearm1);
            canvasForearm1.drawLine(x+240,y+355,x+500,y+310,paintForearm);
        }else if(position==2) {

            /*canvasForearm1.save();
            canvasForearm.save();
            canvasForearm3.save();*/

            canvasForearm1.drawLine(x+240,y+355,x+500,y+310,paintForearm1);

            canvasForearm.drawLine(x+240,y+355,x+450,y+200,paintForearm1);

            canvasForearm3.drawLine(x+240,y+355,x+320,y+100,paintForearm1);
            canvasForearm2.drawLine(x+240,y+355,x+450,y+200,paintForearm);

        }else if(position==3) {

            /*canvasForearm1.save();
            canvasForearm2.save();
            canvasForearm.save();*/

            canvasForearm1.drawLine(x+240,y+355,x+500,y+310,paintForearm1);

            canvasForearm2.drawLine(x+240,y+355,x+450,y+200,paintForearm1);

            canvasForearm.drawLine(x+240,y+355,x+320,y+100,paintForearm1);


            canvasForearm3.drawLine(x+240,y+355,x+320,y+100,paintForearm);
        }

        invalidate();
    }



    public void setPaintBicep(boolean active) {
//    public void setPaintBicep(){
        if(active) {
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintBicep,"color", new ArgbEvaluator(),  paintBicep.getColor() , Color.RED);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintBicep.setColor(Color.RED);
        }
        else {
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintBicep,"color", new ArgbEvaluator(),  paintBicep.getColor() , Color.GREEN);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintBicep.setColor(Color.GREEN);
        }
}

    public void setPaintTriceps(boolean active){
        if(active){
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintTriceps,"color", new ArgbEvaluator(),  paintTriceps.getColor() , Color.RED);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintTriceps.setColor(Color.RED);
        }
        else{
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintTriceps,"color", new ArgbEvaluator(),  paintTriceps.getColor() , Color.GREEN);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintTriceps.setColor(Color.GREEN);
        }
    }

    public void setPaintForearm(boolean active){
        if(active){
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.RED);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintForearm.setColor(Color.RED);
        }
        else{
            ObjectAnimator    colorFade = ObjectAnimator.ofObject(paintForearm,"color", new ArgbEvaluator(),  paintForearm.getColor() , Color.GREEN);
            colorFade.setInterpolator(new LinearInterpolator());
            colorFade.setDuration(4000);
            AnimatorSet t = new AnimatorSet();
            t.play(colorFade);
            t.start();
//            paintForearm.setColor(Color.GREEN);
        }
    }
}

