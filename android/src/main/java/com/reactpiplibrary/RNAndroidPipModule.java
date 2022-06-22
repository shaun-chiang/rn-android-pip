
package com.reactpiplibrary;

import com.facebook.react.bridge.ReactApplicationContext;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.app.PictureInPictureParams;
import android.os.Build;
import android.util.Rational;
import android.util.Log;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Process;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import com.facebook.react.bridge.Promise;

public class RNAndroidPipModule extends ReactContextBaseJavaModule implements LifecycleEventListener, LifecycleEventObserver {

    private final ReactApplicationContext reactContext;
    private static final int ASPECT_WIDTH = 3;
    private static final int ASPECT_HEIGHT = 4;
    private boolean isPipSupported = false;
    private boolean isCustomAspectRatioSupported = false;
    private boolean isPipListenerEnabled = false;
    private boolean isInPiPMode = false;
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

    private void sendEvent(String eventName, @Nullable WritableMap args) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, args);
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
    public void startModeChangeListener() {
        AppCompatActivity activity = (AppCompatActivity) reactContext.getCurrentActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getLifecycle().addObserver(RNAndroidPipModule.this);
                }
            });
        } else {
            Log.d(this.getName(), "App activity is null.");
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

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppCompatActivity activity = (AppCompatActivity) source;
            boolean isInPiPMode = activity.isInPictureInPictureMode();
            // Check for changes on pip mode.
            if (this.isInPiPMode != isInPiPMode) {
                this.isInPiPMode = isInPiPMode;
                Log.d(this.getName(), "Activity pip mode has changed to " + isInPiPMode);
                // Dispatch onPictureInPicutreModeChangedEvent to js.
                WritableMap args = Arguments.createMap();
                args.putBoolean("isInPiPMode", isInPiPMode);
                sendEvent("onPictureInPictureModeChanged", args);
            }
        }
    }
}
