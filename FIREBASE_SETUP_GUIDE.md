# Firebase Setup Guide for Personal Vault

Follow these steps to connect your app to Firebase for Google Sign-In and Cloud Firestore sync.

---

## Step 1: Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **Add project**
3. Name it `PersonalVault` (or anything you like)
4. Optionally enable Google Analytics → click **Create project**
5. Wait for it to finish, then click **Continue**

---

## Step 2: Add Your Android App to Firebase

1. In the Firebase project dashboard, click the **Android** icon (or **Add app → Android**)
2. Enter the package name: `com.personalvault.app`
3. Enter a nickname: `Personal Vault`
4. **SHA-1 fingerprint** — you MUST add this for Google Sign-In to work:

   Open a terminal in your project root and run:

   ```bash
   # Debug SHA-1 (for development)
   ./gradlew signingReport
   ```

   Look for the `SHA1:` line under `Variant: debug`. Copy that value and paste it into Firebase.

5. Click **Register app**
6. Download the `google-services.json` file
7. Place it in your project at: `PersonalVault/app/google-services.json`

   (This file MUST be in the `app/` directory, NOT the project root)

---

## Step 3: Enable Google Sign-In in Firebase Auth

1. In Firebase Console, go to **Authentication** (left sidebar)
2. Click **Get started** if it's your first time
3. Go to the **Sign-in method** tab
4. Click **Google** → toggle **Enable**
5. Set a support email (your email)
6. Click **Save**
7. After saving, click **Google** again — you'll see a **Web client ID**. Copy it.

---

## Step 4: Add the Web Client ID to Your App

Open the file:
```
app/src/main/java/com/personalvault/app/ui/AppNavGraph.kt
```

Find this line near the top:
```kotlin
private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID"
```

Replace `YOUR_WEB_CLIENT_ID` with the Web client ID you copied from Firebase Console.

It will look something like:
```kotlin
private const val WEB_CLIENT_ID = "123456789-abcdef.apps.googleusercontent.com"
```

---

## Step 5: Set Up Cloud Firestore

1. In Firebase Console, go to **Firestore Database** (left sidebar)
2. Click **Create database**
3. Choose **Start in test mode** (you can tighten rules later)
4. Select a location closest to your users (e.g., `asia-south1` for India)
5. Click **Enable**

### Recommended Firestore Security Rules (for production)

Once you're ready, go to **Firestore → Rules** and replace with:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Each user can only read/write their own data
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

Click **Publish**.

---

## Step 6: Build and Run

1. Make sure `google-services.json` is in the `app/` folder
2. Sync Gradle (Android Studio will prompt you)
3. Build and run on a device or emulator **with Google Play Services**

   Note: The Google Sign-In picker will NOT work on emulators without Play Services.

4. On first launch, you'll see the login screen. Tap **Sign in with Google**.
5. After signing in, your data automatically syncs to Firestore.

---

## How Sync Works

- **Offline-first**: Room (SQLCipher) is always the source of truth for the UI
- **Background push**: Every local write is also pushed to Firestore
- **Offline writes**: If you're offline, Firestore SDK queues the write and syncs when internet returns
- **Real-time pull**: Firestore snapshot listeners pull remote changes into Room in real-time
- **First-time upload**: When you first sign in, all existing local data is uploaded to Firestore

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Sign-in failed" error | Make sure you added the SHA-1 fingerprint in Firebase Console |
| App crashes on launch | Make sure `google-services.json` is in the `app/` folder |
| Data not syncing | Check Firestore Console → Data tab to see if documents appear |
| "Google Play Services not available" | Use a physical device or an emulator with Play Store |
| Build fails with "google-services.json not found" | Download it again from Firebase Console → Project Settings |

---

## Adding SHA-1 for Release Builds

When you're ready to publish, you'll also need the release SHA-1:

```bash
keytool -list -v -keystore your-release-key.jks -alias your-alias
```

Add this SHA-1 in Firebase Console → Project Settings → Your Apps → Add fingerprint.
