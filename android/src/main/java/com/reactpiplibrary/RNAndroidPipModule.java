
package com.reactpiplibrary;

import com.facebook.react.bridge.ReactApplicationContext;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;

import android.app.PictureInPictureParams;
import android.os.Build;
import android.util.Rational;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Process;

import com.facebook.react.bridge.Promise;

public class RNAndroidPipModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    private static final int ASPECT_WIDTH = 3;
    private static final int ASPECT_HEIGHT = 4;
    private boolean isPipSupported = false;
    private boolean isCustomAspectRatioSupported = false;
    private boolean isPipListenerEnabled = false;
    private Rational aspectRatio;

    public RNAndroidPipModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isPipSupported = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isCustomAspectRatioSupported = true;
            aspectRatio = new Rational(ASPECT_WIDTH, ASPECT_HEIGHT);
        }
    }

    @Override
    public String getName() {
        return "RNAndroidPip";
    }

    @ReactMethod
    public void enterPictureInPictureMode() {
        if (isPipSupported) {
            AppOpsManager manager = (AppOpsManager) reactContext.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                int modeAllowed = manager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(),
                    reactContext.getPackageName());

                if (modeAllowed == AppOpsManager.MODE_ALLOWED) {
                    if (isCustomAspectRatioSupported) {
                        PictureInPictureParams params = new PictureInPictureParams.Builder()
                                .setAspectRatio(this.aspectRatio).build();
                        getCurrentActivity().enterPictureInPictureMode(params);
                    } else {
                        getCurrentActivity().enterPictureInPictureMode();
                    }
                }
            }
        }
    }
    
    @ReactMethod
    public void hasSpecialPipPermission(final Promise promise) {
        AppOpsManager manager = (AppOpsManager) reactContext.getSystemService(Context.APP_OPS_SERVICE);
        if (manager != null) {
            int modeAllowed = manager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(),
                    reactContext.getPackageName());

            if (modeAllowed == AppOpsManager.MODE_ALLOWED) {
                promise.resolve("Permission enabled");
                return;
            }
        }
        promise.reject("Permission not enabled");
    }

    @ReactMethod
    public void configureAspectRatio(Integer width, Integer height) {
        aspectRatio = new Rational(width, height);
    }

    @ReactMethod
    public void enableAutoPipSwitch() {
        isPipListenerEnabled = true;
    }

    @ReactMethod
    public void disableAutoPipSwitch() {
        isPipListenerEnabled = false;
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
        if (isPipSupported && isPipListenerEnabled) {
            enterPictureInPictureMode();
        }
    }

    @Override
    public void onHostDestroy() {
    }
}
