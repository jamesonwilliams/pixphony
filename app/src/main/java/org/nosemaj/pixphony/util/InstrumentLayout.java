/*
 * Copyright 2015 Nagaraj Bhat
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

package org.nosemaj.pixphony.util;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by scanner on 28-12-15.
 */
public class InstrumentLayout {
    public int currentPressedIndex = ButtonLayout.KeyPressedNone;
    public long timeoutInMillisecondsForKeyReset = 200;
    public ButtonLayout[] buttonCollections = null;
    public Context currentActivityContext = null;

    private boolean autoResetKeys = true;

    public InstrumentLayout(ButtonLayout[] btnCollection) {
        buttonCollections = btnCollection;
    }

    public void SetTimeoutValueForKeyReset(long tmout) {
        if (tmout >= 0) {
            timeoutInMillisecondsForKeyReset = tmout;
        }
    }

    public void SetAutoResetKeys(boolean val) {
        autoResetKeys = val;
    }

    public void ShowUnpressForAllKeys() {
        if (buttonCollections == null) {
            return;
        }

        for (int i = 0; i < buttonCollections.length; i++) {
            if (buttonCollections[i].currentPressStatus == ButtonLayout.KeyPressed) {
                buttonCollections[i].keyButton.setBackgroundResource(buttonCollections[i].keyUnpressedImageId);
                // buttonCollections[i].keyButton.setImageResource( buttonCollections[i].keyUnpressedImageId);
                buttonCollections[i].currentPressStatus = ButtonLayout.KeyUnPressed;
            }
        }

        currentPressedIndex = ButtonLayout.KeyPressedNone;
    }

    public void ShowPressKeyForTone(int buttonMapIndex) {
        if (buttonCollections == null) {
            return;
        }

        if (buttonMapIndex >= buttonCollections.length || buttonMapIndex < 0) {
            return;
        }

        ShowUnpressForAllKeys();

        for (int i = 0; i < buttonCollections.length; i++) {
            if (buttonCollections[i].idToButtonMap != buttonMapIndex) {
                continue;
            }

            if (buttonCollections[i].currentPressStatus == ButtonLayout.KeyUnPressed) {
                // buttonCollections[i].keyButton.setImageResource(buttonCollections[i].keyPressedImageId);
                buttonCollections[i].keyButton.setBackgroundResource(buttonCollections[i].keyPressedImageId);
                buttonCollections[i].currentPressStatus = ButtonLayout.KeyPressed;
                currentPressedIndex = i;

                if (autoResetKeys) {
                    resetButtonTimerHandler.postDelayed(updateTimerThread, timeoutInMillisecondsForKeyReset);
                }
            }
        }
    }

    public boolean IsXYOverButton(ImageButton b, int x, int y) {
        Rect rect = new Rect();
        b.getHitRect(rect);

        if (x > rect.left && x < (rect.left + rect.width()) &&
                y > rect.top && y < (rect.top + rect.height())) {
            return true;
        }

        return false;
    }

    //Will return the keyindex that got pressed. Else, -1
    public int HandleSwipeEventsInLayout(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int lastPressedKey = -1;

        for (int i = 0; i < buttonCollections.length; i++) {

            if (IsXYOverButton(buttonCollections[i].keyButton, x, y) &&
                    buttonCollections[i].currentTouchStatus != ButtonLayout.TouchOngoing &&
                    buttonCollections[i].currentPressStatus == ButtonLayout.KeyUnPressed) {
                //  buttonCollections[i].keyButton.setImageResource(buttonCollections[i].keyPressedImageId);
                buttonCollections[i].keyButton.setBackgroundResource(buttonCollections[i].keyPressedImageId);
                buttonCollections[i].currentPressStatus = ButtonLayout.KeyPressed;
                currentPressedIndex = i;
                lastPressedKey = i;
                buttonCollections[i].currentTouchStatus = ButtonLayout.TouchOngoing;
            }

            else if (!IsXYOverButton(buttonCollections[i].keyButton, x, y)) {
                buttonCollections[i].currentTouchStatus = ButtonLayout.TouchNotOn;
            }
        }

        return lastPressedKey;
    }

    private Handler resetButtonTimerHandler = new Handler();

    // Nagaraj: This is a work around since soundpool API has no
    // callback. We move back the keys after some timeout.
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            ShowUnpressForAllKeys();
        }
    };
}

