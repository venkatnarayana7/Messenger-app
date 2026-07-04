# PixelFlow — Native Android (Kotlin + Jetpack Compose)

A fast, private, offline image utility app. Four core tools (Resize, Compress,
Upscale, Convert) plus Batch and History. **No ads. No watermarks. No AI.
No login for free features.** Every rule from the spec is honored.

---

## Requirements

| Tool             | Version                                |
|------------------|----------------------------------------|
| Android Studio   | **Ladybug (2024.2.1)** or newer        |
| JDK              | **17** (bundled with Android Studio)   |
| Android Gradle Plugin | **8.7.3**                         |
| Kotlin           | **2.0.21**                             |
| Compose Compiler | **2.0.21** (via `kotlin-compose` plug‑in) |
| minSdk / target  | **24 / 35**                            |
| Gradle wrapper   | **8.10.2**                             |

The wrapper JAR is not shipped in this ZIP to keep it lightweight. On first
open in Android Studio just run **File → Sync Project with Gradle Files** —
Android Studio will download the wrapper JAR automatically using
`gradle/wrapper/gradle-wrapper.properties`. Alternatively:

```
gradle wrapper --gradle-version 8.10.2
```

---

## How to open and run

1. Open Android Studio → **Open** → select `PixelFlow-Android/`.
2. Wait for Gradle sync (first sync will download dependencies).
3. Pick a device / emulator (API 24+).
4. Press **Run** ▶️.

The launcher app **PixelFlow** will install. No permissions are prompted at
launch — the modern Photo Picker is used for image selection (no
`READ_MEDIA_IMAGES` prompt on Android 13+).

---

## Project layout

```
PixelFlow-Android/
├── build.gradle.kts             (root)
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml       (version catalog)
│   └── wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/                 (colors, themes, launcher, xml/backup rules)
        └── java/com/pixelflow/app/
            ├── PixelFlowApp.kt          (manual DI container)
            ├── MainActivity.kt
            ├── ui/
            │   ├── theme/               (Color, Type, Shape, Theme)
            │   ├── components/          (PrimaryButton, SecondaryButton,
            │   │                        BottomNavBar, PixelFlowLogo,
            │   │                        ImagePickerBox, PixelChip, ToolCard,
            │   │                        TopBar)
            │   ├── navigation/          (Routes, NavGraph)
            │   └── screens/
            │       ├── splash/          (SplashScreen)
            │       ├── welcome/         (WelcomeScreen)
            │       ├── home/            (HomeScreen)
            │       ├── resize/          (VM + Screen)
            │       ├── compress/        (VM + Screen)
            │       ├── upscale/         (VM + Screen)
            │       ├── convert/         (VM + Screen)
            │       ├── batch/           (VM + Screen)
            │       ├── history/         (HistoryScreen)
            │       ├── premium/         (PremiumScreen)
            │       └── settings/        (SettingsScreen)
            ├── data/
            │   ├── entity/              (HistoryEntry, AppSettings)
            │   ├── db/                  (HistoryDao, SettingsDao, PixelFlowDatabase)
            │   ├── repository/          (HistoryRepository, SettingsRepository)
            │   └── prefs/               (OnboardingPrefs — DataStore)
            ├── domain/
            │   ├── model/               (Models.kt — enums, presets, limits)
            │   └── processing/          (Interpolation, Resizer, Compressor,
            │                             Converter, Upscaler)
            └── util/                    (FormatUtil, MediaSaveUtil)
```

---

## Spec compliance map (Section 2 non-negotiables)

| Rule                                    | Where enforced                                              |
|-----------------------------------------|-------------------------------------------------------------|
| **No ads**                              | No ad SDK in `libs.versions.toml` — grep confirms it.       |
| **No watermarks**                       | Zero watermark code path in `ImageResizer`, `ImageCompressor`, `ImageConverter`, `ImageUpscaler`. |
| **No AI / no ML**                       | Only classical interpolation in `Interpolation.kt` (Nearest, Bilinear, Bicubic, Lanczos). Copy on Upscale / Home avoids any "AI" wording. |
| **No login for free tools**             | Nav graph has no auth screens. Account only requested on Premium upgrade tap. |
| **Privacy / offline**                   | Manifest has no `INTERNET` permission. `SettingsRepository` and `HistoryRepository` are local-only. |
| **Premium unlocks limits, not features**| `FreeTierLimits.BATCH_MAX_FILES = 10` (Premium removes cap). All four tools work on free tier. |

---

## Implementation notes

- **Interpolation algorithms** — full pure-Kotlin implementations in
  `Interpolation.kt`. Lanczos uses `a = 3`. Bicubic uses Catmull-Rom
  (`a = -0.5`). All run on `Dispatchers.Default`.
- **Convert format transparency** — non-alpha targets (JPG/BMP) composite
  transparent input onto white **before** encoding; a red warning banner
  informs the user beforehand.
- **Format encoding fallback** — Android's `Bitmap.compress` supports
  JPEG / PNG / WEBP natively. For BMP / TIFF / GIF / HEIC / ICO the app
  falls back to PNG bytes with the requested extension. Swap in a native
  encoder later if desired.
- **Real compression estimate** — `ImageCompressor.estimateJpegSize`
  actually encodes a downsampled bitmap and scales up, so the "Estimated
  Size" number on the Compress screen is a genuine value, not a heuristic.
- **Batch** — sequential (not parallel) processing so a 50-file job doesn't
  spike heap. Progress bar updates after each file.
- **History** — Room `Flow` queries drive the UI so Storage Saved / count
  update reactively without manual refresh.
- **No dark mode** in v1 (light-only per Section 4).

---

## Things intentionally NOT implemented (out of scope per spec)

- **Cloud sync / AWS integration** — Section 3 explicitly says v1 excludes
  it. Repository layer is structured so it can be added later without a
  rewrite.
- **Google Play Billing** — Premium screen has a stub `onClick` for the
  Upgrade button. Wiring Play Billing is a business decision; drop your
  billing library into `app/build.gradle.kts` and hook the click.
- **Text encoders for BMP/TIFF/ICO/HEIC/GIF** — Android's `Bitmap.compress`
  doesn't produce these; PNG-fallback is used. Add a lightweight native
  encoder later if needed.

---

## Testing (manual)

Run on an emulator. Suggested happy-path check:

1. Launch → splash → welcome (first launch only) → home.
2. Home → **Resize Image** → pick a photo → drag slider to 50% → **Resize**
   → check gallery `Pictures/PixelFlow/pixelflow_resize_*.png`.
3. Home → **Compress Image** → drag quality slider → **Compress & Save** →
   check gallery.
4. Home → **Upscale Image** → 2×, Bilinear → **Export** → progress bar
   animates.
5. Home → **Convert Format** → select a PNG with transparency → pick JPG
   → red banner warns → **Convert** → verify white background.
6. Home → **Batch** → pick multiple images → **Run Batch** → sequential
   progress.
7. Bottom nav → **History** → row shows, delete works, "Delete History"
   dialog works.
8. Bottom nav → **Premium** → gradient card renders. Upgrade is a stub.
9. Bottom nav → **Settings** → guest account, all rows visible.

All eleven screens have `testTag` set on the root and on interactive
elements for future instrumented tests.
