package org.apptive.pictolearn;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class ColorPopup extends Activity {
    // This class controls the color picker popup which comes up when you click on the color picker
    // in the editor.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in MainActivity for description
        // The DisplayMetrics are used to size down the activity so it acts like a popup.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_popup);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        setButtons();
    }

    public void setButtons() {
        // Refer to the setButtons method in MainActivity for description

        Button mBtnYellow = findViewById(R.id.yellowButton);
        Button mBtnBlue = findViewById(R.id.blueButton);
        Button mBtnErase = findViewById(R.id.eraserButton);
        Button mBtnClose = findViewById(R.id.closeBtn);

        mBtnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.setColor(1);
                finish();

            }
        });

        mBtnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.setColor(2);
                finish();

            }
        });

        mBtnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.setColor(0);
                finish();
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }


}
