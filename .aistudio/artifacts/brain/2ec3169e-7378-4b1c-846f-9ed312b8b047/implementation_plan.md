# DOOMSDAY — Android Live Countdown & Glassmorphism Widget App

DOOMSDAY is a lightweight, high-precision minimalist Android countdown application and home/lock-screen widget suite tracking the exact calendar time remaining until **18 December 2026, 12:00:00 AM (00:00 Asia/Kolkata IST)**, with seamless continuation into negative elapsed time once reached.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product and design choices have been confirmed from your responses and incorporated into the architecture below:

- **Default Lock Screen Integration**: **Persistent Lock Screen Notification with Live Countdown** enabled as the primary zero-root lock-screen integration, paired with device-specific Lock Screen Widget compatibility detection and setup guides.
- **Default Glassmorphism Preset**: **Dark Glass with Subtle Silver Highlights** selected as the out-of-the-box visual theme, alongside 5 additional one-tap presets (*Pure Glass*, *Frosted Glass*, *Transparent Minimal*, *Midnight*, *Silver Glass*) and granular sliders.
- **Default Digit Animation & Analog Stick Option**: **Flip Clock (Mechanical Flip Effect)** set as the default digit transition, plus an **Analog Dark BG with White Sticks** mode (precision minimalist dark chronometer dial with crisp white tick sticks and sweeping hands alongside the 12 digital animation styles).

---

## 1. Overview & Core Concept

- **What It Does**: Continuously calculates and renders the exact remaining time to `2026-12-18T00:00:00+05:30` across five calendar-accurate units (**Months | Days | Hours | Minutes | Seconds**) in a customizable glassmorphic in-app display, two resizable home-screen widgets (Compact & Large), and a persistent lock-screen live display.
- **Target Audience / Persona**: Users seeking a refined, luxury-dark horological countdown centerpiece on their home screen and lock screen without battery bloat, ads, or unnecessary permissions.
- **Key Value**: True calendar-aware month/day computation (never a crude 30-day division), negative/elapsed countdown after the target date passes, 13 horological and typographic animation styles, and battery-conscious widget/lock-screen updates that survive device reboots and timezone changes.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Home (Live Preview & Horological Display)**: Opens directly to a deep obsidian canvas featuring the `DOOMSDAY` header, the live `MM : DD : HH : MM : SS` glassmorphic countdown card (or toggleable **Analog White Sticks Dark Dial** view), quick-access pills for active preset/animation, and status indicators for Home Widget and Lock Screen live notification.
2. **Customize (Glassmorphism Studio)**: Live interactive preview header at the top while adjusting 6 instant presets or fine-tuning sliders for Font Size, Font Weight, Text Opacity, Glass Background Opacity, Border Opacity, Blur Intensity, Letter/Number Spacing, Horizontal/Vertical Alignment, Corner Radius, Widget Dimensions, and Text/Accent Color.
3. **Animations (Digit & Analog Engine)**: Interactive sandbox where ticking digits demonstrate all 13 styles in real time (*Flip Clock*, *Analog Dark BG with White Sticks*, *Slide Up*, *Slide Down*, *Fade*, *Scale*, *Rolling Odometer*, *Slot Machine*, *Blur Transition*, *Mechanical Clock*, *Minimal Instant*, *Bounce*, and *Typewriter*), with controls for Speed, Duration, Direction, Easing Curve, Intensity, and Individual vs. Whole-Number mode.
4. **Widgets (Compact & Large Studio)**: Toggle between **Compact** (`01 : 09 : 12 : 59 : 00` / `M | D | H | M | S`) and **Large** (`01 MONTH : 09 DAYS` / `12 HOURS : 59 MINUTES : 00 SECONDS`) widget previews, customize widget layout alignment and sizing, trigger native "Pin Widget to Home Screen" where supported, or follow the 5-step interactive placement guide.
5. **Lock Screen (Live Integration Hub)**: One-tap toggle for the **Persistent Lock Screen Countdown Notification** (handling Android 13+ `POST_NOTIFICATIONS` permission gracefully), lock-screen visibility verification, and device-aware guidance for Android tablet/Pixel/OEM lock-screen widget slots.
6. **Settings**: Manage 24h/IST reference display, negative countdown format (`-00 : 00 : 00 : 00 : 01`), battery optimization status check, and one-tap Reset to Factory Dark Glass Defaults.

