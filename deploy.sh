#!/bin/bash

adb uninstall org.nosemaj.pixphony &
./gradlew assembleDebug &
wait

adb install -r -d app/build/outputs/apk/app-debug.apk
adb shell am start -n org.nosemaj.pixphony/.SplashActivity 
