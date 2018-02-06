package space.connorweeks.sliderwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
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
    // Make the API simpler
    // one line in ACTION_MOVE, finding the next thumb position
    // Use and ARC for the curve
    // There is math on dropbox
    // Perfect math
    // Finger origin & finger position gives a ray
    // Where do ray and path intersect?
    // Make an equation for the curve, plug in the equation of the ray, solve for t
    // t is the ray parameter
    // r(t) = r_o + r_d * t
    // make sure r_d is normalized ||r_d|| = 1


    private float epsilon = 4;

    public interface SliderListener {
        public void onValueChanged(List<Float> values, Slider slider);
    }

    private List<PointF> circleCenters;
    private PointF viewTopLeft;
    private PointF viewBottomRight;
    private Integer selectedCircle = null;


    private float radiusOfThumb;
    private float topMargin;
    private List<Float> values;
    private float minValue;
    private float maxValue;
    private float verticalPadding;
    Paint myPaint;
    Paint linePaint;

    private ArrayList<SliderListener> listeners;

    public Slider(Context context, float thumbRadius, float verticalPadding, int numberOfThumbs, float minValue, float maxValue) {
        super(context);
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());

        listeners = new ArrayList<>();

        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(3f);


        radiusOfThumb = thumbRadius;
        this.verticalPadding = verticalPadding;
        topMargin = epsilon + radiusOfThumb;

        this.minValue = minValue;
        this.maxValue = maxValue;

        circleCenters = new ArrayList<>();
        values = new ArrayList<>();

        for (int i=0; i < numberOfThumbs; i++){
            values.add(maxValue);
            circleCenters.add(new PointF(viewTopLeft.x ,viewTopLeft.y));
        }

        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = 200;
        if ( MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED ){
            width = (int) (MeasureSpec.getSize(widthMeasureSpec));
        }

        int height = (int) (radiusOfThumb * 2f + verticalPadding * 2);
        if ( MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED ){
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        viewTopLeft = new PointF(this.getLeft(),this.getTop());
        viewBottomRight = new PointF(this.getRight(),this.getBottom());

        super.onDraw(canvas);

        for (PointF p : circleCenters) {
            canvas.drawCircle(p.x, p.y, radiusOfThumb, myPaint);
        }

        canvas.drawArc(-this.getRight(), this.getTop(), this.getRight(), this.getBottom()*2, 270f, 90f, false, linePaint);
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
                    float xValue = event.getX();
                    float yValue = event.getY();

                    if (event.getX() < viewTopLeft.x){
                        xValue = viewTopLeft.x;
                    }
                    if (event.getY() > viewBottomRight.y){
                        yValue = viewBottomRight.y;
                    }
                    calculateNewCenter(xValue, yValue, selectedCircle);

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

    private double length(PointF vector){
        return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
    }

    private void calculateNewCenter(float x, float y, Integer selectedCircle) {
        // Get a unit vector from origin to point
        // Find the point on the parabola corresponding to the angle.
        PointF vector = new PointF(x - this.viewTopLeft.x, y - this.viewBottomRight.y);
        double magnitude = length(vector);
        vector.x /= magnitude;
        vector.y /= magnitude;

        // This gives an angle from 0 degrees
        double theta = Math.atan(Math.abs(vector.y) / Math.abs(vector.x));

        float a = viewBottomRight.x - viewTopLeft.x;
        float b = viewBottomRight.y - viewTopLeft.y;

        double radius = (a*b) / Math.sqrt((Math.pow(a, 2) * Math.pow(Math.sin(theta), 2)) + (Math.pow(b, 2) * Math.pow(Math.cos(theta), 2)));

        PointF newGuy = new PointF((float)(this.viewTopLeft.x + radius * Math.cos(theta)), (float)(this.viewBottomRight.y - radius * Math.sin(theta)));

        circleCenters.get(selectedCircle).x = newGuy.x;
        circleCenters.get(selectedCircle).y = newGuy.y;
        values.set(selectedCircle, (float)(minValue + (maxValue - minValue) * theta * 180f  / (3.14159f * 90f)));
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