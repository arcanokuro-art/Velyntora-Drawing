#!/usr/bin/env bash
# Keep device failures bounded and retain evidence before the emulator is stopped.
set -uo pipefail
mkdir -p build/android-diagnostics
adb logcat -c
timeout 240s gradle :app:connectedDebugAndroidTest --stacktrace -Pandroid.testInstrumentationRunnerArguments.timeout_msec=45000
result=$?
timeout 15s adb logcat -d > build/android-diagnostics/logcat.txt 2>&1
timeout 15s adb shell dumpsys activity activities > build/android-diagnostics/activities.txt 2>&1
timeout 15s adb shell dumpsys window windows > build/android-diagnostics/windows.txt 2>&1
timeout 15s adb exec-out screencap -p > build/android-diagnostics/screen.png 2>/dev/null
exit "$result"
