package org.apptive.pictolearn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
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

public class LearnActivityAnswer extends Activity {

    public static Bitmap mBackup;
    private static ImageView mImgLearn;
    private static DataObject dataRead;
    private static int dataSize;
    private static Button startButton;
    private static Button mCheckButton;
    private static TextView mQuestionView;
    private static ArrayList<DataObjectLoad> dataReadArray;
    private int mPlaceHolder, mScore;
    private Button mLoadButton;
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
        mCheckButton.setEnabled(false);
    }

    public void saveData(int points) {
        // Refer to the fileSave method in EditActivity for description

        String dPattern = "MM/dd/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dPattern, Locale.ENGLISH);
        Date date = new Date();
        String sDate = formatter.format(date);

        DataObject toSave = dataRead;

        toSave.lastScoreA = points;
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
        setContentView(R.layout.activity_learn_answer);

        setViews();
        loadExtras();
        resetScore();
    }

    public void setViews() {
        // Refer to the setButtonFunction method in MainActivity for description

        mImgLearn = findViewById(R.id.aImgLearn);
        startButton = findViewById(R.id.aStartButton);
        mLoadButton = findViewById(R.id.aLoadButton);
        mCheckButton = findViewById(R.id.aCheckButton);
        mQuestionView = findViewById(R.id.aAnswerView);
        mScoreView = findViewById(R.id.aScoreView);
        Button quitButton = findViewById(R.id.aQuitButton);

        startButton.setEnabled(false);
        mCheckButton.setEnabled(false);
        mQuestionView.setEnabled(false);

        quitButton.setOnClickListener(new View.OnClickListener() {
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
                Collections.shuffle(dataReadArray);
                resetScore();
                play();
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LearnActivityAnswer.this, LearnPopup.class);
                i.putExtra("key", 2);
                startActivity(i);
                resetScore();
            }
        });
    }

    public void loadExtras() {
        // Refer to the loadExtras method in LearnActivity for description

        Bundle mBundle = getIntent().getExtras();

        if (mBundle == null) {
            Intent i = new Intent(LearnActivityAnswer.this, LearnPopup.class);
            i.putExtra("key", 2);
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

    @SuppressLint("ClickableViewAccessibility")
    public void play() {
        // Refer to the play method in LearnActivity for description
        // The play and setPoints method is merged here, because in this mode only one point
        // is displayed at one time.

        relLayout = findViewById(R.id.relLayout);
        mQuestionView.setEnabled(true);
        mCheckButton.setEnabled(true);

        if (dataSize > 0) {
            relLayout.removeAllViews();
            mImgLearn.setImageDrawable(new BitmapDrawable(getResources(), mBackup));

            Matrix imgMatrix = new Matrix();
            mImgLearn.getImageMatrix().invert(imgMatrix);

            final ImageView image = new ImageView(this);

            Bitmap tempBitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_4444);
            Canvas tempCanvas = new Canvas(tempBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            tempCanvas.drawCircle(24, 24, 24, paint);
            paint.setColor(Color.RED);
            tempCanvas.drawCircle(24, 24, 20, paint);

            image.setImageBitmap(tempBitmap);

            relLayout.addView(image);

            float[] f = new float[9];
            imgMatrix.getValues(f);

            float scaleX = f[Matrix.MSCALE_X];
            float scaleY = f[Matrix.MSCALE_Y];
            float transX = f[Matrix.MTRANS_X];
            float transY = f[Matrix.MTRANS_Y];

            image.setX((dataReadArray.get(mPlaceHolder).mCordX.intValue() - transX) / scaleX);
            image.setY((dataReadArray.get(mPlaceHolder).mCordY.intValue() - transY) / scaleY);

            mCheckButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckButton.setOnClickListener(null);
                    checkAnswer(mQuestionView.getText().toString());
                }
            });
        }


    }

    public void checkAnswer(String answer) {
        // This method compares the answer (passed with the String "answer" with the answer saved in
        // the data set. It is not case sensitive.

        String compare;
        if (dataReadArray.get(mPlaceHolder).mText != null) {
            compare = dataReadArray.get(mPlaceHolder).mText.toLowerCase();
        } else {
            compare = "";
        }

        if (answer.toLowerCase().equals(compare)) {
            mScore++;
            makeToast(getResources().getString(R.string.checkCorrect), false);
        } else {
            makeToast(String.format(getResources().getString(R.string.checkWrong2), compare), false);
        }

        mScoreView.setText(String.format(getResources().getString(R.string.scoreText2), mScore));
        mQuestionView.setText("");
        mPlaceHolder++;

        if (mPlaceHolder < dataSize) {
            play();
        } else {
            double pScorePercentage = (double) mScore / dataSize * 100;
            int scorePercentage = (int) pScorePercentage;
            endDialog(scorePercentage);
            relLayout.removeAllViews();
            saveData(mScore);
            mPlaceHolder = 0;
            mCheckButton.setEnabled(false);
            mQuestionView.setEnabled(false);
            startButton.setEnabled(true);
            mLoadButton.setEnabled(true);
        }
    }

    public void resetScore() {
        // Refer to the resetScore method in LearnActivity for description

        mScore = 0;
        mPlaceHolder = 0;
        mScoreView.setText(String.format(getResources().getString(R.string.scoreText2), mScore));
    }

    public void endDialog(int scorePercentage) {
        // Refer to the endDialog method in LearnActivity for description

        AlertDialog.Builder builder1 = new AlertDialog.Builder(LearnActivityAnswer.this, R.style.MyDialogTheme);
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
            mToast = Toast.makeText(LearnActivityAnswer.this, message, Toast.LENGTH_LONG);
        } else {
            mToast = Toast.makeText(LearnActivityAnswer.this, message, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }

}
