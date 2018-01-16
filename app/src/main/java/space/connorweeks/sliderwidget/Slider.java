package space.connorweeks.sliderwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
    private float radiusOfThumb;
    private float topMargin;
    private boolean isMoving = false;
    private float value;
    private float minValue = 0;
    private float maxValue = 100;
    Paint myPaint;

    public Slider(Context context) {
        super(context);
        viewTopLeft = new PointF(this.getLeft(),this.getRight());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());

        myPaint = new Paint();
        myPaint.setColor(0xff101010);
        myPaint.setAntiAlias(true);
        myPaint.setTextSize(90f);

        radiusOfThumb = 50f;
        topMargin = 10f+ radiusOfThumb;

        circleCenter = new PointF(viewTopLeft.x + radiusOfThumb,viewTopLeft.y+topMargin);

        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        if (isMoving){
            myPaint.setColor(Color.RED);
        }
        else {
            myPaint.setColor(Color.BLACK);
        }
        canvas.drawCircle(circleCenter.x,circleCenter.y,radiusOfThumb,myPaint);
        drawLineFromPoints (new PointF(viewTopLeft.x, viewTopLeft.y + topMargin),
                            new PointF(viewBottomRight.x, viewTopLeft.y + topMargin),canvas,myPaint);

    }

    private void drawLineFromPoints(PointF viewTopLeft, PointF viewBottomRight, Canvas canvas, Paint myPaint) {
        canvas.drawLine(viewTopLeft.x,viewTopLeft.y,viewBottomRight.x,viewBottomRight.y,myPaint);

    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!touchedInsideTheCircle(new PointF(event.getX(), event.getY()))){
                    break;
                }
                isMoving = true;
            case MotionEvent.ACTION_MOVE:
                // get the location of the finger down.
                if (isMoving) {
                    circleCenter.x = event.getX();
                    circleCenter.y = viewTopLeft.y + topMargin;
                }
                // draw it on the screen.
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;

        }


        invalidate(); // make sure we force a redraw.
        return true;
    }

    private boolean touchedInsideTheCircle(PointF touchLocation){
        return (double) radiusOfThumb + 4 > Math.sqrt(Math.pow(touchLocation.x - circleCenter.x, 2) + Math.pow(touchLocation.y - circleCenter.y, 2));
    }

    private float getValue(){
        return value;
    }
}