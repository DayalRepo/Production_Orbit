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
- **`contentColors`** — text, icon and avatar-edge colours with measured WCAG contrast, per platform
  and per theme.
- **`controlColors`** — the monochrome palette icon buttons, chips and field chrome are built from,
  per theme. Tinted buttons take their colours from `badgeColors` instead.
- **`spacing`** and **`sizing`** — a 4dp spacing scale, plus sizes including the platform-specific
  icon, avatar and `minTouchTarget` values.
- **`shapeTokens`** — component roles (`card`, `button`, `field`, `chip`, `sheet`).
- **`elevation`** — component roles (`card`, `topBar`, `dialog`).
- **`extendedTypography`** — dashboard KPI figures, uppercase section labels, tabular numeric and
  reference styles, and a long-form body style at 1.5x line height.
- **`typeScale`**, **`platform`**, **`topBarTitleAlignment`** — the resolved platform tokens.

Read tokens via `OrbitTheme`, not `MaterialTheme`, and avoid literal `dp`, `sp` and `Color` values
in feature code.

### Platform divergence

Android and iOS genuinely differ on type scale, text colour, icon and avatar sizing, minimum touch
target and top-bar title alignment. Every one of those differences lives in `PlatformTokens.kt`;
adding a platform check anywhere else is a smell.

The seam is deliberately one `expect val` in `foundation/OrbitPlatform.kt` returning an
`OrbitPlatform` enum. All token data sits in `commonMain` keyed off that value, so both platforms'
tokens are verified by ordinary host tests — including on Windows and Linux, where the Apple targets
cannot be compiled at all.

| | Android | iOS |
| --- | --- | --- |
| Base font size | 16sp | 17pt |
| H1 | 32/40 | 34/41 |
| Body | 16/24 | 17/22 |
| `minTouchTarget` | 48dp | 44pt |
| Toolbar icon | 24dp | 20–24pt |
| `avatarSm` / `avatarXl` | 40dp / 88dp | 32pt / 80pt |
| Primary text (light / dark) | `#1A1C1E` / `#E3E2E6` | `#1C1C1E` / `#EBEBF5` |
| Top bar title | Left-aligned | Centred |

### Typeface

Google Sans Flex, bundled in `core/designsystem/src/commonMain/composeResources/font/` as all nine
static instances (100–900, roughly 1.1 MB). Static files rather than the six-axis variable font
because variable-axis selection requires Android API 26 and `minSdk` is 24 — on Android 7 every
weight would collapse to Regular. Licensed under the SIL Open Font License 1.1; see
`core/designsystem/licenses/OFL-Google-Sans-Flex.txt`.

All nine ship so weight decisions need no asset work. Weight assignments are collected in
`OrbitFontWeights`, so changing boldness is one edit rather than forty. If the asset size matters
more than the flexibility, dropping Thin, ExtraLight, ExtraBold and Black reclaims about 500 KB.

Because resource-backed fonts can only be loaded from a composition, the type scale is built by
`orbitTypography(sans, scale)` rather than being a top-level `val`. No call site names a font family
or a size, so both are changeable in one place.

Numeric styles stay in Google Sans Flex and request its `tnum` feature rather than switching to a
monospace face that would match nothing else on screen.

### Colour and contrast

Text and icon tokens are pre-composited flat colours with measured contrast ratios, not a base
colour plus an alpha — alpha is only predictable over a known background. `ContrastTest` asserts all
four platform/theme sets and fails the build on regression.

Neither theme uses a pure value: `#000000` on `#FFFFFF` measures a perfect 21:1 and still causes
visual vibration, and `#FFFFFF` on `#000000` blooms during long reading. The light surface is
`#FFFFFF` with deep-charcoal text; the dark surface is `#121212` with soft off-white text.

Two thresholds from the brief had to be tightened after measurement:

- Secondary text tiers are 70% and 65% of primary, not 60%. At 60%, `textTertiary` composites to
  `#767778` on white and measures **4.49:1** — one hundredth below the 4.5:1 requirement.
- Inactive icons are 60%, not 38–50%. That range is safe on dark surfaces but 50% of the iOS
  `#3A3A3C` measures 2.74:1, below the 3:1 non-text floor of WCAG 1.4.11.

### Accessibility

