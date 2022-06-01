package org.apptive.pictolearn;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class LearnPopup extends Activity {

    private ArrayList<DataItem> mDataList;
    private DataSetAdapter mAdapter;
    private static ContextWrapper cw;
    public static Context mContext, dContext;

    public static int parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in ColorPopup for description
        // This onCreate method also gets the Intent extras to determine which Activity started it.
        // The user picks a dataSet in this class and after that the data is passed to the class that
        // opened the LearnPopup. The according class is determined with the "key" extra.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_popup);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            parent = intent.getIntExtra("key", 0);
        }

        mContext = this;


        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        cw = new ContextWrapper(getApplicationContext());

        mDataList = new ArrayList<>();

        buildRecyclerView();

        loadFiles();

        Button mCloseButton = findViewById(R.id.closeButton);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void buildRecyclerView() {
        // Refer to the buildRecyclerView method in MainActivity for description

        RecyclerView mRecyclerView = findViewById(R.id.dataSetRecycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DataSetAdapter(this, mDataList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void loadFiles() {
        // This method adds all the filenames into the RecyclerView with a foreach loop.
        // It also removes the file suffix (.bin or .jpg).

        String[] fileList = fileNames();

        for(String i : fileList) {
            int end = i.length() - 4;
            String noSuffix = i.substring(0, end);
            mDataList.add((new DataItem(R.drawable.ic_baseline_note_24px, noSuffix)));
            mAdapter.notifyDataSetChanged();
        }



    }

    public String[] fileNames() {
        // This method is called by the loadFiles method and returns a String Array of all the
        // files in the images directory.

        File dir = cw.getDir("images", Context.MODE_PRIVATE);

        ArrayList<String> files = new ArrayList<>();

        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {
                    files.add(file.getName());
                }
            }
        }

        return files.toArray(new String[]{});
    }

    public static void openFile(String imageFile, String dataFile) {
        // This method calls the static loadData method in the activity which opened the LearnPopup
        // determined by the parent variable which is determined by the "key" extra.

        File imgDir = cw.getDir("images", Context.MODE_PRIVATE);
        File dataDir = cw.getDir("data", Context.MODE_PRIVATE);

        File image = new File(imgDir, imageFile);
        File data = new File(dataDir, dataFile);

        switch(parent) {
            case 0:
                EditActivity.loadData(image, data);
                ((Activity)mContext).finish();
                ((Activity)dContext).finish();
                break;
            case 1:
                LearnActivity.loadData(image, data);
                break;
            case 2:
                LearnActivityAnswer.loadData(image,data);
                break;
            default:
                Log.d("Error", "No Intent Extras specified!");
                break;
        }

    }
}
