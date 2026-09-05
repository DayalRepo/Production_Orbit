#!/usr/bin/env python3
"""Generates the icon set and the badge colour palette for :core:designsystem.

Two files are produced, both marked generated and both meant to be regenerated rather than
hand-edited:

  icon/OrbitIcons.kt      SVG path data for the Hugeicons free stroke-rounded glyphs we use.
  theme/BadgeColors.kt    Badge tone palette, with every shade tuned until it clears WCAG 4.5:1.

The palette is generated rather than hand-picked because the contrast target is the thing that
matters, and eyeballing 11 hues x 2 themes x 3 roles is how a label ends up at 4.1:1 in one tone
and nobody notices for a year. Here the tuner walks lightness until the ratio passes, so the
numbers in the Kotlin file are a consequence of the requirement instead of an approximation of it.

Usage:
    python tools/gen_designsystem.py            # fetch icons, write both files
    python tools/gen_designsystem.py --offline   # reuse tools/hugeicons.json
"""

from __future__ import annotations

import argparse
import colorsys
import json
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DS = ROOT / "core/designsystem/src/commonMain/kotlin/com/orbitai/erp/core/designsystem"
CACHE = Path(__file__).resolve().parent / "hugeicons.json"

CDN = "https://cdn.jsdelivr.net/npm/@hugeicons/core-free-icons@4.3.0/dist/esm"

