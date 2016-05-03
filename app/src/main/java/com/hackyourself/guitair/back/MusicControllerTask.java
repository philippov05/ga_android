package com.hackyourself.guitair.back;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * MusicControllerTask
 */
public class MusicControllerTask extends AsyncTask<Void, Void, Void> {

    private static String LOG_TAG = "MusicControllerTask";

    private MotionController mMotionController;
    private NetworkManager mNetworkManager;

    public MusicControllerTask(Context context) {
        Log.d(LOG_TAG, "Created.");
        mMotionController = new MotionController(context);
        mNetworkManager = new NetworkManager(context);
        mMotionController.registerObserver(mNetworkManager);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(LOG_TAG, "starting task...");
        while (!mNetworkManager.start()); // Await connection.
        mMotionController.start();
        while (!isCancelled());
        cancel(true);
        return null;
    }

    @Override
    protected void onCancelled() {
        Log.d(LOG_TAG, "canceling task...");
        mMotionController.stop();
        mNetworkManager.stop();
    }
}
