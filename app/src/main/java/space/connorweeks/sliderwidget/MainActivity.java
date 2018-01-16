package space.connorweeks.sliderwidget;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Slider.SliderListener{

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        final Slider slider = new Slider(this);
        slider.registerAsListener(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(slider);

    }

    @Override
    public void onValueChanged(int value, Slider slider){
        // Update the seekbar
        ((TextView)findViewById(R.id.sliderValue)).setText(String.valueOf(value));
    }

}