# Local name -> Hugeicons module name. The battery glyphs are renamed to the severity level they
# represent, because "BatteryMedium02" tells a call site nothing about why it is on screen.
ICONS = {
    "Progress": "Progress03Icon",
    "Clock": "Clock01Icon",
    "ListBullet": "LeftToRightListBulletIcon",
    "CheckmarkBadge": "CheckmarkBadge02Icon",
    "CancelCircle": "CancelCircleHalfDotIcon",
    "Mail": "Mail02Icon",
    "Upload": "Upload01Icon",
    "Calendar": "Calendar04Icon",
    # Two more calendars, which is not redundancy. `Calendar` (04) is the generic one used for a date
    # in running content; `CalendarDate` (01) is the grid-faced glyph that reads as an input field's
    # affordance at 16dp; `CalendarSchedule` (03) carries a clock and so means "commit this to a
    # schedule" rather than "here is a date", which is what the Schedule button needs to say.
    "CalendarDate": "Calendar01Icon",
    "CalendarSchedule": "Calendar03Icon",
    "Puzzle": "PuzzleIcon",
    "NotepadDashed": "NotepadTextDashedIcon",
    "Play": "PlayIcon",
    "Pause": "PauseIcon",
    "Bookmark": "Bookmark02Icon",
    "Add": "Add01Icon",
    # The stepper pair for the quantity field. `Add` (add-01) is already a plus, but it is the *action*
    # plus used on "add stage" and "attach file" rows, where it reads as "create a new thing". A
    # stepper's plus means "one more of the thing already here", and the pair only reads as a pair if
    # both halves come from the same drawing — so plus-sign and minus-sign are taken together rather
    # than pairing minus-sign with add-01 and hoping the strokes match.
    "PlusSign": "PlusSignIcon",
    "MinusSign": "MinusSignIcon",
    "Tick": "Tick02Icon",
    "BadgeAlert": "BadgeAlertIcon",
    "TimeQuarter": "TimeQuarter02Icon",
    "Status": "StatusIcon",
    "Delete": "Delete01Icon",
    "Repeat": "RepeatIcon",
    "StopWatch": "StopWatchIcon",
    # Action-button glyphs. Loading is spun by `OrbitLoadingIcon`; its gapped ring is what makes
    # the rotation legible, which a closed circle would not be.
    "Loading": "Loading03Icon",
    "Cancel": "Cancel01Icon",
    "Sent": "SentIcon",
    "Note": "Note01Icon",
    # Input-field glyphs.
    "Attachment": "Attachment01Icon",
    "Mic": "Mic02Icon",
    # The composer's own mic, and it is `mic-01` rather than the `mic-02` above. 02 is drawn on a
    # stand, which reads as studio equipment; 01 is the bare capsule, which is what a "hold to talk"
    # control means. The two coexist because the older one is still what a settings row wants.
    "MicRecord": "Mic01Icon",
    # The waveform on a voice clip. Used for the attachment's leading mark and, tiled, as the
    # resting shape of the live visualiser before any amplitude has arrived.
    "AudioWave": "AudioWave01Icon",
    # The two rows in the composer's attach menu. Both carry the small plus that marks them as
    # *adding* something rather than browsing what is already there — the menu is a list of things
    # you can do, and a bare picture frame beside a bare folder reads as a list of places instead.
    "ImageUpload": "ImageAdd01Icon",
    "FileUpload": "FileAddIcon",
    # Send, and it is an arrow rather than the paper plane. A plane is idiomatic for *mail* and
    # carries the sense of something dispatched and gone; an upward arrow is what a prompt box
    # means — commit this, get an answer back. It also survives being small far better, since it is
    # two strokes rather than a shape with an internal fold.
    "ArrowUp": "ArrowUp02Icon",
    "Expand": "ExpandIcon",
    "Collapse": "CollapseIcon",
    "BatteryLow": "BatteryLowIcon",
    "BatteryMedium": "BatteryMedium01Icon",
    "BatteryHigh": "BatteryMedium02Icon",
    "BatteryFull": "BatteryFullIcon",
    "Sun": "Sun01Icon",
    "Moon": "Moon02Icon",
    # Account-menu glyphs. `User` is the plain person rather than `UserCircleIcon`, because the menu
    # row already sits beside a circular avatar and two concentric circles read as a duplicate.
    "User": "UserIcon",
    "Logout": "Logout01Icon",
    # Delta arrows. Diagonal rather than vertical: a plain up arrow is also the "scroll to top" and
    # "sort ascending" glyph, and the diagonal is unambiguously a trend line. They are named for the
    # direction of the change rather than the direction the arrow points, since a falling metric is
    # sometimes the good one and the call site should not have to translate.
    "TrendUp": "ArrowUpRight01Icon",
    "TrendDown": "ArrowDownRight01Icon",
    # The document glyph on an attachment tile. `file-02` rather than the paperclip in "Attachment":
    # a paperclip means "this has something attached to it" and belongs on the button that adds one,
    # whereas the tile *is* the attachment and should depict the thing rather than the act.
    "File": "File02Icon",
    # The fallback on an attachment row, for formats with no artwork of their own. `attachment-02`
    # rather than `pin-02`: a pin means "kept" or "stuck to the top", which is a different claim
    # from "attached to this record", and the row is already a list of attachments.
    "AttachmentFile": "Attachment02Icon",
    // The trailing glyph on Open and Login — the two actions that take you somewhere else.
    "ArrowRight": "ArrowRight01Icon",
    # Back / up navigation on the mobile top bar.
    "ArrowLeft": "ArrowLeft01Icon",
    "Menu": "Menu01Icon",
    "Notification": "Notification03Icon",
    "FilterHorizontal": "FilterHorizontalIcon",
    "SidebarLeft": "SidebarLeftIcon",
    "BubbleChatAdd": "BubbleChatAddIcon",
    "Share": "Share08Icon",
    # Copy to clipboard. Sits beside the phone number in the avatar cards, which is the one value in
    # this product that exists to be pasted somewhere else.
    "Copy": "Copy01Icon",
    # The disclosure chevron on a dropdown. Its own name rather than a reuse of `ArrowUp` rotated,
    # because a chevron and an arrow say different things -- an arrow moves you somewhere, a chevron
    # opens something in place -- and the dropdown rotates this one between the two states itself.
    "ChevronDown": "ArrowDown01Icon",
    # `search-02` rather than `search-01`. The two differ in the angle of the handle; 02's sits
    # closer to 45 degrees, which keeps the glass circle centred in a pill instead of riding high.
    "Search": "Search02Icon",
    # Rename an attachment. A capital A with a caret, which is the near-universal mark for "edit
    # this text" and reads as such at 16dp where a pencil turns to mush.
    "TextEdit": "TextIcon",
    "Download": "Download01Icon",
    "MoreVertical": "MoreVerticalIcon",
    "AiMagic": "AiMagicIcon",
    # Step-indicator nodes. Stroke circle for completed (filled in Compose) and upcoming; dashed
    # ring marks the stage the work is currently in — see OrbitStepIndicator.
    "Circle": "CircleIcon",
    "DashedLineCircle": "DashedLineCircleIcon",
    # Numbered stage marks for the vertical step track (Hugeicons stroke-rounded one…five-square).
    "OneSquare": "OneSquareIcon",
    "TwoSquare": "TwoSquareIcon",
    "ThreeSquare": "ThreeSquareIcon",
    "FourSquare": "FourSquareIcon",
    "FiveSquare": "FiveSquareIcon",
    # Chat message reply affordance on OrbitMessageBubble.
    "Reply": "ReplyIcon",
    # Role bottom-nav glyphs (CEO first). Stroke-rounded free set.
    "DashboardCircle": "DashboardCircleIcon",
    "Layers01": "Layers01Icon",
    "BubbleChat": "BubbleChatIcon",
    "Brain03": "Brain03Icon",
    "NoteAdd": "NoteAddIcon",
    "NotepadText": "NotepadTextIcon",
    "Warehouse": "WarehouseIcon",
    "ShoppingCartAdd01": "ShoppingCartAdd01Icon",
    "BadgeCheck": "BadgeCheckIcon",
}


# --------------------------------------------------------------------------- icons


def fetch(module: str, attempts: int = 4) -> str:
    """The CDN times out often enough over 31 sequential requests to be worth retrying."""
    last = ""
    for attempt in range(attempts):
        out = subprocess.run(
            ["curl", "-sSL", "--max-time", "45", "--retry", "2", f"{CDN}/{module}.js"],
            capture_output=True, text=True,
        )
        if out.returncode == 0 and "export default" in out.stdout:
            return out.stdout
        last = out.stderr.strip() or f"exit {out.returncode}"
        if attempt < attempts - 1:
            time.sleep(2 * (attempt + 1))
    raise RuntimeError(f"{module}: fetch failed after {attempts} attempts ({last})")


