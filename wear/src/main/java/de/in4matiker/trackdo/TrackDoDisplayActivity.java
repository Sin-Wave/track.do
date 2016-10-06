package de.in4matiker.trackdo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TrackDoDisplayActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text);
    }
}
