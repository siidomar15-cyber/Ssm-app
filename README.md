# SSM Studio

A modern Android tablet study app prototype for the Italian SSM medical specialization exam.

## Highlights

- Kotlin and Jetpack Compose with Material Design 3.
- Tablet-first layout using a persistent navigation rail and spacious cards.
- Offline-first Room database for questions, attempts, bookmarks and difficult flags.
- MVVM structure with repository-backed state flows.
- Previous-SSM-style questions organized by topic and year.
- Fast quiz flow with five-answer questions, instant feedback, concise explanations and high-yield takeaways.
- Progress tracking, weak-area analytics, bookmarks, difficult-question review and timed simulation mode.

## Structure

- `app/src/main/java/com/ssm/study/data`: Room entities, DAO, database, repository and mock question bank.
- `app/src/main/java/com/ssm/study/viewmodel`: MVVM state and screen actions.
- `app/src/main/java/com/ssm/study/ui`: Compose navigation, Material theme and tablet screens.

## Topics

Cardiology, pneumology, nephrology, endocrinology, neurology, psychiatry, pediatrics, infectious diseases, surgery, emergency medicine, oncology, rheumatology, dermatology, pharmacology, statistics, gynecology, gastroenterology, ophthalmology, ENT and orthopedics.