- All text sizes are `sp`. Compose Multiplatform maps `UIContentSizeCategory` onto `fontScale`
  itself, so iOS Dynamic Type works with no bridging code — but that mapping **caps at 1.8x** for
  AX5 where native iOS reaches roughly 3.1x. Designing to survive Android's 200% covers both;
  anything needing true AX5 fidelity has to be a native screen.
- No component sets a fixed height on anything containing text. Use `heightIn(min = ...)`, never
  `height(...)`, so text can grow to 200% without clipping (WCAG 1.4.4).
- Caption is 12sp/12pt on both platforms and is the hard floor.
- `bodyLongForm` carries 1.5x line height for multi-paragraph reading copy (WCAG 1.4.12).
- Meaning is never carried by hue alone: severity uses filled-segment count as well as colour, and
  health dots require a label even when it is visually hidden.

### Component library

Reusable components live in `core/designsystem/.../component/`, one file per component, grouped by
role — `surface/`, `button/`, `input/`, `status/`, `display/`, `feedback/`, `navigation/`,
`overlay/`. Rules:

- Prefix public composables with `Orbit`, so a raw Material import stands out in review.
- Accept `modifier: Modifier = Modifier` after required parameters; never set outer padding.
- Take no domain types. A badge accepts an `OrbitBadgeTone` and a label, not a `WorkStatus`; the
  enum-to-token mapping belongs in feature code.
- Stay stateless, hoisting state to the caller, and preview through `preview/OrbitPreview.kt`.
- Never set a fixed height on a container holding text, and read `sizing.minTouchTarget` rather than
  assuming 48dp — iOS uses 44pt.

Built so far:

- `badge/` — `OrbitBadge` (three emphases, three sizes, glass fill)
- `button/` — `OrbitButton` (five variants, three sizes, three states, loading state),
  `OrbitIconButton` (four styles, three sizes, three states)
- `input/` — `OrbitTextField` (single or multi-line, glass container, placeholder, supporting and
  error text, scroll-and-expand past its line cap), `OrbitSearchField`, `OrbitMessageField`, over a
  shared internal `OrbitFieldShell`
- `status/` — `OrbitChip` (label and count, no glyph), `OrbitHealthDot`, `OrbitSeverityIndicator`,
  `OrbitProgressBar`
- `feedback/` — `OrbitLoadingIcon`
- `foundation/` — `Modifier.orbitGlass`, `Modifier.orbitGlassScrollbar`, `orbitPressIndication`,
  `Modifier.orbitHandCursor`

`OrbitIconButton` takes `contentDescription` as a required parameter rather than a nullable one: an
icon-only control is invisible to TalkBack and VoiceOver without it, and mandatory is the only
reliable way to stop that shipping.

#### Two tones, borrowed from the badges

Buttons are tonal chips in one of two hues: a light blue for the action the screen wants — Approve,
Send, Create, Login, Open — and a light red for the one that backs out of it, Cancel and Reject.
Both come straight out of `OrbitBadgeTone`, the same palette the status badges use.

This reverses an earlier monochrome scheme, and the reason the reversal works is that the *shape*
took over the job colour used to do. A pill with a centred verb in it is unmistakably a control, so
tinting it can no longer be confused with a status badge sitting on a card, and the two colour
languages that the monochrome argument was avoiding turn out not to collide. Reusing the badge tones
rather than inventing a button palette also means the colours arrive already contrast-verified
against the glass gradient in both themes.

Blue rather than green for the affirmative. Green-on-red is the one pairing that vanishes for the
~8% of men with red-green colour blindness, and approve-versus-reject is precisely the decision that
must not rest on hue. Blue against red survives every common form of it, and the labels differ in any
case.

Destructive is filled now rather than an outlined red label. An outlined red reads as a warning
*about* the button; a red chip reads as the negative option, which is what Cancel and Reject are. It
stays a light tint rather than a saturated red because backing out of a form is an ordinary thing to
do, and a solid red button makes every cancellation feel like a deletion. Cancel and Reject share
that tint deliberately: they are never offered together — Cancel pairs with Send or Create, Reject
with Approve — so nothing is made ambiguous, and giving them different treatments made the same
gesture look like two different kinds of act.

