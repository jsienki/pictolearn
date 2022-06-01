package org.apptive.pictolearn;

class DataObjectLoad {
    // This class is used for interacting with the learning modes. It is used to create an array of
    // data which can easily be shuffled, so the user gets randomized questions.

    String mText;
    Double mCordX, mCordY;

    DataObjectLoad(String text, Double cordX, Double cordY) {
        // Class constructor

        this.mText = text;
        this.mCordX = cordX;
        this.mCordY = cordY;

    }
}
