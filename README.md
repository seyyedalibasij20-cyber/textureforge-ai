# TextureForge AI — Build Status

This is a native Android (Kotlin + Jetpack Compose) project generated against
the TextureForge AI master engineering prompt. It is **not fully complete**
yet — this document is the honest status, not marketing copy.

## What's real and implemented

- **`:core:domain`** — pure Kotlin module (no Android deps), `AiProvider`
  interface, all domain models, repository interfaces, use cases including
  `ObserveHomeDashboardUseCase`.
- **`:core:data`** — Room database (entities, DAOs), DataStore-backed
  `UserPrefsDataSource`, repository implementations, connectivity observer,
  knowledge base seeder.
- **`:core:ai`** — Gemini-backed `AiProvider` implementation, structured
  JSON response schemas.
- **`:core:designsystem`** — full "Liquid Glass" system: `GlassCard`,
  `AmbientFlowField` (animated blob background with Reduce Motion / Lite
  Motion fallback), `ConfidenceBadge`, buttons, nav bars, skeleton/empty
  states, type and color tokens.
- **`:app`** — Hilt application class, type-safe Navigation Compose graph,
  `WindowSizeClass`-driven scaffold (bottom nav on compact/medium, nav rail
  on expanded), edge-to-edge `MainActivity`.
- **`:feature:home`** — fully implemented: real `HomeViewModel` (MVI state:
  Loading / Error / Content, no mocks), dashboard UI with Quick Actions,
  Recent Analyses carousel, Active Project card, and the daily Knowledge
  Base tip, all wired to the domain layer.

## What's a tracked placeholder (not a silent omission)

`:feature:analyze`, `:feature:qa`, `:feature:workflow`, `:feature:prompt`,
`:feature:library`, `:feature:projects`, `:feature:settings`, and
`:feature:onboarding` currently contain a single real, wired-up Composable
each — reachable through actual navigation, using the real design system —
but with placeholder body content ("build in progress") instead of the full
feature logic described in Section 7 of the spec. Each file has a doc
comment naming the spec section it will implement. These are being built out
next, in the module order from Section 11.

## What you cannot verify in this environment

This project was generated in a sandbox with no Android SDK and no network
access to `dl.google.com` / `services.gradle.org`, so **it has not been
compiled or run**. Before building it yourself:

1. Open the project root in Android Studio (Ladybug/2024.2+ recommended).
2. Let it generate the Gradle wrapper jar (`File > Sync`, or run
   `gradle wrapper` once if you have a local Gradle install) — the wrapper
   jar itself isn't checked in here.
3. Add your Gemini API key to a local, non-committed location (e.g.
   `local.properties` as `GEMINI_API_KEY=...`, read via `BuildConfig` — this
   plumbing still needs to be added to `:core:ai`'s build file when that
   module's provider is finalized).
4. Sync and build. Expect some first-pass compile errors — this is
   generated code that has not yet been run through a real compiler; treat
   this as a strong first draft, not a guaranteed green build.
