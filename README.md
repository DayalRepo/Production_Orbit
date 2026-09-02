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
- Meaning is never carried by hue alone: a severity badge carries its level as text as well as
  colour, and an icon button's glyph carries its action independently of its tint.

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
- `button/` — `OrbitButton` (five variants, three sizes, two states, loading state),
  `OrbitIconButton` (four styles, three sizes, two states)
- `input/` — `OrbitTextField`, `OrbitSearchField`, over a shared `OrbitFieldShell`,
  `OrbitMessageField` with `OrbitComposerAttachmentStrip` for queued file thumbnails
- `status/` — `OrbitChip` (label and count, no glyph)
- `display/` — `OrbitAvatar` (five tiers, photo or initials), `OrbitAvatarGroup` (overlapping stack
  with an overflow count), `OrbitDelta` (signed change chip), `OrbitCountBadge` and
  `OrbitPresenceDot`, `OrbitAttachmentRow`, `OrbitMessageBubble` (user / AI / other, expand, copy,
  reply)
- `progress/` — `OrbitSegmentedProgress`, `OrbitStepIndicator` (vertical collapsible workflow track
  with numbered square stages)
- `container/` — `OrbitCard`, `OrbitDivider` and `OrbitVerticalDivider`
- `feedback/` — `OrbitLoadingIcon`, `OrbitSkeleton` / `OrbitSkeletonLine` / `OrbitSkeletonList`
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
white on light, **black on dark** — so whatever is behind still tints through, which is what lets the
same ring drop onto a card, a photo or a coloured header without being retuned for each. The fill is a
neutral, and so is the rim, because the glyph is already carrying the semantic colour. Repeating that
hue on the container gives the same signal twice and puts the two nearest objects in competition;
keeping the container achromatic leaves the glyph as the only coloured thing in the component, which is
what makes a row of these scannable by colour.

On a white page a white ring is nearly invisible, and that is expected rather than a defect. What
separates it there is a hairline rim, a highlight along the top edge and a 3dp contact shadow — and
the shadow is the only one of the three that reads as depth rather than as another line; without it
the highlight tends to be read as a gradient in the background instead of as light catching an edge.
The fill starts doing visible work the moment the control sits on something other than the page, which
is where icon buttons actually live. The alternative, a grey fill on light, makes every toolbar look
disabled.

