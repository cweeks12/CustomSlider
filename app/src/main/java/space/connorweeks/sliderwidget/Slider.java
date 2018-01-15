package space.connorweeks.sliderwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mike on 6/14/2017.
 * see https://github.com/anothem/android-range-seek-bar/blob/master/rangeseekbar/src/main/java/org/florescu/android/rangeseekbar/RangeSeekBar.java
 */

public class Slider extends View
{

    private PointF circleCenter;
    private PointF viewTopLeft;
    private PointF viewBottomRight;
    Paint myPaint;

    public Slider(Context context) {
        super(context);
        circleCenter = new PointF(0f,0f);
        viewTopLeft = new PointF(this.getLeft(),this.getRight());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        myPaint = new Paint();
        myPaint.setColor(0xff101010);
        myPaint.setAntiAlias(true);
        myPaint.setTextSize(90f);
        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        canvas.drawCircle(circleCenter.x,circleCenter.y,50f,myPaint);
        drawLineFromPoints (viewTopLeft, viewBottomRight,canvas,myPaint);

    }

    private void drawLineFromPoints(PointF viewTopLeft, PointF viewBottomRight, Canvas canvas, Paint myPaint) {
        canvas.drawLine(viewTopLeft.x,viewTopLeft.y,viewBottomRight.x,viewBottomRight.y,myPaint);

    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // get the location of the finger down.
                circleCenter.x = event.getX();
                circleCenter.y = event.getY();
                // draw it on the screen.
                break;
        }


        invalidate(); // make sure we force a redraw.
        return true;
    }
}