package space.connorweeks.sliderwidget;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Slider.SliderListener{

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        final Slider slider = new Slider(this, 30f, 3f, 3, 19f, 59f, 50f);
        slider.registerAsListener(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(slider);

    }

    @Override
    public void onValueChanged(List<Float> values, Slider slider){
        // Update the seekbar
        String newValue = String.valueOf((int)values.get(0).floatValue());
        for (int i = 1; i < values.size(); i++){
            newValue += String.valueOf(",\t" + (int)values.get(i).floatValue());
        }
        ((TextView)findViewById(R.id.sliderValue)).setText(newValue);
    }

}