Where the luminosity comes from is the rim, not the fill, and that is a constraint rather than a
preference. Tinted buttons draw a hairline edge in the tone's border shade over a deep translucent
interior — which is what glass actually looks like, a dark body under a lit edge. The obvious
alternative, lightening the fill until it reads as "light blue" in the dark theme, was tried and
reverted: the label sits on that fill, so brightening it forces every label paler, and at the
brightest value the palette would still tolerate, dark Red (#F3B1AF) and dark Green (#C5F1D8) had
converged far enough toward white to stop being separable from each other. A badge's hue is a signal;
washing it out to make a container prettier trades the message for the envelope. The rim carries no
text, so it can be as bright as the tone allows for nothing.

A hairline rather than anything heavier, because a pill has a long edge. At 2dp the same stroke traces
enough of the perimeter to stop reading as a lit edge and start reading as an outline drawn around the
button — which is the thing the Outline variant is for.

Icon buttons are a small glyph inside a **ring of glass**. The fill is achromatic and translucent —
white on light, grey on dark — so whatever is behind still tints through, which is what lets the same
ring drop onto a card, a photo or a coloured header without being retuned for each. The fill is a
neutral rather than a tint of the glyph's hue because the colour here is doing semantic work, and a
blue ring around a blue glyph doubles the signal while halving the contrast between them.

On a white page a white ring is nearly invisible, and that is expected rather than a defect. What
separates it there is a hairline rim, a highlight along the top edge and a 3dp contact shadow — and
the shadow is the only one of the three that reads as depth rather than as another line; without it
the highlight tends to be read as a gradient in the background instead of as light catching an edge.
The fill starts doing visible work the moment the control sits on something other than the page, which
is where icon buttons actually live. The alternative, a grey fill on light, makes every toolbar look
disabled.

The rim is a hairline rather than the 2dp it was before the fill existed. Once the fill defines the
circle, the rim's only remaining job is catching the light along the top edge, and at 2dp it was
drawing a ring around a ring.

`OrbitIconButtonStyle` has four entries, each borrowing the badge tone of the same meaning so that an
icon action, a labelled button and a status pill that mean the same thing are the same colour
app-wide: `Accent` blue for most actions, `Positive` green where the outcome rather than the
navigation is the point, `Destructive` red for anything that destroys or refuses, `Neutral`
monochrome for chrome. Destructive is the one worth being strict about — a delete that looks like
every other action is a delete someone taps by accident, and on an icon-only control there is no
label to catch them. Colour is never the only signal: the glyph carries the meaning and the
`contentDescription` carries it for screen readers.

Rings are 32/38/44dp around 16/18/20dp glyphs, so the glyph is roughly half its ring. The clear space
is the point; a glyph crowding its ring reads as a mistake in the padding rather than as an icon under
a lens. The rings are allowed to fall below the touch target because the ring was never the target —
the hit area is `max(ring, sizing.minTouchTarget)`.

Inactive fades the ring and leaves the glyph alone. The ring carries no information, so dimming it is
free; the glyph is the entire content, and fading both composites two dimmed layers.

#### Shadows, and why glass needs one

`Modifier.orbitGlassShadow` puts a contact shadow under every glass surface — badges, chips, filled
buttons, icon buttons — and it is not decoration. The three layers `orbitGlass` draws are all
*material* cues: they describe what the pane is made of. None of them is a *position* cue, and a
translucent pane with a lit top edge is the same pixels as a pane painted flat onto the page until
something says one of them is in front. On a mid-tone or busy background the highlight is quite
readily read as a gradient in the background itself. A shadow is the only cheap cue that reads as
depth rather than as another line, and it is what makes the rest of the stack land as glass.

Depths are ordered by how much each component's own fill already does: 1dp for a badge or chip, 2dp
for a button, 3dp for an icon button, whose ring is the faintest fill in the system and leans on its
shadow hardest. All three are contact shadows — the millimetre between a pane of glass and the paper
under it — rather than the lift of a floating action button. The shadow scales with the component's
alpha, so a disabled control settles onto the page as its other layers fade instead of hovering over
it at full depth. Variants with no pane are skipped: an outlined badge or a Text button given a
shadow reads as a rendering bug, or as a hole in the card.

The two themes are different problems. On light, the surface is brighter than any plausible shadow, so
a soft black at 16% separates the pane and anything stronger looks like a 2007 web page. On dark the
alpha roughly triples, to 48%, because the surfaces start at `#121212` rather than pure black — a house
rule rather than an accident — which leaves headroom underneath but not much of it. What must not
happen on dark is reaching for a *lighter* shadow to compensate: a pale halo puts light under the
object while the highlight puts light on top, and two contradictory light directions is exactly what
makes a glass effect read as a sticker. Depth on dark comes from going darker, or not at all.

That same trap is what the dark icon ring highlight is now set to 0.05 for. The ring's fill is a pale
grey, so a white highlight over it lightens the fill rather than reading as light glancing off an edge,
and the result was a milky bloom across the top of every circle. On light the layer is harmless because
white over white is invisible. On dark the glass read is carried by the rim and the shadow instead.

#### Why icon glyphs are held to 3:1 and not 4.5:1

The glyph shade is chosen per style *and* per theme, which is why it is a pair of selectors on the
enum rather than one field read off the tone. The rule is "the lightest shade that still clears the
bar", and light blue and light red are what an icon button wants — the deep `label` shades are tuned
for small text and on a glyph they read as ink rather than as light. But the lightest shade that
clears is not the same field everywhere: `border` is it for Blue in both themes and for Red on light;
Green's border shade reaches only 2.9:1 against a white ring, because a saturated green is simply a
bright colour, so Positive takes the next shade down; and Red's border shade fails on dark at 2.91:1
over a grey ring, so dark Destructive takes `solidContainer`, which on an inverted palette is the
*brighter* of the two.

The bar is 3:1 because a glyph is a graphical object, so WCAG 1.4.11 governs it — 1.4.3's 4.5:1
governs text. Holding an icon to the text minimum is not caution but a category error, and it is what
forced the near-navy blue and near-maroon red these shades replace. `ControlContrastTest` checks every
shade against the ring fill composited over each surface, since that is the background the glyph
actually has; testing against the bare surface would flatter the light theme, where the white fill
makes the glyph's job harder rather than easier.

#### Icon stroke and rendered size

The Hugeicons set expresses its stroke in viewport units, so the stroke scales with the glyph: the
same vector draws 1.8dp at 24dp and 1.2dp at 16dp. That is fine while every icon in the app is roughly
one size, and stops being fine the moment one glyph appears at both — the two look like they came from
different libraries — and 1.2dp is under the floor both platforms set for a stroked icon.

`OrbitGlyph` fixes it at the point of use rather than in the generator, because there is no
generation-time answer: a heavier baked stroke would fix 16dp and ruin 32dp. It walks the vector,
redraws every stroked path at a width computed from the rendered size, and memoises the result against
the icon and the stroke. The rule is the same on both platforms, since Material's 1.5–2dp band and SF
Symbols' regular weight agree: take the weight the vector would naturally scale to and clamp it into
the band. At icon-button sizes the natural weight is 1.2 to 1.5dp, so the floor does all the work and
the three sizes come out matching. Filled paths are copied untouched — a solid the designer drew as a
solid should not acquire an outline. The glyph draws into a box a tenth larger than it reports to its
parent, because a corrected stroke is wider than the author's and Compose clips an ImageVector to its
viewport.

#### Geometry and states

Buttons are full pills, like badges and chips, and text-only. A glyph beside a two-word label
competes with it rather than helping — nobody needs a picture to understand "Approve" — and dropping
it lets the label centre in the pill, which is what makes a decision pair read as a matched set.

Buttons size to their labels. A row of them should look like a row of words, not a row of identical
slabs, so `ActionButtonRow` fixes the height of a decision pair through a shared `size` and leaves
each half to take its own width — stretching the two to match turns "Cancel" and "Create" into two
interchangeable blocks distinguishable only by the word inside, and scales the pair with the
container rather than with the words, so on a tablet a two-word decision spans the screen.

The minimum widths are therefore a floor rather than a target: 72/88/104dp against heights of
32/40/48dp, with end padding of 18/22/28dp. They exist only because a pill's radius is half its
height, so a two-character label on an unconstrained pill comes out as a circle.
`OrbitPillGeometryTest` bounds them from *both* sides — wide enough to leave room between the curves,
narrow enough that an ordinary verb clears them, because a floor that an ordinary verb hits is a
fixed width wearing a floor's name.

Small takes the same type size as Medium and a heavier weight — SemiBold against Medium. It is
already giving up height and padding; taking the type down as well left a label that was legible in
isolation and mushy inside a tinted chip, where the fill cuts the effective contrast of every stroke.
Small keeps its identity through its 32dp height, not through smaller text, and its end padding is
18dp rather than 16dp so the shortest labels are not squeezed into the curve.

Tracking runs the opposite way to weight, and both correct for the same thing. The house default is
0, set deliberately for body text; a button label is not body text. It is two or three words with no
line to sit on, so the eye takes the whole shape at once, and the shape of a short bold word is a blob
unless the letters get air. So +0.4sp at Small, where the label is heaviest and most cramped, +0.15sp
at Medium, and −0.1sp at Large, where 18sp letters have room already and default tracking starts to
look like a gap between every pair.

Visible button heights are 32/40/48dp and icon-button glyphs 20/24/28dp, the heights being a
*minimum* rather than a fixed size, with the hit area expanded to `sizing.minTouchTarget` — 48dp on
Android, 44pt on iOS. That expansion is why a 32dp button and a 20dp glyph are allowed to exist at
all. Both components draw it as two nodes, an outer touch target owning the click and an inner
visible shape owning the paint and the press animation, because a modifier that only grows the
reported layout size leaves the extra area outside the clickable's pointer bounds: a control that
looks compliant and is not.

`OrbitButtonState` replaces an `enabled` boolean for both components, because Inactive and Disabled
are different claims — "not the point right now" versus "does not work" — and only one of them is
exempt from the contrast minimum. See [Contrast](#icons-and-generated-colour) for what that
costs.

`OrbitLoadingIcon` is the house spinner — the Hugeicons `loading-03` ring, rotating linearly at one
revolution per 900ms — used in place of `CircularProgressIndicator` so a button in flight still
looks like it came from this design system. The glyph's graduated dashes are what make the rotation
legible; a closed circle would spin invisibly.

### Glass

`Modifier.orbitGlass` draws the treatment shared by badges and buttons, in three layers: a vertical
gradient fill whose alpha is raised along the top edge, a white specular highlight strongest at the
top and fading out, and a hairline rim that fades downward so the pill reads as having thickness.

There is no backdrop blur. Compose Multiplatform has no cross-platform way to sample what is behind
a composable, so a real frosted pane would mean a `UIVisualEffectView` on iOS plus an API 31
`RenderEffect` on Android — two platform implementations for an effect nobody can resolve on a 28dp
pill. Translucency plus a highlight gets the same read.

The alphas are not free parameters. They live in `OrbitGlass` in the generated `theme/BadgeColors.kt`
because every label and icon shade was tuned against this exact stack; see
[Icons and generated colour](#icons-and-generated-colour). Buttons use a weaker highlight than
badges, since an opaque fill washes out faster than a translucent tint.

### Press feedback

Android and iOS disagree about what a press looks like, and picking one loses either way: Material's
ripple spreading from the contact point is the Android idiom and its absence reads as an
unresponsive control, while on iOS a ripple immediately marks the app as a port. So
`orbitPressIndication()` returns a ripple on Android and a UIKit-style shrink on iOS, implemented as
an `IndicationNodeFactory` so the animation is a draw-time transform rather than a layout change —
a press must not shove its neighbours around.

`Modifier.orbitHandCursor` adds a hand cursor, and buttons lift their glass highlight on hover
rather than washing a tint over themselves, so pointer feedback looks like the light moving. Both
are inert on a touch screen and earn their keep on a trackpad-equipped tablet, a ChromeOS window,
an Android device in desktop mode, and a desktop target if one is added. There is no desktop target
in the build yet.

### App-level components

The domain-aware layer lives in `shared/.../ui/component/`, and the split from
`core/designsystem/.../component/` is the point rather than an accident of packaging. The design
system owns geometry, colour and accessibility; `:shared` owns vocabulary. So `OrbitBadge` takes an
`OrbitBadgeTone` and an `ImageVector`, while `ui/component/badge/` supplies `StatusBadge`,
`SeverityBadge`, `WorkStatusBadge`, `ApprovalStatusBadge`, `StockLevelBadge` and
`ProjectHealthBadge` — each two lines, each making it impossible for a screen to pick the wrong
tone or glyph for a state. `ui/component/badge/BadgeCatalog.kt` is the closed list of the 24 badge
kinds and the four severity levels.

`ui/component/button/` does the same job for actions. `ActionKind` pins each recurring ERP verb to
one variant and one wording — Approve, Reject, Cancel, Send, Login, Create, Open — because "Approve"
appears on a purchase order, a timesheet, an RFI, a material request and a change order, and if each
screen picks its own emphasis then approving something looks like five different operations.

Two pairings are load-bearing rather than cosmetic, and `ActionButtonTest` holds them:

- **Backing out is never the loud half.** Cancel is asserted not to be Primary. Styling an escape
  hatch as loudly as the action it escapes is how people tap the wrong one.
- **Cancel and Reject share a treatment, and that is asserted rather than merely permitted.** Both
  are red. They are never offered together, so nothing is made ambiguous, and the previous split —
  Reject red, Cancel neutral — made the same gesture look like two different kinds of act.
- **The committing half of every decision pair outranks its dismiss.** Asserted for all three pairs
  the app actually ships, so a screen cannot end up offering two identical-looking exits.

`ActionButtonRow` lays a pair out at one height and two widths. The shared height is the component —
a decision should not have one half taller than the other — while the widths come from the labels,
because two equal-width chips are distinguishable only by the word inside them and force the eye to
read before it can choose. Emphasis is carried by the tone, where it can be read deliberately rather
than inferred from text metrics. The dismiss goes first, matching both platforms and letting a
left-to-right scan meet the safe option before the committing one.

`BusyKind` and `BusyButton` cover the three waits — Sending, Loading, Verifying. These are states
rather than actions, so they are separate from `ActionKind` and take no `onClick`: the point of the
state is that the work has been fired and must not be fired again. The wording is the only thing the
user has to go on while they wait, and "Loading" where "Verifying" was meant is genuinely
misleading — one says the app is fetching, the other says it is checking something just submitted.
Each `ActionKind` also carries a `busyLabel`, so a preset in flight says "Sending" rather than
freezing.

#### Input fields

`OrbitTextField`, `OrbitSearchField` and `OrbitMessageField` share one internal `OrbitFieldShell`,
because a field's chrome is the part users read as "this is where I type" — if a search box and a
message box reach their tint by different routes they drift apart the first time either is adjusted,
and the drift is invisible in review because the two are never on screen together. Focus is carried
by the rim, at 2dp and a stronger tint, rather than by a fill change, since brightening the fill
would shift the background under text the user is actively reading.

Three decisions in there were driven by bugs rather than taste:

- **The shell owns the tap.** A `BasicTextField` is only as tall as the text currently in it, so an
  empty multi-line field is a one-line hit target inside a five-line box, and everything below the
  first line — including the whole placeholder area — is dead to a tap. The container focuses the
  field through a `FocusRequester` instead.
- **`verticalScroll` goes on a wrapper, never on the text field.** Applied to a `BasicTextField` it
  hands the field an unbounded height constraint and the field measures out of the layout: it still
  draws, but it takes no focus and never reaches the accessibility tree. The field looks completely
  normal and cannot be typed into by anyone, pointer or screen reader.
- **`Modifier.orbitGlassScrollbar` goes *before* `verticalScroll`.** Chained after, the draw lands
  inside the scroll container, so the thumb is measured against the content instead of the viewport
  and scrolls away with the text.

That scrollbar exists because neither platform draws one here — Android shows nothing inside a
`BasicTextField`, iOS shows its transient indicator only on its own scroll views — so a field that
has scrolled looks identical to one that has not, and the user gets no signal that their own text is
hidden above the fold. Past its line cap the field also offers an expand button, bottom-aligned so
it sits beside the last line (usually the short one) rather than stealing width from every line
below the first. Both appear only when there is genuine overflow, tested against the scroll range
rather than by counting lines, which cannot account for wrapping or font scale.

The composer's shape reports its state: a full pill when empty, corners tightening to 20dp once
there is text, relaxing back when it is cleared. A pill is the shape of a prompt and a rectangle the
shape of a container holding content, and the composer is genuinely both at different moments. It is
also practical, since a pill's radius is half its height and the curve starts eating the top and
bottom lines once the field grows. Attachment sits left, microphone and send right, all three present
at all times so the row never reflows under the user's hand mid-sentence.

### Icons and generated colour

`core/designsystem/icon/OrbitIcons.kt` holds the icon set, taken from the Hugeicons free
stroke-rounded collection (MIT — see `licenses/MIT-Hugeicons.txt`). Every glyph is authored on a
24×24 viewport with a 1.8-unit stroke and round caps, which is where the uniform stroke weight comes
from. Because the stroke is in viewport units it scales with the glyph, and that scaling is why the
figure is 1.8 rather than the 1.5 the design rules name: almost nothing draws at 24dp. A badge glyph
is 14–18dp and a small button glyph is 16dp, so at 1.5 units those rendered at 0.9–1.1dp and looked
hairline beside their labels. At 1.8 the same glyphs land at 1.05–1.35dp and a 24dp toolbar icon at
1.8dp, which puts most of the set near the 1.5–2dp band at the sizes actually used.

That is a better default and not a fix, which is what `OrbitGlyph` is for — see above. Anywhere a glyph
is drawn well away from 24dp, render it through `OrbitGlyph` and the stroke is recomputed from the
rendered size instead of inherited from the viewport. Icon buttons do; badges, whose glyphs sit beside
a label at a fixed ratio and read as a set, keep the authored scaling. No button or icon-button glyph is
allowed below 16dp regardless, and `OrbitButtonTest` enforces it.

Both that file and `theme/BadgeColors.kt` are generated by `tools/gen_designsystem.py`. The palette
is generated rather than hand-picked because the contrast target is what actually matters: the script
searches lightness *and* saturation per tone until the label and icon clear the minimum, so the hex
values are a consequence of the requirement instead of an approximation of it.

Because the fill is translucent, contrast is measured against the whole glass stack composited over
the *most elevated* surface in each theme (`#E8E8E8` light, `#2A2A2A` dark) — the least favourable
background a badge can land on — and **sampled down the pill's height** rather than at its edges.
Going up a pill the tint gets stronger and the white highlight gets stronger, and those pull
luminance in opposite directions, so the worst point can sit in the middle. This is why the glass
alphas and the palette have to be regenerated together, and why `Modifier.orbitGlass` must keep
using linear two-stop gradients.

Three findings from that process are worth recording, because they constrain the design rather than
merely describing it:

- **The dark fill is far darker than the light one, not its mirror.** At the mirrored lightness the
  fill plus sheen reached a luminance where even pure white text fell to 4.2:1 — no label colour
  existed. Backing off partway made the search succeed but only by bleaching the labels to
  near-white. A deep fill with a vivid label both passes and matches what dark-theme glass actually
  looks like.
- **Saturation has to fall as lightness rises.** A fully saturated cyan or green has a luminance
  ceiling well below white, so lightness alone ran out of room. Desaturating as the colour brightens
  is what every real palette does, and it is what "bright cyan" looks like anyway.
- **The hovered button, not the resting one, is the worst case**, since hover brightens the
  highlight. At a hover lift of 1.6× the light-theme Destructive fell to 4.35:1, which is what fixed
  `OrbitGlass.ButtonHoverLift` at its current value.

`BadgeContrastTest` samples four cases rather than two: each theme at rest and at the hovered
highlight. Badges are never hovered, but buttons now wear these tones and a hovered button lifts its
highlight past anything a badge reaches, so the generator tunes against both levels and the test holds
both. That coupling is the price of one shared palette, and it is cheaper than two.

The monochrome controls are verified the same way by `ControlContrastTest`, and two of its results
shaped the components rather than merely confirming them:

- **`OrbitAlpha.Inactive` is 0.65, not the conventional 0.60.** Inactive content is still tappable,
  so it is live UI and owes the full 4.5:1 for text rather than the 3:1 non-text floor. The binding
  case is the deep-charcoal control content on a white surface, which lands at 4.47:1 at 0.60 —
  failing by a hair — and 5.3:1 at 0.65.
- **An inverted fill cannot be faded at all, so an inactive Filled icon button is drawn tonal.** Its
  glyph is near-white, so fading the fill moves it toward the surface and drags the glyph down with
  it: about 3.8:1 at the top of the highlight, and about 2.6:1 if the glyph is faded too. No fade
  value both reads as stepped back and stays legible. Stepping the *treatment* down instead is safe
  by construction, since a tonal container carries dark content. Buttons escaped this problem when
  they became tinted chips — thinning a translucent tone moves its background toward the surface, and
  every tone's label clears the minimum against the surface too, so contrast rises as it fades.
  Fading otherwise follows one rule: a filled control fades its fill, a ringed one its ring, a bare
  one its content, never two at once.

`BadgeContrastTest` and `ControlContrastTest` re-derive all of this from the committed files, so the
generator proves the numbers were right when produced and the tests prove they are still right in
the file — which is the check that catches someone warming up an amber by hand.

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
