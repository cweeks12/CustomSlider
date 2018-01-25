package space.connorweeks.sliderwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 6/14/2017.
 * see https://github.com/anothem/android-range-seek-bar/blob/master/rangeseekbar/src/main/java/org/florescu/android/rangeseekbar/RangeSeekBar.java
 */

public class Slider extends View
{

    private float epsilon = 4;

    public interface SliderListener {
        public void onValueChanged(List<Float> values, Slider slider);
    }

    private List<PointF> circleCenters;
    private PointF viewTopLeft;
    private PointF viewBottomRight;
    private Integer selectedCircle = null;

    private float percentageOfScreen;



    private float radiusOfThumb;
    private float topMargin;
    private List<Float> values;
    private float minValue;
    private float maxValue;
    private float verticalPadding;
    Paint myPaint;

    private ArrayList<SliderListener> listeners;

    public Slider(Context context, float thumbRadius, float verticalPadding, int numberOfThumbs, float minValue, float maxValue
                        , float screenPercent) {
        super(context);
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());

        listeners = new ArrayList<>();

        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setAntiAlias(true);

        radiusOfThumb = thumbRadius;
        this.verticalPadding = verticalPadding;
        topMargin = epsilon + radiusOfThumb;

        this.minValue = minValue;
        this.maxValue = maxValue;

        circleCenters = new ArrayList<>();
        values = new ArrayList<>();

        for (int i=0; i < numberOfThumbs; i++){
            values.add(minValue);
            circleCenters.add(new PointF(viewTopLeft.x + radiusOfThumb ,viewTopLeft.y+topMargin));
        }

        percentageOfScreen = screenPercent;
        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = 200;
        if ( MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED ){
            width = (int) (MeasureSpec.getSize(widthMeasureSpec) * percentageOfScreen / 100);
        }

        int height = (int) (radiusOfThumb * 2f + verticalPadding * 2);
        if ( MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED ){
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());
        Log.d ("seek","on draw");
        System.out.println(viewBottomRight);
        System.out.println(viewTopLeft);

        super.onDraw(canvas);

        for (PointF p : circleCenters) {
            canvas.drawCircle(p.x, p.y, radiusOfThumb, myPaint);
        }
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
                selectedCircle = getSelectedCircle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                // get the location of the finger down.
                if (selectedCircle != null) {
                    if (event.getX() < viewTopLeft.x+radiusOfThumb){
                        circleCenters.get(selectedCircle).x = viewTopLeft.x+radiusOfThumb;
                    }
                    else if (event.getX() > viewBottomRight.x-radiusOfThumb){
                        circleCenters.get(selectedCircle).x = viewBottomRight.x-radiusOfThumb;
                    }
                    else{
                        circleCenters.get(selectedCircle).x = event.getX();
                    }
                    circleCenters.get(selectedCircle).y = viewTopLeft.y + topMargin;
                    values.set(selectedCircle, minValue + (maxValue - minValue) *
                            ((circleCenters.get(selectedCircle).x - (viewTopLeft.x+radiusOfThumb))
                            / ((viewBottomRight.x-radiusOfThumb) - (viewTopLeft.x+radiusOfThumb))));
                    for (SliderListener l : listeners){
                        l.onValueChanged(values, this);
                    }
                }
                // draw it on the screen.
                break;
            case MotionEvent.ACTION_UP:
                selectedCircle = null;
                break;
        }


        invalidate(); // make sure we force a redraw.
        return true;
    }

    private double getDistanceFromCircle(float x, float y, PointF circleLocation){
        return Math.sqrt(Math.pow(x - circleLocation.x, 2) + Math.pow(y - circleLocation.y, 2));
    }

    private Integer getSelectedCircle(float x, float y) {

        double minimumDistance = 99;
        Integer returnValue = null;
        for (int i = 0; i < circleCenters.size(); i++) {
            double distance = getDistanceFromCircle(x, y, circleCenters.get(i));
            if (distance < minimumDistance && distance < radiusOfThumb + epsilon) {
                minimumDistance = distance;
                returnValue = i;
            }
        }
        return returnValue;
    }

    private boolean touchedInsideTheCircle(PointF touchLocation){
        return true;
    }

    public void registerAsListener(SliderListener listener){
        listeners.add(listener);
    }

    public float getRadiusOfThumb() {
        return radiusOfThumb;
    }

    public void setRadiusOfThumb(float radiusOfThumb) {
        this.radiusOfThumb = radiusOfThumb;
        invalidate();
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
}