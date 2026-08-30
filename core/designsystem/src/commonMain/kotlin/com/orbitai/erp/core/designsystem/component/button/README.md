# Buttons

The primitive layer. Everything here is theme-driven and knows nothing about construction, ERP, or
any particular screen — that vocabulary lives one layer up, in
`shared/.../ui/component/button/`.

| File | What it is |
| --- | --- |
| `OrbitButton.kt` | The labelled pill. Variants, sizes, states, loading. |
| `OrbitIconButton.kt` | A glyph inside a ring of clear glass, and a touch target around both. |

Icon buttons render their glyph through `icon/OrbitGlyph`, not `Icon`. At 16-20dp the authored stroke
scales down to 1.2dp, under the floor both platforms set; `OrbitGlyph` recomputes it from the rendered
size. Reach for it anywhere an icon is drawn well away from 24dp.
| `OrbitButtonState.kt` | `Active` / `Inactive` / `Disabled`, shared by both. |

## The two layers, and why they are separate

`OrbitButton` takes a `label` and a `variant`. `ActionButton`, in `:shared`, takes an `ActionKind` —
`Approve`, `Reject`, `Cancel`, `Send`, `Login`, `Create`, `Open` — and maps it to a label, a variant
and a busy label.

The split is what stops "Approve" from being spelled and styled five different ways on five screens.
A feature screen should reach for `ActionButton`; it should only reach past it to `OrbitButton` for an
action that genuinely has no preset, and the right response to needing one twice is to add a preset
rather than a second call site.

## Rules that are enforced rather than documented

- **Colour comes from the badge tones**, not from a button palette. `Primary` is `OrbitBadgeTone.Blue`,
  `Destructive` is `Red`. Adding a third tinted variant means adding a tone, not a hex value.
- **Luminosity comes from the rim, never the fill.** The fill sits under the label; brightening it
  forces the label paler. See the comment at the `edgeWidth` in `OrbitButton`.
- **`contentDescription` on `OrbitIconButton` is required, not nullable.** An icon-only control is
  silent to a screen reader without one, and a default value is how that ships unnoticed.
- **Contrast is verified, not eyeballed.** `ControlContrastTest`, `BadgeContrastTest`,
  `OrbitPillGeometryTest` and `OrbitButtonTest` hold the numbers, and the palette itself is generated
  by `tools/gen_designsystem.py`. Changing a colour or an alpha here without regenerating will fail
  the build, which is the intent.

## Adding a component

One file per component, named `Orbit*`. Public API takes a `modifier` as its first optional parameter
and never a `Color`: if a caller needs a colour, the component is missing a variant.
