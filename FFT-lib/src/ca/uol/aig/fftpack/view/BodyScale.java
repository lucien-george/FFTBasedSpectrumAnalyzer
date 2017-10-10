package ca.uol.aig.fftpack.view;

/**
 * Created by leotardn on 2017-03-05.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.util.DisplayMetrics;

public class BodyScale extends ImageView {

    Context mContext;

    int width;
    int height;
    float radius;
    DisplayMetrics displayM;
    Paint paint;
    Path path;
    float center_x, center_y;
    final RectF oval = new RectF();

    Bitmap bitmapScale;
    Canvas canvasScale;

    public BodyScale(Context context) {
        super(context);
        this.mContext = context;
        initialize();
    }

    public BodyScale(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initialize();
    }

    public BodyScale(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initialize();
    }



    private void initialize(){

        if (!isInEditMode()) {

            displayM = this.getResources().getDisplayMetrics();
            width = displayM.widthPixels;
            height = (displayM.heightPixels)/4;

            bitmapScale = Bitmap.createBitmap(width, 500, Bitmap.Config.ARGB_8888);

            path = new Path();
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);

            canvasScale = new Canvas(bitmapScale);
            setImageBitmap(bitmapScale);
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (width > height) {
            radius = height / 4;
        } else {
            radius = width / 4;
        }


        path.addCircle(width / 2,
                height / 2, radius,
                Path.Direction.CW);


        paint.setColor(Color.YELLOW);
        canvas.drawColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);

        paint.setStyle(Paint.Style.STROKE);

        center_x = width / 2;
        center_y = height / 2;

        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);
        canvas.drawArc(oval, 90, 180, false, paint);

    }
}