def parse_elements(js: str) -> list[tuple[str, dict[str, str]]]:
    """Pulls the [tag, attrs] pairs out of a Hugeicons ESM module."""
    body = js[js.index("= ["):js.index("];") + 1]
    elements = []
    for match in re.finditer(r'\["(\w+)",\s*\{(.*?)\}\]', body, re.S):
        attrs = dict(re.findall(r'(\w+):\s*"([^"]*)"', match.group(2)))
        elements.append((match.group(1), attrs))
    return elements


def num(attrs: dict[str, str], key: str, default: float = 0.0) -> float:
    try:
        return float(attrs.get(key, default))
    except (TypeError, ValueError):
        return default


def to_path_data(tag: str, a: dict[str, str]) -> str:
    """Reduces any SVG primitive to path data, so Compose needs only one parser at runtime."""
    if tag == "path":
        return a.get("d", "")
    if tag == "circle":
        cx, cy, r = num(a, "cx"), num(a, "cy"), num(a, "r")
        return (f"M{cx - r} {cy}A{r} {r} 0 1 0 {cx + r} {cy}"
                f"A{r} {r} 0 1 0 {cx - r} {cy}Z")
    if tag == "ellipse":
        cx, cy, rx, ry = num(a, "cx"), num(a, "cy"), num(a, "rx"), num(a, "ry")
        return (f"M{cx - rx} {cy}A{rx} {ry} 0 1 0 {cx + rx} {cy}"
                f"A{rx} {ry} 0 1 0 {cx - rx} {cy}Z")
    if tag == "line":
        return f"M{num(a, 'x1')} {num(a, 'y1')}L{num(a, 'x2')} {num(a, 'y2')}"
    if tag == "rect":
        x, y = num(a, "x"), num(a, "y")
        w, h = num(a, "width"), num(a, "height")
        r = num(a, "rx", num(a, "ry"))
        if r <= 0:
            return f"M{x} {y}H{x + w}V{y + h}H{x}Z"
        return (f"M{x + r} {y}H{x + w - r}A{r} {r} 0 0 1 {x + w} {y + r}"
                f"V{y + h - r}A{r} {r} 0 0 1 {x + w - r} {y + h}"
                f"H{x + r}A{r} {r} 0 0 1 {x} {y + h - r}"
                f"V{y + r}A{r} {r} 0 0 1 {x + r} {y}Z")
    if tag in ("polyline", "polygon"):
        p = re.findall(r"-?[\d.]+", a.get("points", ""))
        if len(p) < 4:
            return ""
        data = f"M{p[0]} {p[1]}" + "".join(
            f"L{p[i]} {p[i + 1]}" for i in range(2, len(p) - 1, 2)
        )
        return data + ("Z" if tag == "polygon" else "")
    return ""


def collect_icons(offline: bool) -> dict[str, dict[str, list[str]]]:
    if offline:
        return json.loads(CACHE.read_text())

    icons: dict[str, dict[str, list[str]]] = {}
    for name, module in ICONS.items():
        stroke: list[str] = []
        fill: list[str] = []
        for tag, attrs in parse_elements(fetch(module)):
            data = to_path_data(tag, attrs)
            if not data:
                raise RuntimeError(f"{name}: cannot convert <{tag}>")
            if attrs.get("fill", "none") not in ("", "none"):
                fill.append(data)
            if attrs.get("stroke"):
                stroke.append(data)
        if not (stroke or fill):
            raise RuntimeError(f"{name}: no drawable geometry")
        icons[name] = {"stroke": stroke, "fill": fill}
        print(f"  {name:16} {len(stroke)} stroke  {len(fill)} fill")

    CACHE.write_text(json.dumps(icons, indent=1) + "\n")
    return icons


ICONS_HEADER = '''package com.orbitai.erp.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The icon set, taken from the Hugeicons free stroke-rounded collection (MIT, published as
 * `@hugeicons/core-free-icons`). Licence text is in `licenses/MIT-Hugeicons.txt`.
 *
 * Every glyph is authored on a 24x24 viewport with a 1.5-unit stroke, round caps and round joins,
 * which is where the set's uniform stroke weight comes from. Because the stroke is in viewport
 * units it scales with the glyph: at `iconMd` (24dp) it draws 1.5dp, at a 16dp badge icon it draws
 * 1dp. That is intended — holding the stroke at a literal 1.5dp on a 16dp glyph fills in the
 * counters and the icon turns into a blob.
 *
 * Colour is opaque black here and always overwritten: every call site renders through
 * `Icon(..., tint = ...)`, which replaces it outright.
 *
 * Generated by `tools/gen_designsystem.py`. Do not edit by hand.
 */
object OrbitIcons {
'''

