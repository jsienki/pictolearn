package org.apptive.pictolearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class ModePopup extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in ColorPopup for description

        super.onCreate(savedInstanceState);

        setContentView(R.layout.mode_popup);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.4));

        setButtons();
    }

    public void setButtons() {
        // Refer to the setButtonFunction method in MainActivity for description
        // This method uses extras to determine if it was started from the Recent Recycler and if
        // it needs to pass data on or not.

        Button mBtnPoint = findViewById(R.id.mode_point);
        Button mBtnAnswer = findViewById(R.id.mode_answer);
        Button mBtnClose = findViewById(R.id.closeBtn);

        mBtnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModePopup.this, LearnActivity.class);

                Bundle mBundle = getIntent().getExtras();

                if (mBundle != null) {
                    String imageFile = mBundle.getString("fileName") + ".jpg";
                    String dataFile = mBundle.getString("fileName") + ".bin";

                    i.putExtra("image", imageFile);
                    i.putExtra("data", dataFile);

                    startActivity(i);
                } else {
                    startActivity(i);
                }

                finish();

            }
        });

        mBtnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModePopup.this, LearnActivityAnswer.class);

                Bundle mBundle = getIntent().getExtras();

                if (mBundle != null) {
                    String imageFile = mBundle.getString("fileName") + ".jpg";
                    String dataFile = mBundle.getString("fileName") + ".bin";

                    i.putExtra("image", imageFile);
                    i.putExtra("data", dataFile);

                    startActivity(i);
                } else {
                    startActivity(i);
                }

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
