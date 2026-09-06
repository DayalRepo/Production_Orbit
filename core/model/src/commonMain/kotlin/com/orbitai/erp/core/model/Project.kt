package com.orbitai.erp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The two project kinds OrbitAI supports in v1.
 *
 * CEO access is organisation-wide and ignores this. Every other role is assigned to one or more
 * concrete projects of these types.
 */
@Serializable
enum class ProjectType {
    @SerialName("villas")
    Villas,

    /** High-rise apartments, towers, and gated community / township buildings. */
    @SerialName("apartment_community")
    ApartmentCommunity,
    ;

    val displayName: String
        get() = when (this) {
            Villas -> "Villas"
            ApartmentCommunity -> "Apartment / Community"
        }
}

@Serializable
data class Project(
    val id: String,
    val name: String,
    val type: ProjectType,
    val organisationId: String,
    val location: String? = null,
)
