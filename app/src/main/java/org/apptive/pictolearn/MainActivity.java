package org.apptive.pictolearn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // This class controls the behaviour of the Main Menu. Every class which extends AppCompatActivity
    // or Activity is in control of it's own screen or popup.

    private RecentsAdapter mAdapter;
    private ArrayList<RecentsItem> mRecentsList;
    private DataObject dataRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This method is called at the startup of the Main Menu Activity and executes methods like
        // setImages(). Through this the other methods are also ran at startup, but the onCreate
        // method remains fairly clean.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setImages();
        buildRecyclerView();
        updateRecycler();
    }

    public void updateRecycler() {
        // This method controls the RecyclerView (Imagine it as a kind of list) at the bottom of the
        // Activity. It reloads all the data in the Recycler so the "Recent" tab is always up to
        // date with five latest scores and last used dates. It also lists the data sets chronologically.

        mRecentsList.clear();
        ArrayList<File> mRecentFiles = getRecents();

        int count;
        if (mRecentFiles.size() >= 5) {
            count = 5;
        } else {
            count = mRecentFiles.size();
        }

        for (int i = 0; i < count; i++) {
            try {
                FileInputStream fis = new FileInputStream(mRecentFiles.get(i));
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
            ArrayList<Double> dXCords = new Gson().fromJson(dataRead.xCords, listTypeDouble);

            double percentP = ((double) dataRead.lastScoreP / (double) dXCords.size()) * 100;
            double percentA = ((double) dataRead.lastScoreA / (double) dXCords.size()) * 100;

            mRecentsList.add(new RecentsItem(dataRead.name, dataRead.lastDate, (int) percentP, (int) percentA));
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setImages() {
        // This method links our ImageViews like editImage with the proper ImageView in the
        // layout files and also sets their OnClickListeners. It's a kind of alternative approach
        // because I could have simply used buttons, but the ImageViews look better imho.

        ImageView editImage = findViewById(R.id.editImage);
        ImageView learnImage = findViewById(R.id.learnImage);
        ImageView infoImage = findViewById(R.id.infoImage);


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        learnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ModePopup.class));
            }
        });

        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog();
            }
        });
    }

    public void infoDialog() {
        // This method calls for an alert dialog which creates an alert box with a short description
        // of this app and also with my mail address. It is called when clicking on the infoImage
        // ImageView.

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder1.setMessage(getResources().getString(R.string.infoMessage));
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


    public void buildRecyclerView() {
        // This method creates the RecyclerView placed on the bottom of the Activity (= Screen).
        // RecyclerViews are a way to create interactive lists in Android with data in them.
        // In this case I used a RecyclerView to display the most recent DataSets used for a quick
        // possibility to start learning. You can find more information regarding RecyclerViews and
        // their usage on https://developer.android.com/guide/topics/ui/layout/recyclerview

        mRecentsList = new ArrayList<>();
        RecyclerView mRecyclerView = findViewById(R.id.recentsRecycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecentsAdapter(mRecentsList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecentsAdapter.OnClickListener() {

            @Override
            public void OnOpenClick(int position) {
                Intent i = new Intent(MainActivity.this, ModePopup.class);
                i.putExtra("fileName", mRecentsList.get(position).getText1());
                startActivity(i);
            }
        });
    }


    public ArrayList<File> getRecents() {
        // This method is meant to return an ArrayList of the names of files in the app_data
        // directory. The ContextWrapper gets the directory in which the app can store files.
        // The while loop sorts the files according to the date they were last modified using the
        // fairly simple bubble sort algorithm.

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dataDir = cw.getDir("data", Context.MODE_PRIVATE);

        ArrayList<File> files = new ArrayList<>(Arrays.asList(dataDir.listFiles()));
        boolean swapped = true;

        while (swapped) {

            swapped = false;

            for (int i = 0; i < (files.size() - 1); i++) {
                if (files.get(i).lastModified() < files.get(i + 1).lastModified()) {
                    File temp = files.get(i);

                    files.set(i, files.get(i + 1));
                    files.set((i + 1), temp);

                    swapped = true;
                }
            }

        }

        return files;
    }

    @Override
    public void onResume() {
        // This method is called when the Activity regains the focus from the user. This happens
        // when another Activity that was open on top of the Main Menu is closed and the user
        // returns to the Main Menu. The RecyclerView is updated via updateRecycler() method because
        // possibly the score of a recent DataSet was changed or a new one was created.

        super.onResume();
        updateRecycler();
    }


}
