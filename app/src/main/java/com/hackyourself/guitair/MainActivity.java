package com.hackyourself.guitair;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackyourself.guitair.back.MusicControllerTask;
import com.hackyourself.guitair.fragments.NoGyroscopeDialog;

import android.util.Log;

public class MainActivity extends Activity {
    private boolean rockButtonIsPress;
    private boolean mWriteAccepted = false;
    private final String LOG_TAG = "activity_main_";
    MusicControllerTask mMusicControllerTask;
    public static String code;
    private boolean mIsNoGyroscope = false;
    private Button rockButton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText;

        final Animation rotateCenterAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
        final Animation moveGuitarAnim = AnimationUtils.loadAnimation(this, R.anim.move_text);
        final Animation movePauseButtonAnim = AnimationUtils.loadAnimation(this, R.anim.move_pause_button);
        final Animation movePauseButtonAnimReverse = AnimationUtils.loadAnimation(this, R.anim.move_pause_button_reverse);
        final Animation moveGuitarAnimReverse = AnimationUtils.loadAnimation(this, R.anim.move_text_reverse);
        final ImageView starImg = (ImageView) findViewById(R.id.imageView2);
        final Button pauseButton = (Button) findViewById(R.id.button2);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        //final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        starImg.setAnimation(rotateCenterAnim);
        editText = (EditText) findViewById(R.id.editText2);
        rockButton = (Button) findViewById(R.id.button);
        if(mIsNoGyroscope){
            editText.setEnabled(false);
            rockButton.setEnabled(false);
        }

        moveGuitarAnimReverse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        setMusicControllerTask(new MusicControllerTask(this));
        rockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rockButtonIsPress){
                    rockButtonIsPress = true;
                    starImg.startAnimation(rotateCenterAnim);
                    frameLayout.startAnimation(moveGuitarAnim);
                    frameLayout.setVisibility(View.INVISIBLE);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rockButton, "translationY",
                            0, -200 );
                    objectAnimator.setDuration(1000);
                    objectAnimator.start();
                    pauseButton.setVisibility(View.VISIBLE);
                    pauseButton.setAnimation(movePauseButtonAnim);
                    code = String.valueOf(editText.getText());
                    editText.setVisibility(View.INVISIBLE);
                    // Run our controller.
                    getMusicControllerTask().execute();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rockButtonIsPress = false;
                starImg.startAnimation(rotateCenterAnim);
                frameLayout.startAnimation(moveGuitarAnimReverse);
                frameLayout.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rockButton, "translationY",
                        -200, 0 );
                objectAnimator.setDuration(1000);
                objectAnimator.start();
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setAnimation(movePauseButtonAnimReverse);
                // Stop our controller.
                getMusicControllerTask().cancel(true);
                setMusicControllerTask(new MusicControllerTask(MainActivity.this));
            }
        });

        // Ask for permissions in API v23.
        if (mShouldAskPermission()) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
    }

    /**
     * Says if writing is accepted in this activity.
     *
     * @return  true if is accepted, else otherwise
     */
    public boolean isWriteAccepted() {
        return true; // mWriteAccepted; // TODO! Set as false if API v23.
    }

    /**
     * Says if you should request some permissions in runtime.
     *
     * @return  true if you should, else otherwise
     */
    private boolean mShouldAskPermission() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions,
                                           int[] grantResults) {
        //
        switch (permsRequestCode) {
            case 200:
                mWriteAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SensorManager sensorMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorMngr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (sensor == null){
            mIsNoGyroscope = true;
            NoGyroscopeDialog d = new NoGyroscopeDialog();
            d.show(getFragmentManager(), "d");
        }

    }

    private void setMusicControllerTask(MusicControllerTask mct) {
        mMusicControllerTask = mct;
    }

    private MusicControllerTask getMusicControllerTask() {
        return mMusicControllerTask;
    }

    //private boolean getRockButtonIsPress() {return rockButtonIsPress;}

    //private void setRockButtonIsPress(boolean fl) {rockButtonIsPress = fl;}
}
