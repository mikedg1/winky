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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EyeGestureReceiver extends BroadcastReceiver {
    //public static Boolean burstMode = null;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        abortBroadcast(); //So we have a problem if we abort this broadcast, because we can't calibrate :(

        //wakeUp(context); //Oh god this is beyond trivial, didn't even need to implement that :)
        //takePhoto(context);
    }

	private void takePhoto(Context context) {
        if (Prefs.getInstance(context).getBurst()) {
            Intent i = new Intent(context, TreyBurstActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TreyBurstActivity.EXTRA_TIMELINE, Prefs.getInstance(context).getSaveToTimeline());
            context.startActivity(i);
        } else
        {
            //The sloppy way of launching the default camera taking app
            Intent i = new Intent("android.intent.action.CAMERA_BUTTON");
            context.sendBroadcast(i);
        }
    }
}
