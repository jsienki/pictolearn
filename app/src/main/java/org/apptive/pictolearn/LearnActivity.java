package org.apptive.pictolearn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class LearnActivity extends Activity {

    public static Bitmap mBackup;
    private static ImageView mImgLearn;
    private static DataObject dataRead;
    private static int dataSize;
    private static Button startButton;
    private static ArrayList<DataObjectLoad> dataReadArray;
    private int mPlaceHolder, mScore;
    private Button mLoadButton;
    private TextView mQuestionView;
    private TextView mScoreView;
    private Toast mToast;
    private RelativeLayout relLayout;

    public static void loadData(File image, File data) {
        // Refer to the loadData method in EditActivity for description

        Uri imageUri = Uri.fromFile(image);

        mImgLearn.setImageURI(imageUri);


        try {

            FileInputStream fis = new FileInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(fis);

            dataRead = (DataObject) ois.readObject();

            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found");
        } catch (IOException e) {
            Log.d("Error", "Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Type listTypeDouble = new TypeToken<ArrayList<Double>>() {
        }.getType();
        Type listTypeString = new TypeToken<ArrayList<String>>() {
        }.getType();

        ArrayList<Double> dXCords = new Gson().fromJson(dataRead.xCords, listTypeDouble);
        ArrayList<Double> dYCords = new Gson().fromJson(dataRead.yCords, listTypeDouble);
        ArrayList<String> dText = new Gson().fromJson(dataRead.text, listTypeString);

        dataSize = dXCords.size();

        dataReadArray = new ArrayList<>();

        for (int i = 0; i < dataSize; i++) {
            dataReadArray.add(new DataObjectLoad(dText.get(i), dXCords.get(i), dYCords.get(i)));
        }

        BitmapDrawable drawable = (BitmapDrawable) mImgLearn.getDrawable();
        mBackup = drawable.getBitmap();

        startButton.setEnabled(true);
    }

    public void saveData(int points) {
        // Refer to the fileSave method in EditActivity for description

        String dPattern = "MM/dd/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dPattern, Locale.ENGLISH);
        Date date = new Date();
        String sDate = formatter.format(date);

        DataObject toSave = dataRead;

        toSave.lastScoreP = points;
        toSave.lastDate = sDate;

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("data", Context.MODE_PRIVATE);


        File file = new File(dir, toSave.name + ".bin");

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(toSave);

            oos.close();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found");
        } catch (IOException e) {
            Log.d("Error", "Error initializing stream");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in MainActivity for description

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        setViews();
        loadExtras();
        resetScore();

    }

    public void loadExtras() {
        // Intent objects can contain so called "Extras". They are useful to pass data between
        // activities without using static variables. Here extras are used to determine if the
        // activity was started from the Recent tab in the main menu, or if the activity was started
        // by clicking the "Learn!" button. The difference is that by starting via the Recent tab
        // you've already picked the data set you want, so data is loaded from the "images"
        // and "data" key.

        Bundle mBundle = getIntent().getExtras();

        if (mBundle == null) {
            Intent i = new Intent(LearnActivity.this, LearnPopup.class);
            i.putExtra("key", 1);
            startActivity(i);
        } else {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File imgDir = cw.getDir("images", Context.MODE_PRIVATE);
            File dataDir = cw.getDir("data", Context.MODE_PRIVATE);

            String imageFile = mBundle.getString("image");
            String dataFile = mBundle.getString("data");

            File image = new File(imgDir, imageFile);
            File data = new File(dataDir, dataFile);

            loadData(image, data);
        }
    }

    public void setViews() {
        // Refer to the setButtonFunction method in MainActivity for description

        mImgLearn = findViewById(R.id.imgLearn);
        mQuestionView = findViewById(R.id.questionView);
        startButton = findViewById(R.id.startButton);
        mLoadButton = findViewById(R.id.loadButton);
        mScoreView = findViewById(R.id.scoreView);
        Button mQuitButton = findViewById(R.id.quitButton);

        startButton.setEnabled(false);

        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                mLoadButton.setEnabled(false);
                mImgLearn.setImageDrawable(new BitmapDrawable(getResources(), mBackup));
                mPlaceHolder = 0;
                Collections.shuffle(dataReadArray);
                resetScore();
                setPoints();
                play();
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LearnActivity.this, LearnPopup.class);
                i.putExtra("key", 1);
                startActivity(i);

                mPlaceHolder = 0;
                resetScore();
            }
        });
    }


    public void play() {
        // This method is used to display the right text in the TextView on the bottom of the
        // screen, so the user knows which point he should tap on. The "dataSize" variable tells
        // us how big the dataSet is, if it is 0 the learning procedure doesn't start.

        if (dataSize > 0) {

            mQuestionView.setText(dataReadArray.get(mPlaceHolder).mText);

        } else {
            mLoadButton.setEnabled(true);
        }


    }

    public void setPoints() {
        // This method creates all the points of the data set into the layout which is on top of the
        // image (relLayout). Every image has a listener, which checks if the image clicked is the
        // right one. The position is calculated with the Matrix library, for further reference
        // read this documentation: https://developer.android.com/reference/android/graphics/Matrix

        relLayout = findViewById(R.id.relLayout);
        mImgLearn = findViewById(R.id.imgLearn);

        mImgLearn.setImageDrawable(new BitmapDrawable(getResources(), mBackup));

        Matrix imgMatrix = new Matrix();

        mImgLearn.getImageMatrix().invert(imgMatrix);

        float[] points = new float[2];

        for (int i = 0; i < dataReadArray.size(); i++) {
            final int dataNum = i;
            final ImageView image = new ImageView(this);

            Bitmap tempBitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);
            Canvas tempCanvas = new Canvas(tempBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            tempCanvas.drawCircle(32, 32, 32, paint);
            paint.setColor(Color.rgb(255, 165, 0));
            tempCanvas.drawCircle(32, 32, 28, paint);

            image.setImageBitmap(tempBitmap);

            relLayout.addView(image);

            points[0] = dataReadArray.get(i).mCordX.floatValue();
            points[1] = dataReadArray.get(i).mCordY.floatValue();

            int cordX = (int) points[0];
            int cordY = (int) points[1];

            float[] f = new float[9];
            imgMatrix.getValues(f);

            float scaleX = f[Matrix.MSCALE_X];
            float scaleY = f[Matrix.MSCALE_Y];
            float transX = f[Matrix.MTRANS_X];
            float transY = f[Matrix.MTRANS_Y];

            image.setX((cordX - transX) / scaleX);
            image.setY((cordY - transY) / scaleY);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < relLayout.getChildCount(); i++) {
                        ImageView tempImage = (ImageView) relLayout.getChildAt(i);
                        tempImage.setColorFilter(null);
                    }
                    image.setColorFilter(Color.RED);
                    if (dataNum == mPlaceHolder) {
                        makeToast(getResources().getString(R.string.checkCorrect), false);
                        mScore++;
                    } else {
                        makeToast(getResources().getString(R.string.checkWrong), false);
                        for (int i = 0; i < relLayout.getChildCount(); i++) {
                            if (i == mPlaceHolder) {
                                ImageView tempImage = (ImageView) relLayout.getChildAt(i);
                                tempImage.setColorFilter(Color.GREEN);
                            }
                        }
                    }

                    mScoreView.setText(String.format(getResources().getString(R.string.scoreText2), mScore));
                    mPlaceHolder++;
                    if (mPlaceHolder < dataSize) {
                        play();
                    } else if (mPlaceHolder == dataSize) {
                        relLayout.removeAllViews();
                        double pScorePercentage = (double) mScore / dataSize * 100;
                        int scorePercentage = (int) pScorePercentage;
                        endDialog(scorePercentage);

                        mQuestionView.setText(getResources().getString(R.string.dataDone));
                        saveData(mScore);
                        mPlaceHolder = 0;
                        startButton.setEnabled(true);
                        mLoadButton.setEnabled(true);
                    }
                }
            });
        }
    }

    public void resetScore() {
        // This method resets the score and the "mPlaceHolder" variable which controls which
        // question is being displayed. It also updates the "mScoreView" which is a simple TextView.

        mScore = 0;
        mPlaceHolder = 0;
        mScoreView.setText(String.format(getResources().getString(R.string.scoreText2), mScore));
    }

    public void endDialog(int scorePercentage) {
        // This method creates a dialog at the end of every data set to show the user how many points
        // he scored and also the percentage of the points he achieved.

        AlertDialog.Builder builder1 = new AlertDialog.Builder(LearnActivity.this, R.style.MyDialogTheme);
        //builder1.setMessage("Score: " + mScore + "; Score Percentage: " + scorePercentage + "%");
        builder1.setMessage(String.format(getResources().getString(R.string.endDialog), mScore, scorePercentage, "%"));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getResources().getString(R.string.okButtonText),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void makeToast(String message, Boolean length) {
        // Refer to the makeToast method in EditActivity for description

        if (mToast != null) {
            mToast.cancel();
        }

        if (length) {
            mToast = Toast.makeText(LearnActivity.this, message, Toast.LENGTH_LONG);
        } else {
            mToast = Toast.makeText(LearnActivity.this, message, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }

}