### Visual Identity & Theme
- **Aesthetic Direction**: Luxury horological dark minimalism with layered translucent glass panels, hairline silver-white borders, and precision tabular numerals.
- **Color Palette & Mood**:
  - `--background`: Deep Obsidian `#050507` with subtle radial charcoal vignette `#0E1015`
  - `--surface-glass`: Translucent Dark Charcoal `rgba(20, 22, 28, 0.68)` (configurable `0.0` to `0.95`)
  - `--border-silver`: Specular Silver Hairline `rgba(226, 232, 240, 0.22)`
  - `--primary`: Pure Crisp White `#F8FAFC` for primary countdown digits and analog white sticks
  - `--secondary`: Muted Metallic Silver `#94A3B8` for unit labels (`MONTH | DAYS | HOURS | MINUTES | SECONDS`)
  - `--accent`: Cold Titanium Silver `#CBD5E1` and subtle Crimson Ember `#EF4444` indicator when in negative/elapsed countdown mode
- **Typography & Hierarchy**:
  - Bundled local Google Fonts (`Space Grotesk` & `JetBrains Mono`) for geometric headers and zero-jitter tabular numerals (`tnum`), ensuring colons never shift horizontally as seconds tick.
- **Interactive Feedback & Motion**:
  - Spring-driven physics (`spring()`) and customizable easing curves (`FastOutSlowIn`, `Linear`, `EaseInOutCubic`, `SpringBounce`) with instant live preview feedback across all controls.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Calendar-Aware Time Calculation (`java.time` / `ZonedDateTime`)**
  - *Chosen Approach*: Compute exact `Period` (years converted to months + remaining calendar months and days) and `Duration` (hours, minutes, seconds) between the current instant in `Asia/Kolkata` (`ZoneId.of("Asia/Kolkata")`) and `2026-12-18T00:00:00+05:30`.
  - *Why*: Respects actual month lengths (28/29/30/31 days) and handles device timezone shifts or manual clock changes automatically. When current time exceeds the target, calculation flips sign cleanly to count elapsed time upward with a leading `-`.
- **Decision 2: Battery-Safe Home Widgets & Lock Screen Live Surface**
  - *Chosen Approach*: Use native `AppWidgetProvider` (`RemoteViews`) with styled glassmorphic drawables, minute/alarm-aligned updates, and native `Chronometer` / scheduled broadcast triggers on `ACTION_TIME_CHANGED`, `ACTION_TIMEZONE_CHANGED`, `ACTION_BOOT_COMPLETED`, and user preference updates—plus an optional lightweight lock-screen notification channel (`VISIBILITY_PUBLIC`) for always-on lock-screen visibility.
  - *Why*: Android `AppWidgetManager` throttles per-second custom canvas animations in home widgets to protect battery. Keeping full 60fps per-digit & analog-stick animations in the Compose app while using lightweight system-friendly updates for widgets and lock-screen notifications guarantees zero battery drain and 100% offline reliability.
- **Decision 3: Local Persistence with Jetpack DataStore**
  - *Chosen Approach*: Store all glassmorphism parameters, active preset, animation engine settings, widget layout overrides, and lock-screen toggle states in `DataStore<Preferences>`.
  - *Why*: Asynchronous, corruption-proof, offline-first persistence that immediately broadcasts changes to both the Compose UI and active `AppWidgetProvider` instances.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram
