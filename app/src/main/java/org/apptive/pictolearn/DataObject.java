package org.apptive.pictolearn;


import java.io.Serializable;

public class DataObject implements Serializable {
    // This class is used for saving and loading data sets. It contains all the important information
    // needed for a data set including the name, image path, text, the coordinates, the last date
    // used and the most recent scores. It uses the serialization approach.

    public String name;
    public String imgUri;

    public String text;
    public String xCords;
    public String yCords;
    public String lastDate;
    public int lastScoreP;
    public int lastScoreA;


    DataObject(String iName, String iImgUri, String iText, String iXCords, String iYCords, String iLastDate, int iLastScoreP, int iLastScoreA) {
        // This method is used to construct a DataObject for saving.

        this.name = iName;
        this.imgUri = iImgUri;
        this.text = iText;
        this.xCords = iXCords;
        this.yCords = iYCords;
        this.lastDate = iLastDate;
        this.lastScoreP = iLastScoreP;
        this.lastScoreA = iLastScoreA;
    }
}
