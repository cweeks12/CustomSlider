package space.connorweeks.sliderwidget;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        final Slider slider = new Slider(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(slider);

        slider.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent unused){
                ((TextView)view.getRootView().findViewById(R.id.sliderValue)).setText(String.valueOf(slider.getValue()));
                return false;
            }
        });

    }

}