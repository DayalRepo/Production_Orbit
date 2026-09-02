package com.orbitai.erp.ui.component.dropdown

/**
 * Stage names from the project's work sequence flow chart.
 *
 * Transcribed from the source document rather than invented, which matters for one specific reason:
 * this is the vocabulary the dropdown was designed against, and it is what makes the design
 * decisions in [com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField] testable.
 * Around a hundred entries is why the list has a pinned search box; names like "Internal Other Area
 * Plastering (Staircase except Lift Door Wall)" are why the rows truncate with an overflow fade
 * instead of wrapping; and the fact that the distinguishing word is usually in the middle is why the
 * filter is a substring match rather than a prefix one. Demo data of six short words would have
 * hidden all three.
 *
 * The wording is left exactly as the site uses it, including the inconsistent capitalisation and the
 * parenthetical qualifiers. Tidying it here would mean the names in the app no longer match the
 * names on the printed sequence taped up in the site office, which is the document people actually
 * reconcile against.
 *
 * This lives in `:shared` and not in the design system. It is project content, and a component
 * library that ships one customer's stage list is not a library.
 */
object WorkSequence {

    /** Excavation up through the frame. */
    val structure = listOf(
        "Mass Excavation",
        "Shoring & PCC",
        "Raft/Footing Reinforcement",
        "Raft/Footing Shuttering",
        "Raft Concreting & Foundation Column",
        "Raft/Footing Waterproofing",
        "Backfilling & Compaction",
        "PCC",
        "Column Reinforcement, Shuttering and Concreting",
        "Beam Bottom & Slab Shuttering and Bottom Reinforcement",
        "Slab Bottom Reinforcement",
        "Slab Conduiting/Sleeves",
        "Slab Top Reinforcement",
        "Slab Concreting",
        "Staircase Shuttering, Reinforcement and Concreting",
    )

    /** Everything inside a unit, in the order it has to happen. */
    val unitInternal = listOf(
        "Electrical Conduit Drop Opening & GI Wire Pulling",
        "Block Work",
        "Plastering Bull Fixing",
        "Electrical Box Fixing & Wall Conduiting",
        "Toilet Rough Plastering",
        "Electrical & PHE Works",
        "Internal Other Area Plastering Bore Packing",
        "Water Proofing with Screed",
        "High Level Plumbing Works",
        "Toilet Dado Tiling",
        "Fire Sprinkler Works",
        "Flooring with Suitable Protection",
        "Inside Cornice Works",
        "Temporary Main Door",
        "UPVC Windows/Sliding Doors Fixing",
        "2 Coats - Internal Wall Putty",
        "Internal Wiring",
        "Internal Door & Shaft Door Fixing (With LDPE Protection)",
        "1st Coat Painting",
        "Switch Plate Fixing",
        "Flooring Protection Removal",
        "Tile Cleaning with Acid Wash",
        "Main Door Fixing",
        "Toilet Tile Grouting",
        "CP & Sanitary Fittings",
        "Tile Grouting (other than Toilet)",
        "Final Coat Painting",
        "Deep Cleaning",
        "Wooden Flooring",
        "Handing Over",
    )

    /** The elevation, from plastering to the final coat. */
    val unitExternal = listOf(
        "Entire Floor External Plastering - Elevation wise",
        "External Plastering (Band to Band)",
        "MS Railing Works",
        "External Primer with Texture & 1 Coat Paint",
        "External Final Coat Painting",
        "Terrace Water Proofing",
        "Lighting Arrestors",
        "Flooring Protection Removal (Top Floor to Bottom Floor)",
    )

