# Input fields

> **Currently shelved.** These are built, tested and compiling, but no gallery page shows them and no
> screen uses them yet. They were taken out of the review surface deliberately so buttons could be
> settled first. Nothing here has been deleted — put `InputGalleryPage` back and they return.

| File | What it is |
| --- | --- |
| `OrbitFieldShell.kt` | `internal`. The chrome every field shares: glass, rim, focus, tap-to-focus. |
| `OrbitTextField.kt` | Single or multi-line, placeholder, supporting and error text, scroll and expand. |
| `OrbitSearchField.kt` | A pill with a search glyph and a clear button. |
| `OrbitMessageField.kt` | The composer: attach, mic, send, and a shape that morphs as you type. |

## Why there is a shell

Three fields, one set of chrome. `OrbitFieldShell` owns the glass fill, the rim and its focus and
error states, and the tap target that puts the cursor in the field. The three public fields own only
what actually differs: their glyphs, their keyboard options, and their shape.

Without it the focus rim was implemented three times and had drifted by the second one.

## Two traps, both already sprung

- **`Modifier.orbitGlassScrollbar` goes *before* `verticalScroll`.** Chained after, the draw lands
  inside the scroll container, so the thumb is measured against the content rather than the viewport
  and scrolls away with the text.
- **`verticalScroll` belongs on a wrapper `Box`, not on the `BasicTextField`.** Applied directly, the
  field measures itself out of the layout and disappears from the accessibility tree — it renders,
  and TalkBack cannot find it. This cost an afternoon; the wrapper is not stylistic.

## Rules

- Fields take a `label` even when they do not display one, because it is the accessible name and the
  string used in the clear and expand descriptions.
- The rim thickens from `hairline` to `borderStrong` on focus. It must not be carried by tint alone:
  on a touch screen the rim is the only thing saying which field the keyboard will type into.
- `OrbitFieldTest` and `OrbitComposerShapeTest` hold the geometry and the shape-morph constants.