ICONS_FOOTER = '''}

/**
 * Builds an [ImageVector] from raw SVG path data.
 *
 * Filled subpaths are laid down before stroked ones so an outline is never buried under its own
 * fill. `Progress` is the glyph that needs this, being a ring with a solid sector inside it.
 */
private fun vector(
    name: String,
    stroke: List<String>,
    fill: List<String> = emptyList(),
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = VIEWPORT,
    viewportHeight = VIEWPORT,
).apply {
    fill.forEach { data ->
        addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            fill = SolidColor(Color.Black),
        )
    }
    stroke.forEach { data ->
        addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = STROKE_WIDTH,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
}.build()

private const val VIEWPORT = 24f

/**
 * Stroke weight in viewport units, so 1.8dp once the glyph is rendered at 24dp.
 *
 * The stroke is in viewport units, which means it scales with the glyph — and that is exactly why
 * this is 1.8 rather than 1.5. Almost nothing draws at 24dp: a badge glyph is 14–18dp and a small
 * button glyph is 16dp, so at 1.5 units those rendered at 0.9–1.1dp and looked hairline-thin beside
 * their labels. At 1.8 the same glyphs land at 1.05–1.35dp and a 24dp toolbar icon at 1.8dp, which
 * puts the whole set inside the 1.5–2dp band the design rules ask for at the sizes actually used.
 */
private const val STROKE_WIDTH = 1.8f
'''


def kotlin_string(s: str) -> str:
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$") + '"'


def emit_icons(icons: dict[str, dict[str, list[str]]]) -> None:
    def path_list(label: str, paths: list[str]) -> str:
        joined = ",\n                ".join(kotlin_string(p) for p in paths)
        return f"{label} = listOf(\n                {joined},\n            )"

    blocks = []
    for name in ICONS:
        data = icons[name]
        args = [f'name = "{name}"']
        if data["fill"]:
            args.append(path_list("fill", data["fill"]))
        args.append(path_list("stroke", data["stroke"]))
        joined = ",\n            ".join(args)
        blocks.append(
            f"    val {name}: ImageVector by lazy {{\n"
            f"        vector(\n            {joined},\n        )\n    }}"
        )

    target = DS / "icon/OrbitIcons.kt"
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(ICONS_HEADER + "\n\n".join(blocks) + "\n" + ICONS_FOOTER, newline="\n")
    print(f"wrote {target.relative_to(ROOT)} ({len(icons)} icons)")


# ------------------------------------------------------------------------- palette

# Hue and saturation per tone. Lightness is not fixed here; the tuner derives it.
TONES = [
    ("Slate", 215, 0.16),
    ("Blue", 212, 0.82),
    ("Indigo", 245, 0.72),
    ("Violet", 270, 0.68),
    ("Cyan", 192, 0.82),
    ("Teal", 172, 0.68),
    ("Green", 145, 0.62),
    ("Amber", 40, 0.88),
    ("Orange", 25, 0.86),
    ("Red", 2, 0.74),
    ("Rose", 340, 0.70),
]

# A translucent badge shows whatever is behind it, so contrast has to be checked against the worst
# case rather than the base surface: the most elevated container in each theme.
WORST_SURFACE = {"light": (0xE8, 0xE8, 0xE8), "dark": (0x2A, 0x2A, 0x2A)}
CONTAINER_ALPHA = {"light": 0.26, "dark": 0.34}
BORDER_ALPHA = {"light": 0.55, "dark": 0.65}

# Glass is drawn as three stacked layers, and the tuner has to model all three or it is tuning
# against a background that never appears on screen:
#
#   1. the surface behind the badge,
#   2. the tint, at CONTAINER_ALPHA * SHEEN along the top edge fading to CONTAINER_ALPHA,
#   3. a white highlight, strongest at the top edge and fading to nothing.
#
# The top and the bottom of a pill therefore have different backgrounds, and which one is worse
# depends on the theme: in light themes the extra tint at the top darkens it under dark text, in
# dark themes the white highlight lightens it under light text. Both ends get measured and the
# label has to clear the minimum against the worse of the two.
SHEEN = 1.45
HIGHLIGHT_ALPHA = {"light": 0.22, "dark": 0.10}
EDGE_FADE = 0.5
WHITE = (0xFF, 0xFF, 0xFF)
# The dark tint is deliberately far darker than the light one rather than its mirror image, and the
# exact value matters more than it looks.
#
# Raising it is the obvious way to make dark-theme fills look more colourful and it backfires twice.
# At L=0.40 the search runs out of room — the teal hue has no lightness or saturation that clears the
# minimum against a 34%-alpha fill plus the sheen — and at L=0.56 even pure white text falls to
# 4.2:1. Worse, the values in between succeed only by bleaching the labels: at 0.34 every label is
# forced pale enough that dark Red (#F3B1AF) and dark Green (#C5F1D8) converge toward white and stop
# being separable from each other, which `FieldValidityContrastTest` catches. A badge's hue is a
# signal, and washing it out to make the container prettier trades the message for the envelope.
#
# 0.26 lets every label keep its full base saturation (Green lands on #73DE9F, Cyan on #61D3EF).
# Luminance for the controls that wear these tones comes from the rim instead — see the tinted button
# rim in `OrbitButton`, which is where a deep fill under a bright edge reads as lit glass rather than
# as a flat slab.
CONTAINER_LIGHTNESS = {"light": 0.52, "dark": 0.26}
BORDER_LIGHTNESS = {"light": 0.42, "dark": 0.62}
SOLID_LIGHTNESS = {"light": 0.36, "dark": 0.72}
LABEL_START = {"light": 0.42, "dark": 0.66}

