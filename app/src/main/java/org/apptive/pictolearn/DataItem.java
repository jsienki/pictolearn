package org.apptive.pictolearn;

class DataItem {
    // This class is used for the DataSet Recycler used in the LearnPopup class for the user to
    // load a data set. The mImageResource Integer is used for the picture next to the entries
    // while the mText1 String is used to set the filename.

    private int mImageResource;
    private String mText1;

    DataItem(int imageResource, String text1) {
        // This method is used as the constructor of a DataSet Recycler entry.

        mImageResource = imageResource;
        mText1 = text1;
    }

    int getImageResource() {
        // This method is used to return the image reference.

        return mImageResource;
    }

    String getText1() {
        // This method is used to return the filename.

        return mText1;
    }
}