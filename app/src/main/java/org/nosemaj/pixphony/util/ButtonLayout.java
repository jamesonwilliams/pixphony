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

import android.widget.ImageButton;

/**
 * Created by scanner on 28-12-15.
 */
public class ButtonLayout {
    public static final int KeyUnPressed = 0;
    public static final int KeyPressed = 1;
    public static final int KeyPressedNone = -1;
    public static final int TouchOngoing = 2;
    public static final int TouchNotOn = 3;

    public int idToButtonMap;
    public int currentPressStatus = KeyUnPressed;
    public int currentTouchStatus = TouchNotOn;
    public int keyUnpressedImageId;
    public int keyPressedImageId;
    public ImageButton keyButton;

    public ButtonLayout(int iidToButtonMap,
                        int icurrentPressStatus,
                        int ikeyUnpressedImageIds,
                        int ikeyPressedImageIds,
                        ImageButton ikeyButton) {
        idToButtonMap = iidToButtonMap;
        currentPressStatus = icurrentPressStatus;
        keyUnpressedImageId = ikeyUnpressedImageIds;
        keyPressedImageId = ikeyPressedImageIds;
        keyButton = ikeyButton;
    }
}