WCAG_TEXT_CONTRAST = 4.5

# Tune to a hair above the requirement rather than exactly to it.
#
# The generator's model of the render is now faithful — float compositing, quantized alphas — but
# not bit-identical, since Compose packs colour components with its own precision. Landing a shade
# on 4.501:1 means the test that re-derives the number in Kotlin can legitimately read 4.499:1.
# The margin costs nothing visible and keeps the two in agreement.
TEXT_CONTRAST = 4.55
# The icon is pushed this many tuner steps past the label. Distinct shades were asked for, and
# stepping away from the fill can only increase contrast, so it is safe by construction.
ICON_OFFSET_STEPS = 5

Rgb = tuple[int, int, int]


def hsl(h: float, s: float, l: float) -> Rgb:
    r, g, b = colorsys.hls_to_rgb(h / 360.0, l, s)
    return (round(r * 255), round(g * 255), round(b * 255))


def _channel(c: float) -> float:
    v = c / 255.0
    return v / 12.92 if v <= 0.03928 else ((v + 0.055) / 1.055) ** 2.4


def luminance(c: Rgb) -> float:
    return 0.2126 * _channel(c[0]) + 0.7152 * _channel(c[1]) + 0.0722 * _channel(c[2])


def contrast(a: Rgb, b: Rgb) -> float:
    la, lb = luminance(a), luminance(b)
    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)


def composite(fg: Rgb, bg: Rgb, alpha: float) -> Rgb:
    """
    Source-over composite, deliberately *not* rounded to 8-bit.

    Rounding each layer was a real bug, not a rounding nit. Compose composites in floating point,
    and on a dark background a 1/255 discrepancy moves the contrast ratio far more than it does on
    a light one, because the denominator is tiny. The dark Slate label was tuned to 4.61:1 here and
    measured 4.46:1 in the Kotlin test — a failure caused entirely by this function rounding
    intermediates the renderer does not.
    """
    return tuple(alpha * fg[i] + (1 - alpha) * bg[i] for i in range(3))


def quantized(alpha: float) -> float:
    """
    The alpha Compose will actually see.

    Alphas are emitted as the top byte of an ARGB literal, so 0.34 is stored as 87/255 = 0.3412 and
    every subsequent multiplication in the renderer starts from that, not from 0.34. Tuning against
    the unquantized value leaves the same kind of small error as rounding the composites.
    """
    return round(alpha * 255) / 255


def argb(c: Rgb, alpha: float = 1.0) -> str:
    return "0x%02X%02X%02X%02X" % (
        round(alpha * 255), round(c[0]), round(c[1]), round(c[2]),
    )


GLASS_SAMPLES = 11


def glass_backgrounds(tint: Rgb, surface: Rgb, mode: str) -> list[Rgb]:
    """
    Every effective background a glass pill presents, sampled down its height.

    Checking only the top and bottom edges would not be sound. Going up the pill the tint gets
    stronger *and* the white highlight gets stronger, and those pull luminance in opposite
    directions, so the darkest point can sit somewhere in the middle rather than at either end.
    Sampling the gradient avoids having to reason about where that is.

    Both layers are interpolated linearly here, which is why the Kotlin side must draw them as
    plain two-stop `Brush.verticalGradient`s. A different easing would invalidate these numbers.
    """
    base_alpha = quantized(CONTAINER_ALPHA[mode])
    top_alpha = min(base_alpha * SHEEN, 1.0)

    # Two highlight levels, not one, and the second is why buttons can wear these colours.
    #
    # A badge is never hovered, so for a long time the resting highlight was the only case that
    # existed. Tonal buttons now draw from this same palette, and a hovered button lifts its
    # highlight by BUTTON_HOVER_LIFT — which puts the top edge brighter than anything the badge
    # tuning had ever checked. Rather than give buttons a second, dimmer palette, the tuner clears
    # both levels, so a hovered button is verified by construction and the resting badge simply has
    # a little contrast in hand.
    highlights = [
        HIGHLIGHT_ALPHA[mode],
        min(HIGHLIGHT_ALPHA[mode] * BUTTON_HOVER_LIFT, 1.0),
    ]

    out = []
    for highlight in highlights:
        for i in range(GLASS_SAMPLES):
            t = i / (GLASS_SAMPLES - 1)
            tinted = composite(tint, surface, top_alpha + (base_alpha - top_alpha) * t)
            out.append(composite(WHITE, tinted, highlight * (1 - t)))
    return out


def worst_contrast(color: Rgb, backgrounds: list[Rgb]) -> float:
    return min(contrast(color, bg) for bg in backgrounds)


