package space.connorweeks.sliderwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate my two thumbed seekbar.
        Slider slider = new Slider(this);

        // add it to the layout.
        FrameLayout layout = (FrameLayout) findViewById (R.id.seekbar_placeholder);
        layout.addView(slider);

    }
}