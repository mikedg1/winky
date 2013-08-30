/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.android.glass.winky;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {
	private static String TAG = "dgGestureService";
    Object glassGestureManager;
    private boolean mJustSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        //Get the string for the gesture service that we want
        String str = getConstantFromClass("GLASS_GESTURE_SERVICE", Context.class);
        if (str == null)
        {
            finish();
        }
        openOptionsMenu();
        glassGestureManager = this.getSystemService(str);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
    private void disableWinkReceiver() {
        setWinkReceiverEnabled(false);
    }

    private void setWinkReceiverEnabled(boolean b) {
        ComponentName component = new ComponentName(this, EyeGestureReceiver.class);
        getPackageManager().setComponentEnabledSetting(component,
                b ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);        
    }

    private void enableWinkReceiver() {
        setWinkReceiverEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        //We want to always enable wink detection when we resume
        //Just easier to do it here in case you accidentally get it disabled by the system
        //Calibrate should enable it by default though
        enableWinkReceiver();
        enableWinkDetection();

        openOptionsMenu();
    }

    public void clearCalibation() {
        try {
            Method method = glassGestureManager.getClass().getMethod("clearEyeCalibration");
            method.invoke(glassGestureManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
    
    public void enableWinkDetection() {
        try {
            Method method = glassGestureManager.getClass().getMethod("enableWinkDetector", Boolean.TYPE);
            method.invoke(glassGestureManager, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    private static String getConstantFromClass(String paramString, Class<?> paramClass) {
        try {
            String str = (String) paramClass.getDeclaredField(paramString).get(null);
            return str;
        } catch (IllegalAccessException localIllegalAccessException) {
            Log.e(TAG, "Unable to get " + paramString + " from " + paramClass.getSimpleName(),
                    localIllegalAccessException);
            return null;
        } catch (NoSuchFieldException localNoSuchFieldException) {
            Log.e(TAG, "Unable to get " + paramString + " from " + paramClass.getSimpleName(),
                    localNoSuchFieldException);
        }
        return null;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        mJustSelected = true;

		switch (item.getItemId()) {
            case R.id.calibration_menu_item:
                //Disables the wink receiver while we calibrate because calibration won't receive a wink if we intercept it
                disableWinkReceiver();
                this.startActivity(new Intent("com.google.glass.action.ACTION_WINK_CALIBRATION"));
                return false;
            case R.id.clear_menu_item:
                clearCalibation();
                Toast.makeText(this, "Cleared calibration", Toast.LENGTH_SHORT).show();
                return false;
		}
        return super.onOptionsItemSelected(item);
	}

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        if (mJustSelected) {
            //FIXME: need to wait a bit
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    openOptionsMenu();
                }
            });
            mJustSelected = false;
        } else {
            //User dismissed so back out completely
            //FIXME: right now, calling finish here doesn't seem to let us re-enable the receiver
            //finish();
        }
    }
}
