# DUDE

DUDE is an Android-native personal assistant prototype. This repository builds a debug APK using GitHub Actions, so development and compilation can be done without an Android IDE.

## Current build (v0.1)

- Native Android chat interface
- Offline commands: `help`, `time`, `date`, and `status`
- Local notes: `remember [note]`, `what do you remember`, and `forget all`
- Conversation and notes stored locally on the device
- No API key or paid service required

## Important limitations

This is a starter application, **not yet an AI language model**. It does not currently provide open-ended intelligent answers, voice input/output, web search, external app control, or autonomous actions. Those capabilities will be added in later stages. Training a capable general-purpose model from scratch on a phone with no budget is not realistic.

## Build an APK on GitHub

1. Open the **Actions** tab.
2. Select **Build DUDE Android APK**.
3. Select **Run workflow** and run it on `main` (or push a commit to `main` to trigger it automatically).
4. Open the completed workflow run.
5. Under **Artifacts**, download `DUDE-debug-apk`.
6. Extract the ZIP and install `app-debug.apk` on your Android phone. Android may ask you to allow installs from your browser or file manager.

The artifact is a debug-signed APK for testing, not a Play Store release.

## Technology

- Kotlin
- Android SDK 35
- Minimum Android API 23
- Gradle 8.9 / Android Gradle Plugin 8.7.3
- Java 17

## Privacy

The app currently makes no network requests and does not request the internet permission. Notes and conversation are stored in app-private local preferences. Clearing app data or uninstalling the app removes that local data.
