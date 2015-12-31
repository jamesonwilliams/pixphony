/*
 * Copyright 2015 Jameson Williams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nosemaj.pixphony;

import android.app.Application;
import android.util.Log;

/* 
 * This class was originally added to have a place to store bluetooth
 * connection state, as suggested on stack overflow answer here:
 *
 * http://stackoverflow.com/a/17569897/695787
 */
public class PixphonyApplication extends Application {
    private static final String TAG = PixphonyApplication.class.getName();

    private static PixmobConnectionManager sConnectionManager = null;
    private static SoundPlayer sSoundPlayer = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        sConnectionManager = PixmobConnectionManager.getInstance(this);
        sSoundPlayer = SoundPlayer.getInstance(this);
    }

    public static PixmobConnectionManager getConnectionManager() {
        return sConnectionManager;
    }

    public static SoundPlayer getSoundPlayer() {
        return sSoundPlayer;
    }
}

