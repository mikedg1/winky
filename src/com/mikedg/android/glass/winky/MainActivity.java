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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.app.backup.RestoreObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements RecognitionListener {

    private static String TAG = "dgGestureService";
    Object glassGestureManager;
	SpeechRecognizer speechRecognizer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Get the string for the gesture service that we want
        String str = getConstantFromClass("GLASS_GESTURE_SERVICE", Context.class);
        if (str == null)
        {
            finish();
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        
        startListening();
        
        glassGestureManager = this.getSystemService(str);
    }
    
    private void startListening() {
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.mikedg.android.glass.winky");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5); 
        
        speechRecognizer.startListening(intent);
    }

    public void onClick_calibration(View view) {
        //Disables the wink receiver while we calibrate because calibration won't receive a wink if we intercept it
        disableWinkReceiver();
        this.startActivity(new Intent("com.google.glass.action.ACTION_WINK_CALIBRATION"));
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
    
    public void onClick_clearCalibration(View view) {
        clearCalibation();
        Toast.makeText(this, "Cleared calibration", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        //We want to always enable wink detection when we resume
        //Just easier to do it here in case you accidentally get it disabled by the system
        //Calibrate should enable it by default though
        enableWinkReceiver();
        enableWinkDetection();
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
    protected void onStop() {
    	super.onStop();
    	
    	speechRecognizer.stopListening();
    	speechRecognizer.destroy();
    }

	@Override
	public void onBeginningOfSpeech() {
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
	}

	@Override
	public void onEndOfSpeech() {
	}

	@Override
	public void onError(int error) {
		Toast.makeText(this, "I couldn't understand you correctly", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		Toast.makeText(this, "Tell me what you want to do now", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResults(Bundle results) {
		String calibrationString = getString(R.string.button_calibration).toLowerCase(Locale.getDefault());
		String clearCalibrationString = getString(R.string.button_clear_calibration).toLowerCase(Locale.getDefault());
		
		ArrayList<String> possibleInput = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		for (String input : possibleInput) {
			input = input.toLowerCase(Locale.getDefault());
			if (calibrationString.equals(input)) {
				onClick_calibration(null);
				
				return;
			} else if (clearCalibrationString.equals(input)) {
				onClick_clearCalibration(null);
				
				return;
			}
		}
		
		startListening();
		
		Toast.makeText(this, "'" + possibleInput.get(0) + "'? No. Please say 'calibration' or 'clear calibration'", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRmsChanged(float rmsdB) {
	}
}