```
┌───────────────────────────────────────────────────────────────────────────┐
│                         UI LAYER (Jetpack Compose)                        │
│  ┌────────────┐ ┌─────────────┐ ┌────────────┐ ┌───────────┐ ┌──────────┐ │
│  │ HomeScreen │ │CustomizeScrn│ │AnimScreen  │ │WidgetScrn │ │LockScreen│ │
│  └─────┬──────┘ └──────┬──────┘ └─────┬──────┘ └─────┬─────┘ └────┬─────┘ │
│        └───────────────┴──────────────┼──────────────┴────────────┘       │
│                                       ▼                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │       DigitAnimationEngine (12 Digital Styles + Analog Sticks)      │  │
│  └────────────────────────────────────┬────────────────────────────────┘  │
└───────────────────────────────────────┼───────────────────────────────────┘
                                        ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                    VIEWMODEL & DOMAIN LAYER (MVVM)                        │
│  ┌───────────────────────────────┐   ┌─────────────────────────────────┐  │
│  │      DoomsdayViewModel        │◄──┤   CountdownCalculator (IST)     │  │
│  │ (1s Ticker Flow + UI State)   │   │ (Calendar Period + Duration +   │  │
│  └───────────────┬───────────────┘   │  Negative Elapsed Time Mode)    │  │
│                  │                   └─────────────────────────────────┘  │
└──────────────────┼────────────────────────────────────────────────────────┘
                   ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                 DATA & SYSTEM INTEGRATION LAYER                           │
│  ┌───────────────────────────────┐   ┌─────────────────────────────────┐  │
│  │   PreferencesRepository       │   │  System Broadcast & Widget Hub  │  │
│  │   (Jetpack DataStore)         │──►│  • CompactWidgetProvider        │  │
│  │   • 6 Glass Presets           │   │  • LargeWidgetProvider          │  │
│  │   • 14 Visual Sliders         │   │  • LockScreenNotificationHelper │  │
│  │   • Animation & Dial Config   │   │  • Boot & TimeChangeReceiver    │  │
│  └───────────────────────────────┘   └─────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────┘
```

### Data Model & State
- `CountdownSnapshot`: Holds `months`, `days`, `hours`, `minutes`, `seconds`, `isNegative` (boolean for elapsed mode after Dec 18, 2026 00:00 IST), `totalRemainingMillis`, and formatted strings (`"01 : 09 : 12 : 59 : 00"`).
- `GlassCustomizationState`: Holds `preset` (`DARK_GLASS`, `PURE_GLASS`, `FROSTED_GLASS`, `TRANSPARENT_MINIMAL`, `MIDNIGHT`, `SILVER_GLASS`), `fontSizeSp`, `fontWeight`, `textOpacity`, `glassOpacity`, `borderOpacity`, `blurRadius`, `letterSpacing`, `numberSpacing`, `horizontalAlignment`, `verticalAlignment`, `cornerRadiusDp`, `widgetWidthScale`, `widgetHeightScale`, `textColorHex`, and `showAnalogSticks`.
- `AnimationConfigState`: Holds `style` (12 digital animations + `ANALOG_WHITE_STICKS`), `speedMultiplier`, `durationMs`, `direction` (`UP`, `DOWN`, `AUTO`), `easingCurve`, `intensity`, and `animatePerDigit` (individual digit vs. whole number).

### Interactive Component & State Mapping
- **Live 1-Second Ticker**: `DoomsdayViewModel` runs a lifecycle-aware coroutine ticker aligned to the system millisecond clock (`1000 - (System.currentTimeMillis() % 1000)`), ensuring exact second boundaries without drift.
- **Customization & Preset Binding**: Selecting any of the 6 presets or dragging any of the 14 customization sliders writes to `PreferencesRepository` and triggers `AppWidgetManager` + `LockScreenNotificationHelper` refresh intents so home widgets and lock screen reflect styling updates immediately.
- **Reboot & Time Change Recovery**: `TimeAndBootReceiver` listens for `BOOT_COMPLETED`, `TIME_SET`, and `TIMEZONE_CHANGED` to recalculate the IST target delta, refresh all placed Compact/Large widgets, and restore the lock-screen countdown notification if enabled.
