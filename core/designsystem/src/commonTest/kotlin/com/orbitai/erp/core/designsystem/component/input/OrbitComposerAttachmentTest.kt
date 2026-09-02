package com.orbitai.erp.core.designsystem.component.input

import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Composer attachment strip contracts: identity for remove, and sizing that keeps the strip denser
 * than a full attachment list.
 */
class OrbitComposerAttachmentTest {

    @Test
    fun `attachments are identified by id not filename`() {
        val a = OrbitComposerAttachment(
            id = "1",
            fileName = "drawing.pdf",
            fileSize = "2.0 MB",
            leading = OrbitAttachmentLeading.Glyph,
        )
        val b = OrbitComposerAttachment(
            id = "2",
            fileName = "drawing.pdf",
            fileSize = "2.0 MB",
            leading = OrbitAttachmentLeading.Glyph,
        )
        assertTrue(a.id != b.id)
        assertEquals("drawing.pdf", a.fileName)
    }

    @Test
    fun `composer thumb tokens stay at or below attachment row height`() {
        // Rows replaced square thumbs, but the tokens remain for any residual chrome and must not
        // grow past a single attachment row or the strip will dominate the composer.
        OrbitPlatform.entries.forEach { platform ->
            val sizing = platformTokens(platform).sizing
            assertTrue(
                sizing.composerThumbSize <= sizing.attachmentRowHeightReadOnly,
                "$platform composer thumb taller than a read-only attachment row",
            )
            assertTrue(
                sizing.composerThumbRemove < sizing.composerThumbSize,
                "$platform remove badge should stay smaller than the thumb it sits on",
            )
        }
    }
}
