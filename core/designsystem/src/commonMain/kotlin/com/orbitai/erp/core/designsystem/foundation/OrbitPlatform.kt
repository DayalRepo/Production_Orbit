package com.orbitai.erp.core.designsystem.foundation

enum class OrbitPlatform { Android, Ios }

/**
 * The design system's only `expect` declaration, and deliberately so.
 *
 * Android and iOS genuinely diverge on type scale, text colour, icon and avatar sizing, minimum
 * touch target and top-bar title alignment. Rather than an `expect`/`actual` pair per token — none
 * of which could be compiled or tested from a Windows or Linux host, since the Apple targets are
 * unavailable there — every token set lives in `commonMain` keyed off this value. Both platforms'
 * tokens are therefore verifiable in ordinary host tests, and the platform-specific code is one
 * line per target.
 */
internal expect val currentPlatform: OrbitPlatform
