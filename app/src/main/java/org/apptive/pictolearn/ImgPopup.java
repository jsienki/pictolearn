package org.apptive.pictolearn;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class ImgPopup extends Activity {
    // This class controls the popup that is called when the EditActivity class starts.

    private Button okBtn;
    private EditText editTextView4;
    private ContextWrapper cw;
    private Toast mToast;

    private static final int PICK_IMAGE = 100;

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Refer to the InputFilter in ExampleAdapter for description.

            String blockCharacterSet = "*/\\:?\"<>|";

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }

            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }

            return null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Refer to the onCreate method in ColorPopup for description

        super.onCreate(savedInstanceState);

        setContentView(R.layout.img_popup);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        editTextView4 = findViewById(R.id.editTextView4);
        editTextView4.setFilters(new InputFilter[] { filter });

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        setButtons();

    }

    public void openGallery() {
        // This method opens the users image gallery.

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This method is ran after the user picked an image from his gallery.

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            EditActivity.imgUri = data.getData();
            EditActivity.setUri();

            okBtn.setEnabled(true);
        }
    }

    public void setButtons() {
        // Refer to the setButtonFunction method in EditActivity for description

        editTextView4 = findViewById(R.id.editTextView4);

        okBtn = findViewById(R.id.okButton);

        Button imgBtn = findViewById(R.id.imgButton);

        Button qtBtn = findViewById(R.id.quitButton);

        Button extBtn = findViewById(R.id.extButton);

        okBtn.setEnabled(false);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cw = new ContextWrapper(getApplicationContext());
                File dir = cw.getDir("images", Context.MODE_PRIVATE);
                final File file = new File(dir, editTextView4.getText().toString() + ".jpg");

                if (editTextView4.getText().toString().matches("")) {
                    makeToast("You need to name your DataSet!", true);
                } else if (file.isFile()){
                    makeToast("A file named like this already exists!", true);
                } else {
                    EditActivity.imgName = editTextView4.getText().toString();
                    EditActivity.enableButtons();
                    finish();
                }
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        qtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //((Activity)mContext).finish();
            }
        });

        extBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadExisting();
            }
        });

    }

    public void loadExisting() {
        // This method is ran when the user clicks the "Import DataSet" button. It calls the
        // LearnPopup class.

        Intent i = new Intent(ImgPopup.this, LearnPopup.class);
        i.putExtra("key", 0);
        startActivity(i);
        LearnPopup.dContext = this;
    }

    public void makeToast(String message, Boolean length) {
        // Refer to the makeToast method in EditActivity for description

        if (mToast != null) { mToast.cancel(); }

        if (length) {
            mToast = Toast.makeText(ImgPopup.this, message, Toast.LENGTH_LONG);
        } else {
            mToast = Toast.makeText(ImgPopup.this, message, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }
}