def tune(
    hue: float,
    saturation: float,
    against: list[Rgb],
    start: float,
    step: float,
) -> tuple[Rgb, float, float]:
    """
    Finds the most saturated colour on `hue` that clears the minimum against every background.

    Lightness alone is not enough. A fully saturated cyan or green has a luminance ceiling well
    below white, so once the dark-theme fill got stronger there was no lightness at S=0.82 that
    cleared 4.5:1 — the search simply ran out of room at L=0.97. Desaturating as the colour
    brightens is how every real palette solves this, and it is what "bright cyan" looks like
    anyway: nearly white with a cyan cast, not neon.

    Saturation is the outer loop and descends, so the result keeps as much colour as the contrast
    requirement allows rather than bleaching everything to be safe.
    """
    # Slate starts at 0.16, already below the floor, so the floor has to be relative to the tone
    # rather than absolute — otherwise the loop never runs for the near-neutral hues.
    floor = min(saturation, MINIMUM_SATURATION)
    s = saturation
    while s >= floor:
        l = min(max(start, 0.03), 0.97)
        while 0.02 < l < 0.98:
            candidate = hsl(hue, s, l)
            if worst_contrast(candidate, against) >= TEXT_CONTRAST:
                return candidate, l, s
            l += step
        s -= SATURATION_STEP
    raise RuntimeError(f"hue {hue} cannot clear {TEXT_CONTRAST}:1 at any lightness or saturation")


MINIMUM_SATURATION = 0.25
SATURATION_STEP = 0.04


def build_palette() -> tuple[dict, list]:
    palette: dict[str, dict[str, dict[str, str]]] = {}
    report = []
    for name, h, s in TONES:
        palette[name] = {}
        for mode in ("light", "dark"):
            tint = hsl(h, s, CONTAINER_LIGHTNESS[mode])
            effective = glass_backgrounds(tint, WORST_SURFACE[mode], mode)
            # Light themes darken text to gain contrast; dark themes lighten it.
            step = -0.01 if mode == "light" else 0.01

            label, label_l, label_s = tune(h, s, effective, LABEL_START[mode], step)
            # Starting from the label's own lightness and saturation, pushed further from the fill,
            # so the icon is a distinct shade and can only have more contrast, never less.
            icon, _, _ = tune(
                h, label_s, effective, label_l + step * ICON_OFFSET_STEPS, step,
            )

            solid = hsl(h, s, SOLID_LIGHTNESS[mode])
            candidates = [(0xFF, 0xFF, 0xFF), (0x12, 0x12, 0x12)]
            on_solid = max(candidates, key=lambda c: contrast(c, solid))

            palette[name][mode] = {
                "container": argb(tint, CONTAINER_ALPHA[mode]),
                "border": argb(hsl(h, s, BORDER_LIGHTNESS[mode]), BORDER_ALPHA[mode]),
                "label": argb(label),
                "icon": argb(icon),
                "solid": argb(solid),
                "onSolid": argb(on_solid),
            }
            report.append((
                name, mode,
                worst_contrast(label, effective), worst_contrast(icon, effective),
                contrast(on_solid, solid), label != icon,
            ))
    return palette, report


def button_glass_report() -> list[tuple[str, str, float]]:
    """
    Headroom check for the glass overlay on the opaque button fills.

    Buttons reuse the Material scheme rather than the badge palette, so their on-colours are
    already fixed. The only thing glass changes is the top edge, where a white highlight lightens
    the fill — harmless under dark text, and exactly where white text loses contrast. Only that
    case is at risk, so only that case is reported.
    """
    pairs = [
        ("light primary", (0x0F, 0x4C, 0x81), WHITE),
        ("light destructive", (0xB3, 0x26, 0x1E), WHITE),
        ("light secondary", (0xDC, 0xE3, 0xEA), (0x14, 0x1B, 0x22)),
        ("dark primary", (0xA5, 0xC8, 0xE8), (0x00, 0x2F, 0x52)),
        ("dark destructive", (0xF2, 0xB8, 0xB5), (0x7F, 0x1D, 0x1D)),
        ("dark secondary", (0x3D, 0x48, 0x55), (0xDC, 0xE3, 0xEA)),
    ]
    out = []
    for name, fill, on_color in pairs:
        mode = "light" if name.startswith("light") else "dark"
        peak = BUTTON_HIGHLIGHT_ALPHA[mode]
        out.append((name, "base", contrast(on_color, fill)))
        out.append((name, "top", contrast(on_color, composite(WHITE, fill, peak))))
        # Hover brightens the highlight, so the hovered top edge — not the resting one — is the
        # real worst case, and it is the one that has to clear the minimum.
        out.append((
            name, "hover",
            contrast(on_color, composite(WHITE, fill, min(peak * BUTTON_HOVER_LIFT, 1.0))),
        ))
    return out


# Deliberately weaker than the badge highlight. A badge fill is translucent, so its highlight is
# competing with whatever shows through; a button fill is opaque brand colour, and the same 0.22
# would wash a light-theme primary far enough to cost white text its 4.5:1.
BUTTON_HIGHLIGHT_ALPHA = {"light": 0.14, "dark": 0.10}
BUTTON_SHEEN = 1.0

# Multiplier applied to the highlight while a pointer is hovering.
BUTTON_HOVER_LIFT = 1.35

# The icon button's ring is achromatic, so its highlight is not spent against a contrast budget.
# Dark is near zero because white over an already-pale fill lightens the fill instead of reading as
# an edge, which showed up on device as a milky bloom rather than glass.
# Zero on dark: a white wash over a ring on a near-black page lightens the ring rather than reading
# as light on an edge, and no nonzero value was subtle rather than simply absent. See the doc on the
# generated constant.
RING_HIGHLIGHT_ALPHA = {"light": 0.16, "dark": 0.0}