The rim is drawn at `sizing.hairline`, 1dp, the same as every other enclosing edge in the system —
see [One border](#one-border) below. It was briefly 0.75dp on the theory that a fill and a shadow had
already defined the circle and the rim only had to describe an edge; that is true, but sub-dp widths
land on a pixel boundary at 2x and between two at 3x, so the same rim looked crisp on one phone and
soft on the next. That is a rasterisation outcome pretending to be a design decision.

`Dp.Hairline` was tried there too and reverted. It is one *physical pixel*, which sounds like the
thinnest possible line and is, but it makes the rim's apparent weight track screen density instead of
the design — a third of a dp at 3x, and no thinner than a full hairline at 1x. A real dp value scales
like every other token and looks the same on every device.

### One border

Every enclosing edge in the product — card, tile, divider, tonal control ring, attachment row — draws
`controlColors.controlBorder` at 1dp. One colour, one width, both platforms, both themes.

The uniformity is the point. A card rim, a divider and a control ring are all answering the same
question, "where does this thing stop", and when each picks its own alpha the screen ends up with four
weights of grey line that read as four levels of hierarchy nobody designed.

The two themes are **not** symmetrical, though, and that part is deliberate:

| Theme | Value        | Kind        |
| ----- | ------------ | ----------- |
| Light | `#E8E8E8`    | Opaque      |
| Dark  | `#FFFFFF33`  | White at 20% |

Light is opaque so it renders identically on the page, on a card and on an elevated sheet — which is
what makes a column of cards look like a column rather than a gradient. Dark is translucent because
the surfaces underneath an edge vary far more there (near-black page, `#1A1A1A` card, `#2A2A2A`
sheet), and an opaque light grey that reads correctly on the page turns into a glowing outline on the
sheet. Letting it composite keeps the edge proportional to whatever it lands on.

One test had to change with this. `ControlContrastTest` asserted that an Outline control's ring is
stronger than a tonal control's rim, and it measured that by comparing **alpha** — a fine proxy while
both were the same ink at different opacities, and meaningless the moment light's border went opaque.
A solid `#E8E8E8` has alpha 1.0 and is far weaker on a white page than a dark ink at 28%. It now
compares contrast against the surface, which is the thing the assertion was always about.

Tinting that rim to match the glyph was tried and reverted. It does tie the glyph to its container, but
it costs more than it buys: the glyph's colour is doing semantic work, and repeating the hue on the ring
immediately around it weakens the thing that lets a toolbar be scanned by colour.

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

Disabled fades the ring and the glyph together, which is only safe because WCAG 1.4.3 exempts
inactive controls from the contrast minimum outright. The removed Inactive state could not: it was
still tappable, so it still owed 4.5:1, and dimming both layers composited two faded surfaces and
landed well under it — which is why it had to fade the ring alone.

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

That same trap is what the dark icon ring is now **black-filled with no highlight at all** for, and it
took three passes to accept that the amount was never the problem.

The ring's dark fill used to be a pale grey with a small white highlight over it, on the reasoning that
glass is a lightening material. Measured on device that produced a `#383839` disc on a `#121212` page:
not a hint of material but a solid light-grey coin, brighter than anything near it, pulling the eye to
the container rather than to the glyph it exists to hold. Thinning the highlight twice did not fix it,
because the direction was wrong rather than the quantity.

Glass lightens on light because what you are looking through it at is a bright page. On a near-black
page the honest analogue is the opposite: the ring reads as a slight well the glyph sits in. So the
dark fill is now black-based and the dark highlight is exactly zero — any nonzero value produced a
visible film, and there was no setting that was subtle rather than simply absent. The circle is carried
by its rim, already tinted to match the glyph, and by the contact shadow.

The constant this used to share with cards and attachment rows was split for the same reason. A ring is
a 32dp disc whose fill is most of its area, so a wash across it recolours the whole object; a card is a
wide panel where the same wash falls as a gradient along a long top edge and reads correctly as an edge
catching light. `RingHighlight*` and `SurfaceHighlight*` look like the same number and are not the same
problem, and keeping them as one meant fixing the disc would have silently flattened every card.

Input fields are the other place the white layer had to go, and there the fix was to opt out of
`orbitGlass` entirely — see the input field section below.

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
the band.

The floor is a parameter rather than a constant, because the right floor depends on what the glyph is
standing next to. `sizing.iconStrokeWidth` at 1.5dp is the weight an icon needs beside a label, where it
has to hold its own against type. `sizing.iconStrokeLight` at 1.25dp is what a glyph alone inside an
icon button uses: there is no type beside it and a ring is already pointing at it, so the heavier floor
reads as heavy rather than legible and begins closing the counters of a small glyph.

Filled paths are copied untouched — a solid the designer drew as a
solid should not acquire an outline. The glyph draws into a box a tenth larger than it reports to its
parent, because a corrected stroke is wider than the author's and Compose clips an ImageVector to its
viewport.

#### Geometry and states

Buttons are full pills, like badges and chips, and every one carries a glyph.

They were text-only for a while, on the reasoning that nobody needs a picture to understand
"Approve" and a glyph beside a two-word label competes with it. That is true of the glyph as
*identification* and misses what it does here: a tinted chip with a mark in it is a recognisable
object, a tinted chip with only a word in it is a coloured rectangle, and a form full of them reads
as a form full of rectangles. The mark is what makes the control look like a control.

Which side it sits on depends on what it is saying. Most lead, because the glyph and the label are
naming the same thing and the mark is the faster of the two to recognise — a red ✕ registers before
"Cancel" is read, so putting it first shortens the scan. `Login` and `Open` trail, because their
arrow is not naming the action but pointing at where it takes you, and a direction indicator belongs
at the end of the phrase it applies to for the same reason "next →" reads correctly and "→ next"
does not.

Approve takes `checkmark-badge-02` rather than a bare tick. A bare tick is also the mark for "done"
and for "selected", and Approve is neither — it is a decision someone with authority is making, and
the badge around the tick is what distinguishes conferring approval from ticking a box.

Two glyphs are shared deliberately: Reject and Cancel both take `cancel-01` (same gesture, never
offered together), Login and Open both take `arrow-right-01`.

The end padding came down a step when the glyphs arrived — 14/18/24dp, from 18/22/28. The old
figures were tuned for a text-only pill where padding was the only thing between a short word and
the curve; a leading mark now does part of that work, and keeping them made every Small button wide
enough to look like a Medium one.

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

`OrbitButtonState` replaces an `enabled` boolean for both components, so that a call site cannot
pass a flag *and* dim the control itself — which is how two screens end up with different ideas of
what an unavailable button looks like.

It has two values. There was a third, `Inactive`: interactive but stepped back, for a control that
works and just is not the point right now. It is gone, and the reasoning generalises. A
dimmed-but-tappable control is a claim nobody can read — dimming is the universal visual language
for "you cannot press this", so sighted users could not tell it from Disabled, and it had to publish
a `stateDescription` to be legible to a screen reader at all, meaning it said one thing visually and
another out loud. It also could not be dimmed honestly: still live, so still owing 4.5:1, which
forced a per-variant rule about which single layer was safe to fade. That is a lot of machinery for
a distinction that never matched a real decision in this product. De-emphasis is the variant's job —
`Secondary` or `Text` says "less important" at full contrast, the same way to everyone.

`OrbitAlpha.Inactive` went with it rather than being left for a future caller. Any new use would be
live UI owing the full 4.5:1, and that calculation depends on which ink is fading onto which
surface; inheriting a number tuned for a component that no longer exists is worse than redoing it.

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

`component/input/` holds `OrbitTextField` and `OrbitSearchField` over a shared `OrbitFieldShell`.
This is the second attempt; the first was deleted and rebuilt from scratch, and the findings from it
are recorded further down because most were bugs rather than taste.

**Solid, not glass. Fields are the one component that opts out.** Everything else is drawn through
`Modifier.orbitGlass`: a tonal fill under a white highlight gradient. That highlight is a white wash
across the top of a shape, and a field is the one component whose content is small dark text the
user is actively reading and comparing against what they meant to type. On light it lifted the fill
toward pure white and cut the contrast of every stroke sitting on it. On dark it was worse — a
visible pale film across the top of the box that read as a rendering artefact rather than material,
and grew more obvious the moment there was text under it. Glass works on a badge because a badge is
a coloured object you glance at; it does not work on a surface whose whole job is to disappear
behind its own text.

**The fill is white and black, not grey.** Flat `cardContainer` — the same near-white/near-black the
cards and attachment rows use. A field's job is to look like somewhere you can put something, and
the conventional way to say that is a recessed grey well, but a grey well only reads as recessed
against a white *page* and half this product's surfaces are not white pages. On a card the grey
field looked like a disabled control rather than an empty one. Edge definition comes from the rim
and a contact shadow, which behave the same on any surface.

**The state rims are solid too** — red for error, green for success, both taken from the badge
palette's `label` shades, which the generator has already verified past 4.5:1 in both themes. An
error rim a low-vision user cannot see is worse than none, because the form then just looks
unresponsive.

**Focus moves the rim, not the fill, and uses a quiet colour.** Tinting the fill was the rejected
alternative and it fails a specific, constant case: a field being typed into is a field with text in
it, and washing a tint under that text moves its contrast at the exact moment legibility matters
most.

Focus does *not* borrow the accent used by primary buttons. That colour is loud by design — it is
the "do the thing" blue — and putting it around a box you are merely typing into made a form of five
fields look like it had a button in the middle of it. It also collided with the error rim: focusing
a field in error swapped red for blue and hid the error while you fixed it. So focus uses
`outlineBorder`, the neutral ink at partial strength, and only when the field has nothing more
important to say; error and success outrank it and keep their colour while focused.

The rim also thickens, to `borderFocus` at 1.5dp rather than the full 2dp `borderStrong`. 1dp to 2dp
was a visible jolt — nothing reflowed, but the box appeared to grow as you tapped it. The width
change matters regardless of how small it is, because it is what stops focus and error being carried
by hue alone (WCAG 1.4.1): the colour communicates, the width complies.

**Typed text outweighs the hint it replaced.** The value is set a step heavier than the placeholder
and in the primary ink rather than the secondary, at the same size and baseline so nothing shifts as
the hint gives way. Weight rather than size for the difference, since size would move the baseline
and make the swap visible as a jump. What it buys is that a filled field and an empty one are
distinguishable across a form at a glance, and that a user's own input never reads as placeholder
text.

Each field size gets its own type size, which needed a new `fieldLarge` token: the body scale runs
out at `bodyLarge`, which is what a Medium field uses, and a Large field set in the same size is not
a larger field but a taller one.

**Overflow is measured, never assumed.** A single-line `BasicTextField` scrolls horizontally and
neither platform draws anything to say so — Android renders no indicator inside a text field at all,
iOS shows its transient one only on its own scroll views. So a value that has run off the edge looks
identical to one that ends there, and the user reads what is visible, believes it is the whole
value, and submits it. The fix is a 12dp fade on whichever edge the text runs off: the **start**
while focused, since the caret is kept in view and the hidden material is behind you, and the **end**
at rest, since the field shows the beginning of its value.

The first attempt drew the fade whenever the field merely had a value, reasoning that short text
would not reach it. That was wrong on device in a way that is obvious in hindsight — the fade
applies to the text slot, which begins where the text begins, so every filled field rendered its
first character ghosted. Whether text has overflowed is not inferable from whether text exists;
it needs `getLineRight(0)`, which reports the line's true width past the constraint, against the
slot's measured width. The reported layout `size` is already clamped and would always say it fits.

**Focus is released when the keyboard closes.** Dismissing the IME with the system back gesture does
not clear focus — Compose treats the two as separate, on the reasoning that a hardware keyboard user
may still be typing, which does not hold on a phone. The visible result was a caret blinking in a
field with a lit rim, no keyboard, and no way to type; the form looked like it was waiting for input
it could not receive, and a stale rim meant two fields could appear focused at once. The guard is
that it only fires on a *transition* from shown to hidden while the field holds focus — reacting to
"keyboard is hidden" alone would steal focus in the frame between tapping a field and the keyboard
animating in.

**The placeholder is a hint, not a label.** `label` is a separate required parameter and is the
accessible name whether or not a screen renders it visibly, because a placeholder disappears the
moment anyone types: a user who tabs away and back has no way to recall what the box was for, and
neither does a screen reader user arriving at a filled field. A form built entirely from
placeholders is legible exactly once, before it is filled in.

The placeholder ellipsises rather than wrapping — a hint is expendable and recognisable from its
first few words, and letting it wrap makes an empty field *taller* than the same field once typed
into, so the form visibly shrinks as it is completed. The value never ellipsises; it scrolls, since
text the user typed has to stay reachable by moving the caret.

**Field sizes all clear 48dp**, unlike buttons, where Small goes under the touch minimum
deliberately for dense table rows. A button that is hard to hit can be hit on the second try; a
field that is hard to hit cannot be typed into at all. `OrbitFieldSizingTest` pins this along with
the rule that a field is never shorter than a button of the same size — a column of fields ending in
a taller button looks assembled from two designs.

`OrbitSearchField` is separate rather than `OrbitTextField` with an icon, for three reasons. It is a
**pill**, which is the one piece of shape vocabulary this product spends on meaning: rounded
rectangles are things you fill in, pills are things you act with, and a search box is closer to a
control than a form field. It **clears itself**, because abandoning a search is the most common
thing anyone does with one. And its IME action is **Search**, so the keyboard offers a search key
rather than a newline. Its clear button takes the full 48dp target even though the glyph is small:
a miss lands in the field, which focuses it and opens the keyboard, so a failed clear actively makes
things worse.

##### Findings from the first attempt

Kept because three of these were bugs rather than taste, and they cost real time to diagnose. The
first two are handled by the current implementation; the last two matter for the message composer,
which is still to come. `Modifier.orbitGlassScrollbar` in `foundation/` is still there, still
uncalled, for that reason.

- **The shell must own the tap.** A `BasicTextField` is only as tall as the text currently in it, so
  an empty multi-line field is a one-line hit target inside a five-line box, and everything below the
  first line — including the whole placeholder area — is dead to a tap. Focus the field from the
  container through a `FocusRequester`.
- **`verticalScroll` goes on a wrapper, never on the text field.** Applied to a `BasicTextField` it
  hands the field an unbounded height constraint and the field measures out of the layout: it still
  draws, but it takes no focus and never reaches the accessibility tree. The field looks completely
  normal and cannot be typed into by anyone, pointer or screen reader. This one is worth re-reading
  before the rebuild, because nothing about the symptom points at the cause.
- **`Modifier.orbitGlassScrollbar` goes *before* `verticalScroll`.** Chained after, the draw lands
  inside the scroll container, so the thumb is measured against the content instead of the viewport
  and scrolls away with the text.
- **Overflow needs a custom affordance.** Neither platform draws a scrollbar here — Android shows
  nothing inside a `BasicTextField`, iOS shows its transient indicator only on its own scroll views —
  so a field that has scrolled looks identical to one that has not and the user gets no signal that
  their own text is hidden above the fold. Test for overflow against the scroll range, not by
  counting lines, which cannot account for wrapping or font scale.

The one decision flagged as unvalidated last time — carrying focus on the rim rather than the fill —
has now been checked on device and kept. It is described above.

#### The message composer

`OrbitMessageField` is built for the AI prompt box as much as for chat, and most of what follows
falls out of that. A prompt is frequently a paragraph, often pasted, and is reread before it is
sent; a chat message is usually one line typed once. A composer tuned only for chat is a single-line
pill that stops working the moment somebody writes three sentences.

**It grows, then it scrolls.** Height follows the text to five lines and then stops. Growing is what
lets you see a whole prompt while writing it. Stopping is what keeps the composer from eating the
conversation it belongs to — past about five lines the thing you are replying *to* has gone, and
people write worse prompts when they cannot see the context.

**Pill, then rectangle.** Fully round at one line, squaring to a 16dp radius once it grows, animated
between. Not decoration: a pill's corners eat horizontal space in proportion to its height, so a
tall pill has enormous scooped corners that push the first and last lines inward and waste width
exactly where the text is longest.

**The scroll is ours, not the text field's.** `BasicTextField` will happily clip at `maxLines` and
scroll internally — through a state nothing outside it can read. That is fine until you want to draw
an overflow hint, at which point there is no way to ask whether it has overflowed. Capping the height
and scrolling it ourselves puts the answer in a `ScrollState` that `OrbitScrollFade` can observe.

**Both edges fade, independently.** Top once you have scrolled down, bottom while there is more
below, both in the middle of a long prompt. Fading only the bottom — the usual shortcut — says there
is more ahead but leaves you unable to tell whether you are at the beginning, which is the half of
the question that matters when checking what you are about to send. A scrollbar would show position
*and* proportion, but it needs a gutter, and a gutter inside a 48dp composer takes width from the
text; the fade costs no space, which is why it is the convention for scrolled text everywhere else.

**Return inserts a newline.** The IME action is `Default`, not `Send`. With Send on the return key
every attempt at a second sentence fires the message off half-written.

**Send is disabled, never hidden.** Hiding it reflows the row each time the field goes from empty to
non-empty, which moves the mic button under the user's thumb as they start typing.

##### Spacing, the send glyph, and the placeholder

Three small corrections that only showed up on a device.

The mic and send buttons were butted together at zero spacing, which put two 48dp targets edge to
edge. That is legal by the touch guidance and still wrong: adjacent targets with no gap read as one
wide control and get mis-hit at the seam. The gap is now `sizing.composerControlGap`, a platform
token rather than a reuse of `spacing.xxs` — the composer is the only row in the product with four
independent controls inside one bounded shape, and it is the one place where the difference between
4dp and 10dp decides whether the row looks spacious or crammed. Tying it to a general spacing step
means a later adjustment made for cards silently re-crowds this. It is set to 4dp on Android and 6dp
on iOS: Apple's targets are 4pt smaller, so matching the numbers would leave the iOS row visibly
tighter for exactly the reason the targets differ.

The text area also gained horizontal padding, which it was missing entirely — the first character of
every message sat closer to the plus button than the placeholder's descender did to the rim.

Send is now `arrow-up-02` rather than the paper plane. The plane is mail's idiom and carries
"dispatched, gone"; this box is as often a prompt as a message, and a prompt is submitted and
answered rather than sent away. The arrow also survives 24dp far better, being two strokes instead of
a folded silhouette that turns to mush.

The placeholder had two problems. It rendered at the base style's regular weight while the field
itself renders `Medium`, so the hint sat visibly lighter and on a slightly different baseline, and
the first keystroke made the line appear to jump and thicken — a placeholder is a preview of what you
are about to type, and any metric it does not share with the real text shows up as a shift. The
default text was also too long for the space: once the three controls have taken their targets the
text area is roughly half the screen, and "Ask Orbit AI, or write a message" ellipsised to
"Ask Orbit AI, or write a mes...", which reads as a rendering fault and cut the half of the sentence
explaining the second thing you can do. It is now "Message or ask Orbit AI".

##### The typed text survives the microphone

Starting a recording swaps the text area for the live meter, so a half-written message disappears
from view. It is not gone — the value is held in the app-level `MessageComposer` and the field is
simply not rendering it — and the guarantee that matters is that discarding the clip brings it back
untouched. `onCancelRecording` clears the recording state and nothing else; the only path that
touches the text is a successful send. Verified on device: typing, recording, then hitting the red
discard leaves the caret and the words where they were.

##### Recording, and where the seam is

The design system draws; the app layer decides. `OrbitMessageField` renders an `OrbitComposerMode`
and reports taps, and it records nothing, plays nothing and opens no pickers. Everything that makes
the microphone *work* — permission, capture, the elapsed clock, accumulating amplitudes — lives in
`ui/component/composer/MessageComposer`, because all of it is sequencing over time and that is where
platform reality intrudes: a permission the user can refuse, an activity backgrounded mid-recording,
an audio session to release. None of that can live in a component that also has to render in a
gallery and a unit test. Swapping the stub for a real `AudioRecord` or `AVAudioEngine` is a change to
that one file.

`OrbitComposerMode` is a sealed type rather than an `isRecording` boolean beside a `value` string,
which would let a caller express the impossible combination and leave something to decide silently
what that renders as.

**Two exits from recording, not three.** Discard, or send. A row holding pause, stop, discard *and*
send is four controls where the two people reach for are the first and last, and the middle pair get
mistaken for each other. The first pass had a red cross that *kept* the clip — the control that looks
destructive being the safe one, which is how you teach people to distrust every red glyph in a
product. It is now a bin, and it discards.

##### The waveform

One component, `OrbitAudioWave`, for the live meter and the finished clip's scrubber, because they
are the same drawing at a different `progress`. Building them separately means the clip you were
just watching redraws itself slightly differently the moment you stop, which reads as a glitch even
when every pixel is defensible.

Three decisions inside it are worth recording, and all three were wrong first time on device:

- **Downsampling takes the peak, never the mean.** A voice note is mostly quiet with short loud
  consonants, and averaging flattens exactly those into the silence around them — the dead uniform
  strip that makes every recording look like it captured nothing.
- **Padding versus stretching depends on whether the clip is still growing.** A live meter pads at
  the front so bars grow in from the right; the empty space is the future. A *finished* clip
  stretches to fill, because it is not partially recorded. Padding it left a run of dead bars at the
  left that read as silence the recording did not contain, and it broke the playhead — progress is a
  fraction of the bars, so with a quarter of them padding, playback appeared to stall for the first
  quarter of the clip. This was visible as a dotted lead-in on every clip in the gallery.
- **The minimum bar is taller than it is wide.** Silence still gets a mark, or the waveform has gaps
  in it and a gap reads as *the clip ends here*. But with rounded caps, a stub as tall as it is wide
  is a perfect circle, and a silent clip rendered as an evenly spaced row of dots — a dotted rule,
  which is the mark used elsewhere to separate sections. A short vertical tick is unmistakably a very
  small bar.

The unplayed run is neutral ink at 32%, not the near-invisible `controlContainer` the progress track
uses. The two look like the same job and are not: a progress track's unlit run is empty space, while
a voice note's unplayed run is the clip's own shape — the only way to tell one recording from another
before playing it. At the track's 8% the row read as a disabled control.

Bars are also wider and further apart than progress slats, because the two are read differently. A
progress bar is *counted*. A waveform is read as a silhouette, and the information is in the envelope
the bar tops trace.

While a clip is playing, a shallow ripple travels through the bars behind the playhead. This is
feedback rather than decoration and it is worth the frames: a paused clip and a playing clip whose
playhead is creeping a pixel a second are visually identical for the first few seconds, and the only
other cue is a 24dp glyph at the far left of the row that nobody looks at after pressing it. On a
short clip with the volume down, or on a device whose media session silently failed to start, "is
this playing?" is the user's only question and the ripple answers it immediately. It is capped at 8%
of bar height and confined to the played run — the bars are data, and an animation that swung them
far enough to change the read of the waveform would trade the component's purpose for its
liveliness. The infinite transition is only created while `playing` is true, so a list of twenty
finished notes is not running twenty animation clocks.

`OrbitVoiceNoteRow` is a sibling of `OrbitAttachmentRow` rather than a variant, because the two
disagree about what the row's middle is for: a filename is text read once to identify a thing, while
a waveform is both the identity *and* the scrubber. The chrome is deliberately identical, since they
appear in the same list.

##### The attach menu

A `Popup`, not a column in the layout. Inline, the menu has to push the composer and everything above
it out of the way, which moves the text you are writing while you decide what to attach. It also
gives outside-tap dismissal and the Android back gesture from one `onDismissRequest`, which is the
same intent expressed two ways and would otherwise need two handlers, one of which gets forgotten.

The explicit close button is not redundant with those two: outside-tap is undiscoverable, and back is
a gesture a user mid-draft may reasonably fear will discard their message. A visible X is the one
exit that looks safe.

It is bounded at `widthIn(sizing.menuMinWidth, sizing.menuMaxWidth)`, and the maximum is
load-bearing. A `Popup` hands its content the whole window as the maximum constraint, so the
`fillMaxWidth` used to place the close button and to make rows tappable across their width meant
*the window* — the panel stretched edge to edge with its rounded corners off both sides of the
screen.

The clamp alone was not enough, though, and the second half of the fix is the more interesting one.
With only a maximum, every menu came out exactly `menuMaxWidth` wide regardless of what was in it,
because `fillMaxWidth` takes whatever bound it is handed. Adding `width(IntrinsicSize.Max)` above the
clamp makes the column measure at its children's maximum intrinsic width instead, so `fillMaxWidth`
resolves against the widest label: the rows still span the panel for tapping, and the panel is only
as wide as "Upload image" plus its padding.

The panel's contents were then cut back to what carries information. The title that used to sit
beside the close button is gone — a two-row menu reading "Upload image" and "Attach file" does not
need a heading saying "Attach", and that heading was the widest thing in the panel, so it was
setting the width while saying nothing. The two dividers are deliberately different: full-bleed under
the close button, because that separates the panel's chrome from its contents; inset between the two
rows, because a rule that stops short of both edges says "two items in one list" where a full-bleed
one would say "two different sections".

The labels are `bodyLarge` at `Medium`, a step up from a list row elsewhere. A menu item is a
*command* — read once, under time pressure, with a thumb already moving toward it — so it wants the
weight of a button label rather than of body copy. The glyphs went to `iconMd` to match; a 16dp icon
next to 17sp Medium text reads as a bullet point rather than as the row's icon.

#### Dialogs

`OrbitDialog` is the pane; `OrbitConfirmDialog` and `OrbitRenameDialog` are the two shapes actually
used. The platform `Dialog` wrapper is kept because that part is genuinely hard — scrim, window,
focus capture, back gesture — but Material's `AlertDialog` is not, since overriding its container
colour, shape, typography and button styling leaves a component that is Material in structure and
Orbit in appearance, which is the arrangement that breaks on the next Material release.

Dialogs take the glass treatment that input fields refuse. The two look like the same decision and
are not: a field's whole job is to disappear behind its own small text, and a white wash over that
costs contrast where it is least affordable. A dialog is an object floating over a dimmed screen, it
*should* read as a distinct pane of material, and the highlight along its top edge is what sells
that. It uses the surface highlight rather than the ring's, like cards, because it is a wide panel
where the wash falls as a gradient along a long edge.

`usePlatformDefaultWidth` is turned off so the panel can size itself between `menuMinWidth` and
`dialogMaxWidth`. Left on, the platform forces a fixed proportion of the screen, which on a tablet is
one very long line of text — and a line much past sixty characters is measurably harder to read
because the eye loses its place on the return sweep. That is the worst possible shape for the one
sentence a user has to read before answering. `dialogEdgeInset` is the guarantee at the other end: on
the narrowest phone there is still air around the panel, so it reads as an object on top of the
screen rather than as a new screen.

##### The message says what will happen; the buttons say Yes and No

Splitting those two jobs is the point. The common alternative — a vague message plus verb buttons —
puts the whole decision into two words that get skimmed, and "Cancel" beside a destructive action is
genuinely ambiguous: cancel the deletion, or cancel the upload that is running? Naming the object in
the message and keeping the answers to a plain Yes and No means the sentence carries the meaning and
the buttons carry only the answer. So the message must be a complete question naming the thing —
`Delete "1.pdf"? This cannot be undone.`, never "Are you sure?", which is the canonical dialog that
tells the user nothing they did not already know.

On a destructive confirmation, Yes takes the Destructive variant and No becomes the quiet secondary.
That is the right way round even though it makes the dangerous button the loud one: hiding Yes in a
weak style means a user who *does* want to delete has to hunt for the control, and hunting under a
confirmation is how people click the wrong one. Colour here is a warning label, not a discouragement.
No is placed first so that the last button — the one under a right thumb — is deliberate rather than
the one you hit by reflex on the way past. A destructive dialog also refuses to close on a scrim tap
or a back press: everywhere else those are a courtesy, but here they are indistinguishable from an
answer. The close button always stays, because there must be at least one visible way out.

##### Rename holds its own draft

`OrbitRenameDialog` keeps the edited value locally, which is the opposite of the rule everywhere else
in this system, and the reason is that a rename has a cancel. Hoisting the draft means every keystroke
writes through to the thing being renamed, so dismissing the dialog leaves the half-typed name behind.
A local draft makes cancel actually cancel. Save is disabled while the name is empty — a file with no
name is data loss with extra steps — and while it is unchanged, because a Save that does nothing
still shows a spinner and teaches the user that the button lies. Both are disabled rather than
hidden, so the button does not move as you type. The field opens with the current name in it, since
a rename is nearly always an edit of a few characters.

##### Where the flow is assembled

`OrbitAttachmentRow` and `FileAttachmentRow` still take bare callbacks and hold no state, which is
what keeps them renderable in a gallery, a test and a preview. `ManagedAttachmentRow`, in `:shared`,
is the piece that wires the three trailing controls to the dialogs they imply — written once, so that
screens do not each reinvent it and end up with one screen confirming deletion, another deleting
immediately, and a third confirming with the wrong verb.

The three are graded by damage and reversibility. **Rename** opens straight into an editable field
without a prior confirmation, because confirming before an edit asks you to agree to something you
have not written yet; the dialog's own Cancel is the safety. **Delete** confirms as destructive. 
**Remove** confirms too, but mildly — ordinary dismissal, no red, and copy that says so: "You can
attach it again."

That middle case is the one worth defending, since a confirmation on a cheap reversible action
normally is friction for its own sake. It survives here because the cross sits a few millimetres from
a red bin on the same row: the cost of a mis-tap is not the removal, it is that the user learns the
two small glyphs at that end of the row are dangerous and stops using either. One extra tap keeps the
cheap control feeling cheap.

#### Status indicators and the account menu — removed, to be rebuilt

`OrbitHealthDot`, `OrbitSeverityIndicator` and `OrbitProgressBar` were built and then deleted. The
first two overlapped the severity badges, which already carry level as text and colour together and
do it in a form that survives a dense list; keeping two ways to say "critical" guarantees the two
drift. `status/` now holds only `OrbitChip`. Their `OrbitSizing` tokens went with them — the dot,
segment and progress-bar sizes — so a rebuild starts from measurement rather than inheriting numbers
tuned for a shape nobody kept.

`OrbitMenu` and the `AccountMenu` preset over it — the avatar dropdown, with its profile row, theme
toggle and sign out — were also deleted, along with `OrbitSwitch`, which existed only to carry that
toggle. Two things learned there are worth keeping:

- **A popup needs a `MutableTransitionState` to animate its exit.** Removing a `Popup` from
  composition takes its contents with it on the same frame, so an exit transition declared the
  obvious way simply never runs and the panel disappears instantly while the open animates fine.
- **A floating panel cannot be as translucent as the rest of the glass stack.** It is the one surface
  whose background is unknown at design time, so its text has to survive landing on a photograph. It
  also cannot separate itself from every surface by fill alone: the light theme runs from `#FFFFFF`
  to `#F5F5F5`, and a fill that clears the page closes the gap to an elevated card. Depth there has
  to come from the rim and the shadow.

`OrbitAvatar` was kept. It is independent of the menu, its five tiers come straight from
`User Profile Avatars (Android & iOS).xlsx`, and `AvatarGeometryTest` pins them per platform.

#### Progress — segmented, and why

`OrbitSegmentedProgress` draws a track as discrete slats rather than one continuous fill. That is a
legibility choice rather than a stylistic one: a solid bar is read by judging a proportion, which
people do badly — a solid bar at 70% gets read as anything from 60 to 80 — whereas slats turn the
same reading into counting, and counting is both more accurate and comparable between two stacked
bars.

The cost is quantisation, which is why the component is never the whole reading. `ProgressCard` sets
the exact figure in the largest type on the card and puts the bar beneath it, inverting the usual
arrangement where a bar leads and a small number annotates it.

There are 36 slats with a 3.5dp gap, and getting there involved one piece of arithmetic worth stating
because it inverts the obvious move. The track always spans its container, so slat width is not a
free choice — it is `(width - gaps) / count`. Holding the width fixed, *fewer* slats can only mean
*wider* ones. So when the bars needed to be narrower, the count had to go **up**, not down: the
chunky, nearly-touching slats this replaced were chunky precisely because the count was low and the
gap was a hairline. The count rose from 32 and the gap more than doubled from 1.5dp, which together
take a slat on a normal phone from around ten points wide to under six, with a gap you can actually
see.

The ceiling is still respected, and it is a ratio rather than a count. The whole argument for slats
is that people count better than they estimate, and a bar cut fine enough that the slats read as a
hatched texture has given that up while keeping the quantisation error — the worst of both. That is
what 56 did: the slats were about as wide as the gaps between them and the eye stopped resolving them
as separate objects. `SegmentedProgressLayoutTest` pins the ratio at phone widths from both
directions, since a test on the constant would pass while someone halved the gap and put the solid
bar back.

One consequence is deliberate and is the trade-off the fixed count buys: a wide container spends its
extra width on wider slats rather than more of them, so the ratio falls on a tablet. That is what
keeps the same value lighting the same number of slats on every device. If wide layouts ever need
thinner slats, the fix is a second `segmentCount` at a breakpoint, not a smaller gap everywhere.

Two rounding rules are pulled out of the arithmetic and tested directly, because plain rounding gets
both wrong in a way that states something false rather than merely imprecise:

- **Above zero always lights one slat.** An empty bar is how the component says *nothing has
  started*, and 2% is not nothing.
- **Below one always leaves one slat dark.** A full bar says *finished*, and 99.4% is not finished.
  This is the one that matters, because the person looking at a handover checklist is looking for
  permission to sign off.

**A pale blue bar is only available on the dark theme, and this is forced.** The track is a
translucent neutral, so it takes the card's lightness — near-white on light, near-black on dark. To
open a gap against a near-white track the fill has to come *down*; against a near-black one it has to
go *up*. On a white card `Blue60` measures 2.55:1 against the track at the top of a slat, which is a
bar you cannot read without reading the number beside it. `Blue50` — a ramp step added for exactly
this — is as light as the light theme goes while holding the floor, and it only gets there because
the slat highlight is kept to 0.08 rather than the 0.16 the rest of the glass stack uses. Every
point of highlight is spent straight out of the contrast budget when the fill is the darker colour.
Dark uses `Blue80` and reaches 8.6:1. `SegmentedProgressContrastTest` pins both ends of every slat
at the WCAG 1.4.11 floor of 3:1, and has a third test whose only job is to fail loudly if someone
collapses the two blues into one token.

The figures are set in Google Sans Flex at 400 (`OrbitFontWeights.metric`). A dashboard number is
already the largest thing on its card, so size alone carries the emphasis; adding weight makes it
shout, and a screen of six shouting cards has no hierarchy left. 300 was tried first and went too far
the other way — at that weight the strokes thin enough that a figure over a glass card starts to look
tentative.

The card's label is muted and one tier below caption (`OrbitTypographyTokens.cardLabel`). With label
and figure both at full-strength ink the card read as two equally important things stacked rather
than as a number with a name; the label is what has to give, because the figure is the thing anyone
opened the dashboard for. `cardLabel` is the only style in the system below the caption size, and
that is only safe because it is always set in caps — cap height there still exceeds the x-height of
caption-size sentence case, so it is not the regression the number suggests. The uppercasing happens
in `ProgressCard`, not at the call site, so one normal-case string can serve both the caps display
and the spoken description; screen readers never see the caps, which matters because TalkBack spells
short all-caps words out as initialisms.

`OrbitDelta` keeps direction and sentiment apart. The arrow points the way the number moved; the
colour says whether that is good news. They coincide often enough to be tempting to merge, and then
the first time the chip lands on open defects or cost variance the dashboard goes green while the
project catches fire. `higherIsBetter = false` gives a red up-arrow, which the gallery shows.

The chip is filled with the badge tone's translucent `container` rather than outlined, so it matches
the status pills, but the fill stays a tint rather than a solid block — a solid green pill at this
size pulls the eye before the percentage does, which is backwards for a footnote. `DeltaContrastTest`
re-derives the whole glass stack over a *card* rather than over the elevated surface the badge
palette was tuned against, since the card is further from the tuning point than anything the
generator considered. Its last test is inverted on purpose: it asserts that green and red are *not*
separable in greyscale, which is what makes the arrow load-bearing rather than decorative.

`ProgressCard` will take `label = null` for a card sitting under a section header that already names
it, which saves a line of height on every card in a column. Doing so makes `contentDescription`
mandatory — without a label there is nothing to build a spoken description from, and the card would
otherwise announce a bare percentage. That is a `require`, not a nullable fallback, because a silent
degradation here is invisible to everyone except the people it excludes.

#### Display and state

`OrbitAvatarGroup` overlaps its faces and collapses the rest into a count, which is right because the
useful reading of a watcher list is "a handful, and one of them is me" rather than any individual
face. The separating rings between faces are drawn in the *page* colour, not as a border, and this is
the one thing about the component that is easy to get wrong: a stack on a card needs the card's
colour passed to `background`, and a hairline instead of a ring makes two mid-tone faces merge into a
smear at arm's length. `AvatarGroupTest` pins the overlap fraction inside a range and the ring above
the avatar hairline, because both failures look fine with two members and only appear at five.

`OrbitCountBadge` draws the same glass stack as `OrbitBadge` on the same tone palette, because the
two frequently share a row and a solid pill beside a translucent one reads as two components from two
different products. It diverges in exactly one place: **the rim is the tone's `label`, not its
`border`.** A status pill sits on a card, where its own tint locates it and the rim is a finishing
touch; a count badge overlaps an icon, so its translucent fill is by definition close to whatever is
behind it and cannot be what separates the two. That work falls to the rim, which then has to clear
the 3:1 graphical floor of WCAG 1.4.11 — and the palette's `border` measures about 2.1:1, which is
where the first version of this failed. `CountBadgeContrastTest` samples the digit down the whole
sheened-fill-plus-highlight stack and pins the rim separately, so swapping back to `border` fails
immediately.

The badge is still outside its tested envelope when placed directly over a dense glyph rather than
over a surface; the rim and the contact shadow are what make that placement survivable, and a screen
doing it should keep the badge clear of the icon's strokes.

`OrbitAttachmentRow` replaced a square thumbnail tile, and the swap is worth recording because the
tile was not obviously wrong. At 56dp square there is no room for a filename, so the name lived only
in the spoken description — a sighted user looking at four grey squares could not tell the drawing
from the invoice, which is the entire question you ask of an attachment list. Going full-width costs
one item per screen and buys the two facts that identify a file. It also gives removal somewhere to
live: on a square tile the only place for a close control is a corner, at roughly 20dp, under half
the minimum touch target.

The leading slot is an `OrbitAttachmentLeading` with three cases rather than one nullable painter,
because the three are drawn under different rules. A photo is cropped square and rounded; the
file-type artwork (PDF, Docs, Sheets) is drawn **whole and untinted**, because those marks have a
folded corner and their own margins and cropping to a square eats the corner that says "document";
the fallback is `attachment-02`, tinted with the theme's ink like every other glyph. It was `pin-02`
first, and that was wrong on meaning: a pin means "kept" or "stuck to the top", which is a different
claim from "attached to this record".

The fallback is drawn at the **full mark size**, the same box the artwork fills, and at
`iconStrokeHairline` — the thinnest weight in the system, and the only thing that uses it. Those two
go together. Sized down it sat in a pool of empty space while the artwork rows filled theirs, and
the filenames stopped lining up; but at full size and a normal stroke the one *unrecognised* file
became the heaviest mark in the list, which is backwards for the row carrying the least information.
Growing the glyph and thinning its stroke is what lets an outline sit beside flat colour without one
shouting over the other.
Collapsing these into one parameter would leave the caller remembering which rules apply, and the
failure mode — a stretched PDF badge — is quiet.

Nothing sits behind the mark. A container behind a shape that is *already* bounded reads as a box
inside a box, and the row's own border is doing that job. The fill is `cardContainer` (near-white,
near-black) rather than the grey tonal fill the tile used, so nothing competes with the artwork for
colour; edge definition comes from the 1dp border and a contact shadow instead.

