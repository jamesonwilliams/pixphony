#!/bin/bash

adb uninstall jp.kshoji.blemidi.sample &
./gradlew assembleDebug &
wait

adb install -r -d sample/build/outputs/apk/sample-debug.apk
adb shell am start -n jp.kshoji.blemidi.sample/.CentralActivity 
