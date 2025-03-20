package com.scriptsbundle.nokri.manager.download.manager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Glixen Technologies on 16/01/2018.
 */

public class Nokri_Download implements Parcelable {
    public Nokri_Download() {
    }
    private int progress;
    private int currentFileSize;
    private int totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    protected Nokri_Download(Parcel in) {
        progress = in.readInt();
        currentFileSize = in.readInt();
        totalFileSize = in.readInt();
    }

    public static final Creator<Nokri_Download> CREATOR = new Creator<Nokri_Download>() {
        @Override
        public Nokri_Download createFromParcel(Parcel in) {
            return new Nokri_Download(in);
        }

        @Override
        public Nokri_Download[] newArray(int size) {
            return new Nokri_Download[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(progress);
        parcel.writeInt(currentFileSize);
        parcel.writeInt(totalFileSize);
    }
}