The filename is set a size up from body, at Medium weight — the same treatment a typed value gets in a
text field, and for the same reason. The filename is the row's content; the size beneath it and the
glyphs beside it are chrome telling you what to do with that content. Set in plain body regular it
carried no more visual weight than the file size, so a column of attachments read as evenly grey lines
and you had to *look for* the name rather than simply see it. The size stays subordinate at
`metricCaption` in secondary ink, which is the whole hierarchy the row needs.

##### Trailing actions

Three optional controls, and which ones a screen passes says what kind of list it is. A **composer**
passes `onRemove` — a neutral `cancel-01`, because detaching a file from a draft is reversible by
reattaching, and a red cross on every row makes a list of files look like a list of errors. A
**library** passes `onRename` and `onDelete`: `text` in neutral ink for the housekeeping action, and
`delete-01` in the badge palette's red for the one that destroys the file. That red is the only
colour on the row, and it is there because the cost of confusing the two is not symmetric.

`onRemove` and `onDelete` should not both be passed. They look nearly identical and mean very
different things, and a row offering both asks the user to distinguish "take this off the draft"
from "delete this permanently" using two small glyphs.

Rename comes before delete, with the destructive control at the far edge, so a thumb reaching in
from the side of the screen meets the recoverable action first — the same reasoning that puts Delete
at the bottom of a menu rather than the top.

