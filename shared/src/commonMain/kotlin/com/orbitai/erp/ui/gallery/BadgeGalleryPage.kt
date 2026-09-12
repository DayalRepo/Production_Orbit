package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.badge.OrbitRoleBadge
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.ui.component.badge.BadgeKind
import com.orbitai.erp.ui.component.badge.SeverityBadge
import com.orbitai.erp.ui.component.badge.StatusBadge

/** Badges: status catalogue, severity, sizes, and role short-form chips. */
@Composable
internal fun BadgeGalleryPage() {
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxl)) {
        GallerySection("Badges") {
            GalleryFlow {
                BadgeKind.entries.forEach { StatusBadge(kind = it) }
            }
        }

        GallerySection("Severity") {
            GalleryFlow {
                Severity.entries.forEach { SeverityBadge(severity = it) }
            }
        }

        GallerySection("Badge size") {
            GalleryFlow {
                OrbitBadgeSize.entries.forEach { size ->
                    StatusBadge(kind = BadgeKind.InProgress, label = size.name, size = size)
                }
            }
        }

        GallerySection("Role badges") {
            GalleryFlow {
                UserRole.entries.forEach { role ->
                    OrbitRoleBadge(label = role.shortLabel)
                }
            }
        }
    }
}
