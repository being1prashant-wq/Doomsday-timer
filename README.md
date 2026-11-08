# DOOMSDAY — Android Live Countdown & Glassmorphism Widget App

**DOOMSDAY** is a lightweight, offline-first minimalist horological Android application and widget suite that displays a continuously updating countdown to:

> **18 December 2026, 12:00:00 AM (00:00:00 Asia/Kolkata IST)**

---

## Key Features

1. **Calendar-Aware IST Countdown Engine (`CountdownCalculator`)**
   - Calculates exact remaining **Months | Days | Hours | Minutes | Seconds** using `java.time.Period` and `java.time.Duration` anchored to `Asia/Kolkata (IST)`.
   - Never uses a fixed 30-day month approximation; respects 28/29/30/31-day calendar months and leap years.
   - **Negative / Elapsed Countdown Mode**: Once 18 December 2026 00:00 IST passes, the timer automatically transitions into negative elapsed-time mode (`-00 : 00 : 00 : 00 : 01`) and continues running indefinitely.

2. **Glassmorphism Studio (`CustomizeScreen`)**
   - **6 Instant Presets**: *Dark Glass (Default)*, *Pure Glass*, *Frosted Glass*, *Transparent Minimal*, *Midnight*, and *Silver Glass*.
   - **14 Granular Controls**: Font size, font weight, text opacity, glass background opacity, border opacity, blur intensity, letter spacing, number spacing, horizontal/vertical alignment, widget dimensions, corner radius, text color tint, background transparency, and silver specular highlights.

3. **13 Digit & Analog Animation Modes (`DigitAnimationEngine`)**
   - Includes **Flip Clock** (default split-flap mechanical flip), **Analog Dark BG with White Sticks** (precision dark chronometer dial with 60 white stick markers and sweeping hands), **Slide Up**, **Slide Down**, **Fade**, **Scale**, **Rolling Odometer**, **Slot Machine**, **Blur Transition**, **Mechanical Clock**, **Minimal Instant**, **Bounce**, and **Typewriter**.
   - Full customization of animation speed, duration, direction, easing curve, transition intensity, and individual-digit vs. whole-number mode.

4. **Home Screen Widgets (`CompactWidgetProvider` & `LargeWidgetProvider`)**
   - **Compact Widget (4×1)**: Displays `DOOMSDAY`, `01 : 09 : 12 : 59 : 00`, and `M | D | H | M | S`.
   - **Large Widget (4×2)**: Displays `DOOMSDAY`, `01 MONTH : 09 DAYS`, and `12 HOURS : 59 MINUTES : 00 SECONDS`.
   - Supports resizing, transparent glass styling, and automatic recovery on device reboot (`ACTION_BOOT_COMPLETED`) and time/timezone changes.

5. **Official Lock Screen Integration (`LockScreenIntegrationScreen`)**
   - **Persistent Lock Screen Live Notification**: Uses an official Android `VISIBILITY_PUBLIC` ongoing silent chronometer surface so the countdown is visible on any Android phone's lock screen without root, custom ROMs, or battery-draining wake locks.
   - **Keyguard Widget Support**: Declares `home_screen|keyguard` widget categories with device-specific setup instructions for Samsung One UI / LockStar and Android Tablet / Pixel Glanceable Hub lock screens.

---

## Widget & Lock Screen Setup Instructions

### Adding Home Screen Widgets
1. Long-press an empty space on your Android Home Screen (or tap **Add Widget to Home Screen** inside the **Widgets** tab).
2. Select **Widgets** from the launcher menu.
3. Locate **Doomsday** and choose **Doomsday Compact** or **Doomsday Large**.
4. Drag the widget to your desired position and drag its border handles to resize.

### Enabling Lock Screen Display
1. Open the **Lock Screen** tab in Doomsday.
2. Enable **Lock Screen Live Countdown** (grant notification permission on Android 13+ if prompted).
3. Ensure Android *Settings → Notifications → Lock screen notifications* is set to **Show alerting and silent notifications**.

---

## GitHub Actions APK Build Workflow

This repository includes an automated GitHub Actions workflow at `.github/workflows/build-apk.yml` that builds the APK cleanly on every push, pull request, or manual `workflow_dispatch` trigger:
1. Configures **JDK 21 (Temurin)**, **Android SDK (API 36.1)**, and **Gradle 9.3.1**.
2. Automatically provisions `.env` from `.env.example` and restores `debug.keystore` from `debug.keystore.base64` (or generates a fallback keystore) so signing never fails in CI.
3. Executes `:app:testDebugUnitTest` and `:app:assembleDebug`.
4. Uploads the built APK as the **`Doomsday-debug-apk`** downloadable workflow artifact.

