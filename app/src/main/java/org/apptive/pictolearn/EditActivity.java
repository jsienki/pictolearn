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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends Activity {
    // This class controls the behaviour of the editing screen.

    public static ArrayList<String> textList;
    public static ImageView imgView1;
    public static String imgName;
    public static Uri imgUri;
    public static int mPaintColor;
    private static ArrayList<ExampleItem> mExampleList;
    private static ArrayList<Integer> mXCordList;
    private static ArrayList<Integer> mYCordList;
    private static ExampleAdapter mAdapter;
    private static Button buttonAdd;
    private static Button buttonSave;
    private static Button buttonDelete;
    private static Button buttonExit;
    private static DataObject dataRead;
    private static File dataPath, imgPath;
    private static Bitmap mBitmap, mBackup;
    private static Context mContext;
    private static ImageView mEColor;
    private static Bitmap mPaintBitmap;
    private static int mDrawRadius;
    private static Button mPaintButton;
    private static ImageView flipLeft, flipRight;
    private static int lastScoreP, lastScoreA;
    private OutputStream outputStream;
    private ContextWrapper cw;
    private Uri newUri;
    private Canvas mCanvas;
    private Matrix paintMatrix;
    private float[] downCords;
    private float[] upCords;
    private ImageView imgView2;
    private Button mClearButton;
    private Toast mToast = null;


    public static void setColor(int color) {
        // This method is in charge of choosing the color of the color picker. It sets the color
        // variable mPaintColor and also changes the visual color of the color picker.

        Bitmap mPaintBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        mPaintBitmap.eraseColor(android.graphics.Color.GREEN);


        mPaintColor = color;

        switch (mPaintColor) {
            case 0:
                mPaintBitmap.eraseColor(android.graphics.Color.WHITE);
                break;
            case 1:
                mPaintBitmap.eraseColor(android.graphics.Color.YELLOW);
                break;
            case 2:
                mPaintBitmap.eraseColor(android.graphics.Color.BLUE);
                break;
            default:
                break;
        }

        mEColor.setImageBitmap(mPaintBitmap);

    }

    public static void getRadius() {
        // This method calculates how big the points and how thick the drawn lines should be
        // according to the size of the image. This is necessary because the points and lines are
        // drawn on the picture layer or one above.

        BitmapDrawable drawable = (BitmapDrawable) imgView1.getDrawable();
        Bitmap radiusBmp = drawable.getBitmap();

        mDrawRadius = Math.round(((radiusBmp.getWidth() / 60) + (radiusBmp.getHeight() / 60)) / 2);
    }


    public static void setUri() {
        // This method is used to load pictures from the Gallery. It sets a maximum image width and
        // height and if the picture is too big it downsizes it automatically. It also calls the
        // getRadius() method after loading the picture.

        int maxOpenWidth = 2048;
        int maxOpenHeight = 2048;

        try {
            mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        while (bmpWidth > maxOpenWidth || bmpHeight > maxOpenHeight) {
            bmpWidth = (int) (bmpWidth * 0.8);
            bmpHeight = (int) (bmpHeight * 0.8);
        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, bmpWidth, bmpHeight, true);

        imgView1.setImageBitmap(mBitmap);

        BitmapDrawable drawable = (BitmapDrawable) imgView1.getDrawable();
        mBackup = drawable.getBitmap();

        mPaintBitmap = Bitmap.createBitmap(mBackup.getWidth(), mBackup.getHeight(), Bitmap.Config.ARGB_8888);

        getRadius();


    }

    public static void enableButtons() {
        // This method is used for enabling the buttons after loading the picture and data.

        buttonAdd.setEnabled(true);
        buttonSave.setEnabled(true);
        mPaintButton.setEnabled(true);
        mEColor.setEnabled(true);
        flipLeft.setVisibility(View.VISIBLE);
        flipRight.setVisibility(View.VISIBLE);
        flipLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipPicture(true);
            }
        });
        flipRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipPicture(false);
            }
        });
    }

    public static void insertItem(int x, int y) {
        // This method is used for inserting data into the Example Recycler. It adds data to the
        // mExampleList and then notifies the adapter that the data has been changed.

        int front = mExampleList.size();

        mExampleList.add(new ExampleItem());
        mXCordList.add(front, x);
        mYCordList.add(front, y);
        textList.add(front, null);
        mAdapter.notifyItemInserted(front);
    }

    public static void loadData(File image, File data) {
        // This method is used to load existing data from the LearnPopup Activity.
        // The Gson library usage may seem a little unusual, but I'm using it to transform Arrays into
        // Strings or the other way around, because Arrays are not serializable.

        dataPath = data;
        imgPath = image;

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
        lastScoreP = dataRead.lastScoreP;
        lastScoreA = dataRead.lastScoreA;

        imgName = dataRead.name;
        Uri dImgUri = Uri.parse(dataRead.imgUri);

        imgView1.setImageURI(dImgUri);

        for (int i = 0; i < dXCords.size(); i++) {

            insertItem(dXCords.get(i).intValue(), dYCords.get(i).intValue());

            textList.add(i, dText.get(i));
        }

        buttonAdd.setEnabled(true);
        buttonSave.setEnabled(true);
        buttonDelete.setEnabled(true);
        mPaintButton.setEnabled(true);
        mEColor.setEnabled(true);

        BitmapDrawable drawable = (BitmapDrawable) imgView1.getDrawable();
        mBackup = drawable.getBitmap();

        mPaintBitmap = Bitmap.createBitmap(mBackup.getWidth(), mBackup.getHeight(), Bitmap.Config.ARGB_8888);

        getRadius();

        flipLeft.setOnClickListener(null);
        flipRight.setOnClickListener(null);
        flipLeft.setVisibility(View.GONE);
        flipRight.setVisibility(View.GONE);
    }

    public static void flipPicture(boolean isLeft) {
        // This method is used to flip the picture with the flip buttons. The flip buttons disappear
        // after adding points or drawing on the picture, because otherwise it would mess up the
        // end result. The boolean isLeft decides in which direction the picture should be flipped.

        if (isLeft) {
            Matrix matrix = new Matrix();

            matrix.postRotate(-90);

            mBackup = Bitmap.createBitmap(mBackup, 0, 0, mBackup.getWidth(), mBackup.getHeight(), matrix, true);
            imgView1.setImageBitmap(mBackup);

        } else {
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            mBackup = Bitmap.createBitmap(mBackup, 0, 0, mBackup.getWidth(), mBackup.getHeight(), matrix, true);
            imgView1.setImageBitmap(mBackup);
        }

        mPaintBitmap = Bitmap.createBitmap(mBackup.getWidth(), mBackup.getHeight(), Bitmap.Config.ARGB_8888);
    }

    public static void hideKeyboard(Activity activity) {
        // This method hides the keyboard before adding any new points so the user can interact with
        // the regular size of the picture (When the soft keyboard is open the ImageView resizes
        // itself to make room for the keyboard).
        // src: https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard/17789187#17789187

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in MainActivity for description
        // The lastScore variables are set to 0 because they are static methods and can have old
        // values stored.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        lastScoreP = 0;
        lastScoreA = 0;

        createLists();
        buildRecyclerView();
        setButtonFunction();
        setColor(1);

        mContext = this;

        startActivity(new Intent(EditActivity.this, ImgPopup.class));


        imgView1 = findViewById(R.id.imgView1);
        imgView2 = findViewById(R.id.imgView2);
    }

    public void removeItem(int position) {
        // This method is used to remove entries from the ExampleRecycler list. The integer position
        // decides which entry is getting removed.

        mExampleList.remove(position);
        textList.remove(position);
        mXCordList.remove(position);
        mYCordList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void createLists() {
        // This method creates the required ArrayLists.

        mExampleList = new ArrayList<>();
        mXCordList = new ArrayList<>();
        mYCordList = new ArrayList<>();
        textList = new ArrayList<>();
    }

    public void buildRecyclerView() {
        // Refer to the buildRecyclerView() method in MainActivity for description
        // This method also sets the listeners for the RecyclerView in a non static way.

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnClickListener() {

            @Override
            public void OnDeleteClick(int position) {
                removeItem(position);
                makeToast(getResources().getString(R.string.entryDeleted), true);
            }

            @Override
            public void OnCheckClick(int position) {
                imgView2.setImageDrawable(new BitmapDrawable(getResources(), mPaintBitmap));
                drawCanvas(mXCordList.get(position), mYCordList.get(position));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setButtonFunction() {
        // Refer to the setButtonFunction() method in MainActivity for description

        buttonAdd = findViewById(R.id.addButton);
        buttonSave = findViewById(R.id.saveButton);
        buttonDelete = findViewById(R.id.deleteButton);
        buttonExit = findViewById(R.id.exitButton);
        imgView1 = findViewById(R.id.imgView1);
        imgView2 = findViewById(R.id.imgView2);
        flipLeft = findViewById(R.id.flipLeft);
        flipRight = findViewById(R.id.flipRight);
        mEColor = findViewById(R.id.eColor);

        buttonDelete.setEnabled(false);
        mEColor.setEnabled(false);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(EditActivity.this);

                buttonAdd.setEnabled(false);
                buttonSave.setEnabled(false);
                buttonDelete.setEnabled(false);
                buttonExit.setEnabled(false);
                mPaintButton.setEnabled(false);

                flipLeft.setOnClickListener(null);
                flipRight.setOnClickListener(null);
                flipLeft.setVisibility(View.GONE);
                flipRight.setVisibility(View.GONE);

                makeToast(getResources().getString(R.string.choosePos), true);

                imgView2.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent ev) {
                        // This listener gets the touched coordinate INSIDE the picture. It's
                        // important that those are picture coordinates not screen coordinates
                        // because the app should work on different screen sizes.
                        // src: https://stackoverflow.com/questions/8002298/android-imageview-get-coordinates-of-tap-click-regardless-of-scroll-location/26405291#26405291

                        if (ev.getAction() == MotionEvent.ACTION_UP) {
                            imgView1.setImageDrawable(new BitmapDrawable(getResources(), mBackup));
                            imgView2.setImageDrawable(new BitmapDrawable(getResources(), mPaintBitmap));

                            Matrix imgMatrix = new Matrix();

                            imgView1.getImageMatrix().invert(imgMatrix);

                            float[] touchPoint = new float[2];

                            touchPoint[0] = ev.getX();
                            touchPoint[1] = ev.getY();

                            imgMatrix.mapPoints(touchPoint);

                            int cordX = (int) touchPoint[0];
                            int cordY = (int) touchPoint[1];

                            drawCanvas(cordX, cordY);
                            insertItem(cordX, cordY);

                            buttonAdd.setEnabled(true);
                            buttonSave.setEnabled(true);
                            buttonExit.setEnabled(true);
                            mPaintButton.setEnabled(true);

                            imgView2.setOnTouchListener(null);

                            return true;
                        } else {
                            return false;
                        }
                    }
                });


            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAll();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditActivity.this, R.style.MyDialogTheme);
                builder1.setMessage(getResources().getString(R.string.confirmDelete));
                builder1.setCancelable(true);

                builder1.setPositiveButton(getResources().getString(R.string.confirmYes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (imgPath.delete() && dataPath.delete()) {
                                    finish();
                                } else {
                                    Log.d("Error", "File could not be deleted");
                                }

                            }
                        });

                builder1.setNegativeButton(getResources().getString(R.string.confirmNo),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonAdd.setEnabled(false);
        buttonSave.setEnabled(false);

        mEColor = findViewById(R.id.eColor);

        mEColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditActivity.this, ColorPopup.class));

            }
        });

        mPaintButton = findViewById(R.id.ePaintButton);

        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipLeft.setOnClickListener(null);
                flipRight.setOnClickListener(null);
                flipLeft.setVisibility(View.GONE);
                flipRight.setVisibility(View.GONE);
                drawOnPicture();
            }
        });

        mPaintButton.setEnabled(false);

        mClearButton = findViewById(R.id.eClearButton);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaintBitmap = Bitmap.createBitmap(mPaintBitmap.getWidth(), mPaintBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                imgView2.setImageBitmap(mPaintBitmap);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton.setEnabled(false);

        flipLeft.setVisibility(View.GONE);
        flipRight.setVisibility(View.GONE);
    }

    public void drawCanvas(int posX, int posY) {
        // This method allows to draw points on the image to show the user where he exactly
        // clicked on the screen.

        BitmapDrawable drawable = (BitmapDrawable) imgView2.getDrawable();
        Bitmap mBitmap2 = drawable.getBitmap();

        Bitmap mutableBitmap = mBitmap2.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mutableBitmap);

        Paint mRed = new Paint();
        mRed.setColor(Color.RED);

        Paint mBlack = new Paint();
        mBlack.setColor(Color.BLACK);

        mCanvas.drawCircle(posX, posY, (int) (mDrawRadius * 1.2), mBlack);
        mCanvas.drawCircle(posX, posY, mDrawRadius, mRed);

        imgView2.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));

    }

    public void saveAll() {
        // This method is called when the user clicks the "Save" button and it saves the
        // image and the according data to storage.

        imgSave();
        fileSave();

        makeToast(getResources().getString(R.string.filesSaved), true);


    }

    public void imgSave() {
        // This method is used to save the image. There is no need to call for permissions because
        // Android SDK 23+ grants storage permissions automatically for every app.

        cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("images", Context.MODE_PRIVATE);

        File file = new File(dir, imgName + ".jpg");

        imgPath = file;

        try {
            outputStream = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmOverlay = Bitmap.createBitmap(mBackup.getWidth(), mBackup.getHeight(), mBackup.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(mBackup, new Matrix(), null);
        canvas.drawBitmap(mPaintBitmap, 0, 0, null);

        bmOverlay.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        newUri = Uri.fromFile(file);

    }

    public void fileSave() {
        // This method saves data with the DataObject class. It uses the serialization approach.
        // src: https://www.tutorialspoint.com/java/java_serialization.htm

        String textListGSON = new Gson().toJson(textList);
        String xCordListGSON = new Gson().toJson(mXCordList);
        String yCordListGSON = new Gson().toJson(mYCordList);

        String dPattern = "MM/dd/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dPattern, Locale.ENGLISH);
        Date date = new Date();
        String lastDate = formatter.format(date);

        DataObject toSave = new DataObject(imgName, newUri.toString(), textListGSON, xCordListGSON, yCordListGSON, lastDate, lastScoreP, lastScoreA);

        cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("data", Context.MODE_PRIVATE);


        File file = new File(dir, imgName + ".bin");

        dataPath = file;

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

        buttonDelete.setEnabled(true);


    }

    public void makeToast(String message, Boolean length) {
        // This class is used to make Toasts (The small messages on the bottom of the screen). It is
        // quite useful, because the syntax is shorter and it automatically cancels the last Toast
        // so there is no stacking.

        if (mToast != null) {
            mToast.cancel();
        }

        if (length) {
            mToast = Toast.makeText(EditActivity.this, message, Toast.LENGTH_LONG);
        } else {
            mToast = Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void drawOnPicture() {
        // This method is called when the user clicks the "Draw" button. It allows the user to
        // finger paint on the image to hide text or add notes. The onTouch method of the imgView2
        // is inspired by the following StackOverflow answer:
        // https://stackoverflow.com/questions/26209532/draw-on-picture-in-android/28186621#28186621

        imgView2 = findViewById(R.id.imgView2);

        imgView2.setImageDrawable(new BitmapDrawable(getResources(), mPaintBitmap));
        imgView1.setImageDrawable(new BitmapDrawable(getResources(), mBackup));

        mCanvas = new Canvas(mPaintBitmap);

        final Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mDrawRadius);

        downCords = new float[2];
        upCords = new float[2];
        paintMatrix = new Matrix();
        imgView1.getImageMatrix().invert(paintMatrix);

        buttonAdd.setEnabled(false);
        buttonSave.setEnabled(false);
        mClearButton.setEnabled(false);
        mPaintButton.setText(getResources().getString(R.string.drawButtonText2));

        mPaintButton.setOnClickListener(null);
        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView2.setOnTouchListener(null);
                mPaintButton.setText(getResources().getString(R.string.drawButtonText));
                buttonAdd.setEnabled(true);
                buttonSave.setEnabled(true);
                mClearButton.setEnabled(true);

                mPaintButton.setOnClickListener(null);
                mPaintButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawOnPicture();
                    }
                });
            }
        });


        imgView2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                switch (mPaintColor) {
                    case 0:
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        break;
                    case 1:
                        paint.setXfermode(null);
                        paint.setColor(Color.YELLOW);
                        break;
                    case 2:
                        paint.setXfermode(null);
                        paint.setColor(Color.BLUE);
                        break;
                    default:
                        break;
                }

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downCords[0] = ev.getX();
                        downCords[1] = ev.getY();

                        paintMatrix.mapPoints(downCords);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        upCords[0] = ev.getX();
                        upCords[1] = ev.getY();

                        paintMatrix.mapPoints(upCords);

                        mCanvas.drawLine(downCords[0], downCords[1], upCords[0], upCords[1], paint);
                        downCords[0] = upCords[0];
                        downCords[1] = upCords[1];
                        break;
                    case MotionEvent.ACTION_UP:
                        upCords[0] = ev.getX();
                        upCords[1] = ev.getY();
                        paintMatrix.mapPoints(upCords);
                        mCanvas.drawLine(downCords[0], downCords[1], upCords[0], upCords[1], paint);
                        break;
                    default:
                        break;
                }

                imgView2.setImageDrawable(new BitmapDrawable(getResources(), mPaintBitmap));

                return true;
            }
        });


    }

}