    /** Lobbies, shafts, staircases and the lift core. */
    val commonArea = listOf(
        "Shaft Internal Plastering (Respective Floor)",
        "FAPA Box Fixing & Conduiting",
        "Lobby Area Plastering",
        "Internal Other Area Plastering (Staircase except Lift Door Wall)",
        "Staircase MS Railing",
        "Common Area - Fire Sprinkler Works",
        "SS Railing & Toilet False Ceiling",
        "Lift Lobby Flooring & Protection",
        "LMR - Lift Centre Line Marking",
        "Lift Door Frame and Call Boxes Fixing",
        "Lift Lobby Cladding",
        "Lift Lobby Wiring",
        "Lift Lobby Falseceiling",
        "Lift Lobby 1st Coat Painting (Top Floor to Bottom Floor)",
        "Fire Sprinkler and Electrical Fixtures (Top Floor to Bottom Floor)",
        "Lift Lobby Final Coat Painting (Top floor to Bottom Floor)",
    )

    /** Below grade: slab, services, ventilation and parking. */
    val basement = listOf(
        "Drain and Subsoil with Water Proofing",
        "Grade Slab",
        "Electrical Box Fixing and Wall Conduiting",
        "Wall Plastering",
        "Ceiling Joints Grinding",
        "Surface Conduiting",
        "Ceiling Whitewash",
        "MEP Works (High Level & Low Level)",
        "Shaft Internal Wet Works (if any)",
        "Cabling/DG Exhaust",
        "VDF",
        "Painting - Column, Wall & PHE Lines",
        "Panel Erection & Electrical Fixtures",
        "Basement Ventilation",
        "Column Guards & Signages",
        "Driveway & Parking Markings",
        "Expansion Joints Sealing",
    )

    /** Podium, landscape, roads and external services. */
    val externalDevelopment = listOf(
        "Podium Drain Sleeve & Bore Packing",
        "Expansion Joint Treatment",
        "Podium Street Light & Planter Box Foundations",
        "Earth Strips/Cable Sleeves with Guide Wires",
        "Soil Filling for Landscaping",
        "Kerb Stone Fixing",
        "External Cabling",
        "Compaction and Paved Block Laying",
        "Fire Hydrant Works",
        "Podium/Swimming Pool Waterproofing and Screed",
        "Landscape PHE Works",
        "External Light Fixtures & Plantation Works",
        "Services Hume Pipe & Chamber Construction",
        "Excavation/Levelling",
        "GSB",
        "Wet Mix Macadam",
        "Bituminous / CC Roads",
    )

    /**
     * Every stage, in sequence order.
     *
     * Flat rather than grouped, because the search box is what makes this findable and search does
     * not care about groups. Deduplicated: several stages — "Backfilling & Compaction", "Block Work"
     * — appear under more than one heading on the chart, and a dropdown offering the same string
     * twice is a dropdown the user cannot tell apart.
     */
    val allStages: List<String> = (
        structure + unitInternal + unitExternal + commonArea + basement + externalDevelopment
        ).distinct()
}

/**
 * Materials as a site names them, with the grade included wherever the grade is what gets ordered.
 *
 * "Cement" alone is not orderable; "Cement (OPC 53)" is. The dropdown exists to produce a line on a
 * requisition, so the entries have to be specific enough to be one.
 */
val ConstructionUnits = listOf(
    "Nos",
    "Kg",
    "Tonnes",
    "Metres",
    "Square Metres",
    "Cubic Metres",
    "Litres",
    "Bags",
    "Sheets",
    "Rolls",
    "Bundles",
    "Pairs",
    "Sets",
    "Hours",
    "Days",
)

val ConstructionMaterials = listOf(
    "Cement (OPC 53)",
    "Cement (PPC)",
    "Reinforcement Steel Fe500D",
    "Structural Steel",
    "Coarse Aggregate 20mm",
    "Coarse Aggregate 12mm",
    "River Sand",
    "M-Sand",
    "Ready-Mix Concrete M25",
    "Ready-Mix Concrete M30",
    "AAC Blocks",
    "Solid Concrete Blocks",
    "Waterproofing Membrane",
    "Binding Wire",
    "Shuttering Ply",
    "PVC Conduit 25mm",
    "CPVC Pipe 25mm",
    "Vitrified Tiles 600x600",
    "Wall Putty",
    "Primer",
)
