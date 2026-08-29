package com.orbitai.erp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The seven personas OrbitAI is built for. The serial names are the stable wire format and must
 * match the role values stored in the backend — rename the constant, never the [SerialName].
 */
@Serializable
enum class UserRole {
    @SerialName("ceo")
    Ceo,

    @SerialName("project_manager")
    ProjectManager,

    @SerialName("site_engineer")
    SiteEngineer,

    @SerialName("contractor")
    Contractor,

    @SerialName("qa_qc")
    QaQc,

    @SerialName("warehouse_manager")
    WarehouseManager,

    @SerialName("procurement_manager")
    ProcurementManager,
    ;

    /** Short label for chips and avatars. */
    val shortLabel: String
        get() = when (this) {
            Ceo -> "CEO"
            ProjectManager -> "PM"
            SiteEngineer -> "Engineer"
            Contractor -> "Contractor"
            QaQc -> "QA/QC"
            WarehouseManager -> "Warehouse"
            ProcurementManager -> "Procurement"
        }

    /** Full label for profile screens and role pickers. */
    val displayName: String
        get() = when (this) {
            Ceo -> "Chief Executive Officer"
            ProjectManager -> "Project Manager"
            SiteEngineer -> "Site Engineer"
            Contractor -> "Contractor"
            QaQc -> "QA/QC Inspector"
            WarehouseManager -> "Warehouse Manager"
            ProcurementManager -> "Procurement Manager"
        }

    /**
     * Whether this role sees company-wide data rather than only the projects it is assigned to.
     * Drives dashboard scope and list filtering defaults.
     */
    val hasOrganisationWideScope: Boolean
        get() = this == Ceo
}
