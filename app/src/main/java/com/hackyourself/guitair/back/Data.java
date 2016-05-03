package com.hackyourself.guitair.back;

public class Data {

    private int mMovement;
    private long mTimeStamp;

    public Data(long mTimeStamp, int mMovement) {
        this.mTimeStamp = mTimeStamp;
        this.mMovement = mMovement;
    }

    public int getMovement() {
        return mMovement;
    }

    public long getTime() {
        return mTimeStamp;
    }
}
