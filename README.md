SINCE XE11, WINKY DOES NOT WORK
===========
See: https://github.com/kaze0/winky/issues/6

winky
===========

A way to enable the wink gesture on Google Glass to take a photo.

- The screen can be off while you wink, this means completely hands and mouth free photo taking. Take pictures while you eat!
- Run the app, then use the touchpad to tap on the calibrate. Follow the on screen directions to calibrate.

So why do we need this app?
There's at least one location in code on Glass that does a check to see if the wink gesture should be enabled and then turns it off if it shouldn't. This turns on the wink gesture, and makes sure that Glass won't be able to disable it by intercepting any future wink gestures and responding with a photo.

Who is Mike DiGiovanni? Emerging technology lead at Roundarch Isobar (http://www.roundarchisobar.com) Mike has interests in all areas of mobile development and wearable computing. As a long time Android developer, he is looking forward to working with Google Glass.

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
