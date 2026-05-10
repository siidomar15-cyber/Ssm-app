# SSM Studio

A modern Android study app prototype for the Italian SSM medical specialization exam. The app is written in Kotlin with Jetpack Compose, Material Design 3, Navigation Compose, and an offline Room question database.

## Highlights

- Kotlin, Jetpack Compose, and polished Material Design 3 screens.
- Responsive layouts: bottom navigation on phones, navigation rail on tablets, and a wider rail-first layout for large screens.
- Offline-first Room database for questions, attempts, bookmarks, and difficult flags.
- Memory-conscious question import: JSON files are read per topic and inserted into Room in small chunks instead of keeping the full bank in application state.
- Loading and error states for question-bank import and quiz preparation.
- Topic drills, timed simulation mode, weak-area analytics, bookmarks, and difficult-question review.
- JVM tests that validate every JSON question file before building an APK.

## Project structure

- `app/src/main/java/com/ssm/study/data`: Room entities, DAO, database, repository, JSON parser, and chunked asset import.
- `app/src/main/java/com/ssm/study/viewmodel`: MVVM state, question-bank loading state, quiz state, and screen actions.
- `app/src/main/java/com/ssm/study/ui`: Compose navigation, responsive layouts, Material theme, and screens.
- `app/src/main/assets/questions`: Topic-grouped JSON question banks.
- `app/src/test/java/com/ssm/study`: JSON validation tests.

## Question bank format

Add one UTF-8 `.json` file per topic under `app/src/main/assets/questions`. Each file should contain a JSON array. The file name must match the enum topic name in lowercase, for example `cardiology.json` contains only `CARDIOLOGY` questions.

Each question object must include:

```json
{
  "id": "cardiology-2024-001",
  "topic": "CARDIOLOGY",
  "year": 2024,
  "stem": "Question stem...",
  "options": ["A", "B", "C", "D", "E"],
  "correctIndex": 0,
  "explanation": "Short explanation.",
  "takeaway": "High-yield takeaway."
}
```

Validation rules are enforced by `QuestionBankJsonTest`:

- every JSON file must parse successfully;
- every file must contain at least one question;
- every file must contain one topic that matches its file name;
- all question IDs across files must be unique;
- every question must have exactly five non-blank options;
- `correctIndex` must be between `0` and `4`.

## Build requirements

- Android Studio Ladybug or newer is recommended.
- JDK 17 is required for the Android Gradle Plugin and Kotlin toolchain.
- Android SDK Platform 35 must be installed.

If your shell defaults to a newer JDK, set `JAVA_HOME` before running Gradle:

```bash
export JAVA_HOME=/path/to/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
```

## Building and testing from the command line

This repository does not require secrets or network calls at runtime. Dependency resolution may require access to Google's Maven repository and Maven Central the first time you build.

Run JSON/import validation and unit tests:

```bash
gradle testDebugUnitTest
```

Assemble a debug APK:

```bash
gradle assembleDebug
```

The debug APK will be created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Assemble a release APK:

```bash
gradle assembleRelease
```

The unsigned release APK will be created at:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

For a signed release, create a keystore and configure Android Studio's **Build > Generate Signed Bundle / APK** flow, or add signing config values through local, non-committed Gradle properties.

## Building in Android Studio

1. Open Android Studio.
2. Choose **File > Open** and select this repository root.
3. Let Gradle sync finish. If sync fails because of the JDK, set **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK** to JDK 17.
4. Select an emulator or device running Android 8.0 (API 26) or newer.
5. Click **Run app**.

## Current topics

Cardiology, pneumology, nephrology, endocrinology, neurology, and statistics currently include bundled sample questions. The app already displays the remaining SSM topic categories so additional files can be added without UI changes.
