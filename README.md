# Personal Vault — Android App

A single place to track **Health, Wealth, Expenses, Investments, Proofs/Documents,
and Insurance**. Built with modern Android tooling and stored fully encrypted on your
device.

## Features

- Six pre-built life categories on the home screen, each with a live entry count.
- Add any entry with **title, date, optional amount, notes**, and any number of
  **attachments** (photos from camera, images from gallery, or any file from the
  device).
- Attachments are copied into the app's private internal storage — other apps cannot
  see them.
- **Encrypted local database** using SQLCipher. The 32-byte random passphrase is
  generated on first launch and stored in `EncryptedSharedPreferences`, which is
  backed by the Android Keystore.
- **Biometric app lock** on every launch (fingerprint / face / device PIN fallback).
- No internet, no analytics, no cloud — 100% on-device.
- Jetpack Compose UI with Material 3.

## Project layout

```
PersonalVault/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml       # version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/personalvault/app/
        │   ├── MainActivity.kt
        │   ├── PersonalVaultApp.kt
        │   ├── data/            # Room + SQLCipher
        │   ├── ui/theme/
        │   ├── ui/lock/         # Biometric gate
        │   ├── ui/home/
        │   ├── ui/list/
        │   ├── ui/edit/
        │   └── util/            # attachment + formatting helpers
        └── res/                 # strings, themes, launcher icon, file paths
```

## Prerequisites

- **Android Studio** Ladybug (2024.2) or newer. Hedgehog is too old for Kotlin 2.0.
- **JDK 17** (bundled with recent Android Studio versions — you don't need to
  install it separately).
- A phone running **Android 7.0 (Nougat) or newer** with a USB cable.

## 1. Open the project

1. Launch Android Studio.
2. Choose **File → Open…** and select the `PersonalVault` folder (this folder).
3. Android Studio will ask whether to trust the project — click **Trust Project**.
4. Wait while Gradle downloads dependencies for the first sync. This can take
   several minutes the first time.
5. You'll also be prompted to **generate the Gradle wrapper** if `gradlew` is missing.
   Open the Terminal inside Android Studio and run:

   ```
   gradle wrapper
   ```

   (Only needed the first time — afterwards you can use `./gradlew` on macOS/Linux
   or `gradlew.bat` on Windows.)

## 2. Enable USB debugging on your phone

1. On your phone, open **Settings → About phone**.
2. Tap **Build number** seven times until it says *"You are now a developer"*.
3. Go back to **Settings → System → Developer options**
   (on some phones: Settings → Additional settings → Developer options).
4. Toggle **USB debugging** ON.
5. Optional but recommended: also toggle **Install via USB** and
   **USB debugging (Security settings)**.

## 3. Connect your phone

1. Plug the phone into your computer with a **USB cable** (a data cable, not
   charge-only).
2. On the phone, change the USB connection mode to **File Transfer / MTP**
   (pull down the notification shade to do this).
3. A pop-up will appear on the phone: **"Allow USB debugging from this computer?"**
   — check *"Always allow"* and tap **Allow**.
4. Back in Android Studio, the device picker at the top should now show your phone
   (e.g. *"Samsung SM-S918B"*).

If the phone doesn't show up:
- Try a different USB cable / port.
- On Windows, install your manufacturer's USB driver (Google, Samsung, Xiaomi,
  OnePlus, etc. each have their own).
- Run `adb devices` in a terminal — your device should appear as `device`
  (not `unauthorized`).

## 4. Run the app on your phone

1. Make sure your phone is selected in the device dropdown at the top of Android
   Studio.
2. Click the green **Run ▶** button (or press `Shift+F10` / `Ctrl+R`).
3. Android Studio will compile the app, install it on the phone, and launch it.
4. The first launch will trigger the biometric prompt on your phone — authenticate
   with your fingerprint / face / PIN to unlock the vault.

Alternative (command line):

```
# From the project root
./gradlew installDebug       # macOS / Linux
gradlew.bat installDebug     # Windows
```

Then open the app drawer on your phone and tap **Personal Vault**.

## 5. Wireless debugging (optional, Android 11+)

If you want to unplug the cable after the first run:

1. On the phone, **Developer options → Wireless debugging → Pair device with
   pairing code**.
2. In Android Studio, **Device Manager → Pair Devices Using Wi-Fi** and enter the
   code shown on your phone.
3. Hit Run — no cable needed.

## 6. Security notes

- The database file `personal_vault.db` lives in the app's private data
  directory and is encrypted with SQLCipher. Even with adb / root access, you
  can't read it without the key.
- The encryption key is generated at first launch from
  `SecureRandom` and stored in `EncryptedSharedPreferences`, which is wrapped
  by a hardware-backed master key in the Android Keystore on supported devices.
- Cloud backup and device-to-device transfer are disabled in the manifest
  (`backup_rules.xml` / `data_extraction_rules.xml`), so your vault never
  leaves the device via Google's backup system.
- Attachments are stored in `filesDir/attachments/` (app-private). Other apps
  cannot read them.

## 7. Troubleshooting

**"Unresolved reference: sqlcipher"** — run **File → Sync Project with Gradle
Files**. The first sync pulls down the SQLCipher native library.

**Biometric prompt never appears** — the device doesn't have a screen lock
enrolled. The app will detect this and skip the lock screen. Set up a PIN or
fingerprint in Android Settings to re-enable.

**Camera attachment fails on Android 10+** — make sure you granted the camera
permission when prompted. You can reset it under Settings → Apps → Personal
Vault → Permissions.

**App installs but crashes at startup** — check Logcat in Android Studio for
the stack trace. The most common cause is a stale Gradle cache; try
**Build → Clean Project** followed by **Build → Rebuild Project**.

## 8. Next steps / ideas to extend

- Add search across all categories.
- Add charts (e.g. monthly expenses, net worth over time) using the amount field.
- Add export to encrypted ZIP for off-device backup.
- Add categories for reminders (insurance renewal, health checkups).
- Add cloud sync via your own WebDAV / Nextcloud — keep it under your control.

Happy tracking.