# Cards and attachment rows. Separate from the ring pair because a wash across a 32dp disc recolours
# the object while the same wash along a wide panel's top edge reads correctly as a lit edge.
SURFACE_HIGHLIGHT_ALPHA = {"light": 0.16, "dark": 0.05}

# Contact shadow under any glass surface. Roughly triple on dark for the same visual weight, because
# the dark surfaces start at #121212 and leave a shadow very little room to register.
SHADOW_ALPHA = {"light": 0.16, "dark": 0.48}


BADGE_COLORS_TEMPLATE = '''package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Hue families a badge can be tinted with.
 *
 * These are colour names rather than domain states, and that is deliberate: `:core:designsystem`
 * has no business knowing what "awaiting QA sign-off" means. Mapping a `WorkStatus`, a `Severity`
 * or an audit action onto a tone is `:shared`'s job, in `ui/status/StatusTokens.kt`.
 */
enum class OrbitBadgeTone {{
{tones}
}}

/**
 * The shades one badge needs, in one place.
 *
 * [label] and [icon] are separate colours and never equal — the icon sits a step further from the
 * fill than the text, which reads as intent rather than as a flat monochrome pill.
 *
 * [container] and [border] carry their own alpha. That translucency *is* the glass effect: the
 * card underneath tints through the pill instead of the pill sitting on top of it as an opaque
 * sticker. The cost is that contrast now depends on what is behind the badge, so every shade here
 * was verified against the fill composited over the most elevated surface in its theme
 * (`#E8E8E8` light, `#2A2A2A` dark) — the least favourable background a badge can land on.
 * `BadgeContrastTest` re-checks it on every build.
 *
 * Generated by `tools/gen_designsystem.py`. Do not edit by hand; the numbers are tuner output.
 */
@Immutable
data class OrbitBadgeColors(
    val container: Color,
    val border: Color,
    val label: Color,
    val icon: Color,
    /** Opaque fill for `OrbitBadgeEmphasis.Solid`, where a badge has to shout. */
    val solidContainer: Color,
    val onSolidContainer: Color,
)

internal val OrbitLightBadgeColors: Map<OrbitBadgeTone, OrbitBadgeColors> = mapOf(
{light}
)

internal val OrbitDarkBadgeColors: Map<OrbitBadgeTone, OrbitBadgeColors> = mapOf(
{dark}
)

val OrbitBadgeTone.colors: OrbitBadgeColors
    @Composable @ReadOnlyComposable get() =
        (if (OrbitTheme.isDark) OrbitDarkBadgeColors else OrbitLightBadgeColors).getValue(this)

/**
 * The alphas the glass stack is drawn with.
 *
 * These live here, next to the palette, because they are not free parameters: the label and icon
 * shades above were tuned against exactly these values. Raising [Sheen] or a highlight without
 * regenerating the palette moves the background out from under text that was measured against it,
 * and `BadgeContrastTest` reproduces this stack precisely so that mistake fails the build.
 */
object OrbitGlass {{
    /** Alpha multiplier applied to the fill along the top edge, fading to 1.0 at the bottom. */
    const val Sheen = {sheen}f

    /** Peak alpha of the white highlight at the top edge of a badge. */
    const val BadgeHighlightLight = {badge_highlight_light}f
    const val BadgeHighlightDark = {badge_highlight_dark}f

    /**
     * Peak alpha of the white highlight on a button.
     *
     * Lower than the badge values on purpose. A badge fill is translucent so its highlight competes
     * with whatever shows through; a button fill is opaque brand colour, and the badge figure would
     * wash a light-theme primary far enough to cost white label text its 4.5:1.
     */
    const val ButtonHighlightLight = {button_highlight_light}f
    const val ButtonHighlightDark = {button_highlight_dark}f

    /**
     * Multiplier applied to a button's highlight while a pointer hovers it.
     *
     * The hovered top edge, not the resting one, is the brightest a button ever gets, so this is
     * part of the contrast budget: at this value white label text on a light-theme primary still
     * clears the minimum. It is verified alongside the palette.
     */
    const val ButtonHoverLift = {button_hover_lift}f

    /**
     * Peak alpha of the white highlight on an icon button's ring.
     *
     * Modest on light, and **zero** on dark.
     *
     * Piling a white highlight onto the near-white fill of a light-theme ring buys little and risks
     * blowing the top edge out, so it is kept small there. On dark it is not merely useless but
     * actively wrong, and it took three attempts to accept that trimming it was not the answer.
     *
     * A highlight is meant to read as light glancing off a raised edge. That only works when it is
     * brighter than the material *and* the material is brighter than what is behind it. On a
     * near-black page neither holds: white added to the ring simply lightens the ring, and the
     * result was a milky bloom across the top of every circle — an effect the eye reads as a
     * rendering artefact, not as material. Any nonzero value produced a visible film; there was no
     * amount that was subtle rather than absent.
     *
     * On dark the glass read is carried entirely by the rim and the contact shadow, which are
     * position and material cues rather than a wash, and by the ring's fill now deepening rather
     * than lightening — see `ringContainer`.
     *
     * Neither figure is spent against a contrast budget the way the badge and button values are,
     * since the ring carries no text.
     */
    const val RingHighlightLight = {ring_highlight_light}f
    const val RingHighlightDark = {ring_highlight_dark}f

    /**
     * Peak alpha of the white highlight on a large glass *surface* — cards and attachment rows.
     *
     * Split from the ring pair above, which it used to share. The two look like the same value and
     * are not the same problem: a ring is a 32dp disc whose fill is most of its area, so a wash
     * across it recolours the whole object, while a card is a wide panel where the same wash falls
     * as a gradient along a long top edge and reads as the edge catching light. Sharing one constant
     * meant that fixing the disc would have flattened every card on dark, which is a change nobody
     * asked for and would have been made silently.
     */
    const val SurfaceHighlightLight = {surface_highlight_light}f
    const val SurfaceHighlightDark = {surface_highlight_dark}f

    /**
     * Alpha of the black contact shadow under a glass surface, per theme.
     *
     * Roughly triple on dark for the same visual weight. The dark surfaces start at `#121212` rather
     * than pure black, so there is headroom underneath them, but not much, and a shadow has to be far
     * deeper in alpha to register across it. See `Modifier.orbitGlassShadow` for why the answer is
     * never a lighter shadow.
     */
    const val ShadowLight = {shadow_light}f
    const val ShadowDark = {shadow_dark}f

    /** How far the hairline edge fades from top to bottom, which is what gives the pill depth. */
    const val EdgeFade = {edge_fade}f
}}
'''


