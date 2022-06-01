package org.apptive.pictolearn;

class RecentsItem {
    // Refer to the DataItem class for description

    private String mText1;
    private String mLastDate;
    private int mLastScoreP;
    private int mLastScoreA;

    RecentsItem(String text1, String lastDate, int lastScoreP, int lastScoreA) {
        mText1 = text1;
        mLastDate = lastDate;
        mLastScoreP = lastScoreP;
        mLastScoreA = lastScoreA;
    }

    String getText1() {
        return mText1;
    }

    String getDate() {
        return mLastDate;
    }

    int getLastScoreP() {
        return mLastScoreP;
    }

    int getLastScoreA() {
        return mLastScoreA;
    }
}