Two is also the ceiling. Each control takes the full 48dp touch target regardless of its glyph size,
so three would claim 144dp of a 360dp screen and start eating the filename, which is the row's
actual content. A row needing more than two actions wants a menu.

The filename ellipsises in the **middle**. Truncating the tail throws away the extension, which is
the one part of a filename that says what the thing is.

The extension-to-artwork mapping lives in `:shared` (`FileAttachmentRow`), not in the design system.
`:core:designsystem` cannot own artwork for Google Docs without becoming a library only this product
can use, so it takes the leading case and stays ignorant of what a `.xlsx` is. The recognised set is
deliberately three formats: there are hundreds of extensions, most appear once, and a
half-populated icon set looks more broken than a consistent fallback does.

`OrbitSkeleton` pulses opacity instead of sweeping a shimmer. A sweep is horizontal motion, which is
the same gesture vocabulary as a swipe and reads as content already moving; it is also a per-frame
shader over every placeholder on screen, spent at the exact moment the device is busy doing the thing
being waited for. The pulse is frozen under `LocalInspectionMode` so previews and screenshot tests
have something that settles.

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

The monochrome controls are verified the same way by `ControlContrastTest`. Two of its results
shaped the components rather than merely confirming them, and both were about the removed Inactive
state — they are recorded here because the constraints are properties of the palette and will bind
again on whatever reaches for a faded-but-live control next:

- **A faded live control needs 0.65, not the conventional 0.60.** It is still tappable, so it owes
  the full 4.5:1 for text rather than the 3:1 non-text floor. The binding case was the deep-charcoal
  control content on a white surface, which lands at 4.47:1 at 0.60 — failing by a hair — and 5.3:1
  at 0.65.
- **An inverted fill cannot be faded at all while staying legible.** Its content is near-white, so
  fading the fill moves it toward the surface and drags the content down with it: about 3.8:1 at the
  top of the highlight, and about 2.6:1 if the content fades too. No value both reads as stepped
  back and stays readable. This is the finding that pushed buttons from inverted slabs to tinted
  chips, which do not have the problem — thinning a translucent tone moves its background toward the
  surface, and every tone's label already clears the minimum against the surface, so contrast rises
  as it fades rather than falling.

Neither is asserted any more. With Disabled the only faded state, and WCAG 1.4.3 exempting it from
the minimum outright, there is no code path left for either test to guard — so they were removed
rather than left passing vacuously.

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
