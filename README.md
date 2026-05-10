# SSM Studio

A large-scale offline Android tablet study platform prototype for the Italian SSM medical specialization exam.

## Highlights

- Kotlin and Jetpack Compose with Material Design 3.
- Tablet-first layout using a persistent navigation rail, spacious cards, dark-mode-aware theme and smooth Compose visibility animations.
- Offline-first Room database for imported questions, attempts, bookmarks and difficult flags.
- Structured JSON question bank split by topic under `app/src/main/assets/questions`.
- 2,500 generated SSM-style questions with topic, subtopic, difficulty, year style, five options, concise explanation, high-yield takeaway and tags.
- JSON import pipeline that seeds Room on first launch.
- Fast search/filtering, topic practice, random mixed quizzes, weak-topic mode, bookmark mode, difficult-question mode and 140-question timed simulations.
- Progress analytics with accuracy, topic mastery, weak areas and daily streak calculation.
- Optional AI Tutor entry point that keeps the app fully usable offline and can later be connected to a cloud endpoint.

## Structure

- `app/src/main/assets/questions`: split JSON question bank plus `index.json`.
- `app/src/main/java/com/ssm/study/data`: Room entities, DAO, JSON importer, database and repository.
- `app/src/main/java/com/ssm/study/viewmodel`: MVVM state and app actions.
- `app/src/main/java/com/ssm/study/ui`: Compose navigation, Material theme and tablet screens.
- `scripts/generate_question_bank.py`: deterministic generator for the bundled question bank.
- `scripts/validate_question_bank.py`: schema and count validation for the JSON assets.

## Topics

Cardiology, pneumology, nephrology, endocrinology, gastroenterology, neurology, psychiatry, pediatrics, infectious diseases, hematology, oncology, rheumatology, dermatology, general surgery, emergency medicine, pharmacology, statistics and epidemiology, gynecology and obstetrics, ophthalmology, ENT, orthopedics and public health.
