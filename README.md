# OrbitAI

An AI-driven ERP platform for construction, built with Kotlin Multiplatform and Compose
Multiplatform so Android and iOS share one UI and one business-logic layer.

## Scope

OrbitAI covers site updates, issue tracking, task assignment, team management, material and
inventory updates, audit logs and invoice generation, presented through role-aware interactive
dashboards. AI assists with material efficiency and cost savings, and with assigning tasks and
issues to the right team members. A built-in inbox handles quick updates between roles.

## Roles

Access is governed by role-based access control. The seven roles are CEO, Project Manager, Site
Engineer, Contractor, QA/QC, Warehouse Manager and Procurement Manager.

Roles are never checked directly in UI code. `UserRole` maps to a set of `Permission` values via
`Permission.forRole`, and screens gate on permissions. This keeps authorisation rules in one place
instead of scattered role comparisons. The client-side check hides affordances a user cannot act
on; the backend remains the authority.

## Module layout

| Module | Responsibility |
| --- | --- |
| `:androidApp` | Android application entry point |
| `iosApp` | Xcode project and SwiftUI entry point |
| `:shared` | App assembly: theme host, navigation, DI aggregation, RBAC composables |
| `:core:designsystem` | Design tokens and reusable UI components |
| `:core:model` | Domain models, roles and permissions |
| `:core:common` | Result/error types and async UI state |
| `:core:data` | Repository interfaces and their implementations |

`:core:designsystem` exposes Compose and Material 3 with `api`, so feature code depends on the
design system rather than on Compose directly.

Feature modules (`:feature:dashboard`, `:feature:tasks`, and so on) are added as each feature is
built, to avoid empty modules slowing down the build.

## Design system

Styling goes through `OrbitTheme`, which wraps `MaterialTheme` and additionally provides the
tokens Material does not model:

- **`semanticColors`** — work status, defect severity, project health (RAG), stock level, AI accent
  and a categorical chart palette. Each is a `ColorPair` of content plus container so contrast
  holds in light and dark themes.
- **`spacing`** and **`sizing`** — a 4dp scale plus role-named values such as `cardPadding` and
  `minTouchTarget`.
- **`shapeTokens`** — component roles (`card`, `button`, `field`, `chip`, `sheet`).
- **`elevation`** — component roles (`card`, `topBar`, `dialog`).
- **`extendedTypography`** — dashboard KPI figures, uppercase section labels, and monospaced
  tabular/reference styles for quantities, currency and invoice numbers.

Read tokens via `OrbitTheme`, not `MaterialTheme`, and avoid literal `dp`, `sp` and `Color` values
in feature code.

### Responsive layout

`OrbitTheme` publishes `LocalWindowSize`. `WindowSize` exposes width and height classes plus
derived decisions: `navigationLayout` (bottom bar, rail or permanent drawer), `supportsTwoPane`
and `dashboardColumns`. This is computed with `BoxWithConstraints` rather than the Material
adaptive artifact, keeping the design system free of platform-specific dependencies.

## Technology

- [Compose Multiplatform](https://jb.gg/compose) for shared UI
- [Compose Navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html)
  with type-safe `@Serializable` routes
- [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
- [Ktor](https://ktor.io/) and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
  for networking and JSON
- [Coil](https://github.com/coil-kt/coil) for image loading
- [supabase-kt](https://github.com/supabase-community/supabase-kt) for the Supabase backend
  (declared in the version catalog, wired up in the backend phase)

## Building

Requires JDK 17+, the Android SDK, and Xcode for iOS.

Create `local.properties` in the repository root pointing at your Android SDK (this file is
machine-specific and git-ignored):

```properties
sdk.dir=/path/to/Android/Sdk
```

Then:

```bash
# Android debug APK
./gradlew :androidApp:assembleDebug

# Unit tests (commonTest, executed on the JVM)
./gradlew testAndroidHostTest
```

iOS is built from `iosApp/iosApp.xcodeproj`. Set `TEAM_ID` in
`iosApp/Configuration/Config.xcconfig` before running on a device.

Kotlin/Native iOS targets only compile on macOS. On Windows and Linux those tasks are skipped, so
`commonTest` is also wired to run on the JVM via `withHostTestBuilder` in each module — that is why
tests are runnable on any host.

## Current status

UI/UX foundation phase. The design system tokens, theming, responsive window sizing, domain roles
and permissions, and the app shell are in place. The data layer is backed by in-memory fakes
(`FakeSessionRepository`) so screens can be built and previewed before authentication and the
backend exist. Reusable components and feature screens are being built next.