def emit_palette(palette: dict) -> None:
    def entries(mode: str) -> str:
        rows = []
        for name, _, _ in TONES:
            d = palette[name][mode]
            rows.append(
                f"    OrbitBadgeTone.{name} to OrbitBadgeColors(\n"
                f"        container = Color({d['container']}),\n"
                f"        border = Color({d['border']}),\n"
                f"        label = Color({d['label']}),\n"
                f"        icon = Color({d['icon']}),\n"
                f"        solidContainer = Color({d['solid']}),\n"
                f"        onSolidContainer = Color({d['onSolid']}),\n"
                f"    ),"
            )
        return "\n".join(rows)

    names = ", ".join(name for name, _, _ in TONES)
    target = DS / "theme/BadgeColors.kt"
    target.write_text(
        BADGE_COLORS_TEMPLATE.format(
            tones=f"    {names},",
            light=entries("light"),
            dark=entries("dark"),
            sheen=SHEEN,
            badge_highlight_light=HIGHLIGHT_ALPHA["light"],
            badge_highlight_dark=HIGHLIGHT_ALPHA["dark"],
            button_highlight_light=BUTTON_HIGHLIGHT_ALPHA["light"],
            button_highlight_dark=BUTTON_HIGHLIGHT_ALPHA["dark"],
            button_hover_lift=BUTTON_HOVER_LIFT,
            ring_highlight_light=RING_HIGHLIGHT_ALPHA["light"],
            ring_highlight_dark=RING_HIGHLIGHT_ALPHA["dark"],
            surface_highlight_light=SURFACE_HIGHLIGHT_ALPHA["light"],
            surface_highlight_dark=SURFACE_HIGHLIGHT_ALPHA["dark"],
            shadow_light=SHADOW_ALPHA["light"],
            shadow_dark=SHADOW_ALPHA["dark"],
            edge_fade=EDGE_FADE,
        ),
        newline="\n",
    )
    print(f"wrote {target.relative_to(ROOT)} ({len(TONES)} tones)")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--offline", action="store_true",
                        help="reuse tools/hugeicons.json instead of fetching")
    args = parser.parse_args()

    print("icons:")
    emit_icons(collect_icons(args.offline))

    palette, report = build_palette()
    print(f"\npalette:\n  {'tone':8} {'mode':6} {'label':>6} {'icon':>6} {'solid':>6}  distinct")
    failures = 0
    for name, mode, label_r, icon_r, solid_r, distinct in report:
        bad = min(label_r, icon_r, solid_r) < TEXT_CONTRAST or not distinct
        failures += bad
        flag = "   <-- FAIL" if bad else ""
        print(f"  {name:8} {mode:6} {label_r:6.2f} {icon_r:6.2f} {solid_r:6.2f} "
              f" {str(distinct):5}{flag}")
    if failures:
        print(f"\n{failures} tone/mode combinations fail the contrast minimum", file=sys.stderr)
        return 1

    print("\nbutton glass headroom (on-colour against the glassed fill):")
    for name, edge, ratio in button_glass_report():
        bad = ratio < TEXT_CONTRAST
        failures += bad
        print(f"  {name:20} {edge:5} {ratio:6.2f}" + ("   <-- FAIL" if bad else ""))
    if failures:
        print("\nbutton glass highlight is too strong", file=sys.stderr)
        return 1

    emit_palette(palette)
    print(f"\nall {len(report)} tone/mode combinations clear {TEXT_CONTRAST}:1")
    return 0


if __name__ == "__main__":
    sys.exit(main())
