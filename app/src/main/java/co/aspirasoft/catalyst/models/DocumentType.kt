package co.aspirasoft.catalyst.models

import kotlin.reflect.KClass

sealed class DocumentType(val name: String) {

    val headings = ArrayList<String>()

    companion object {
        @JvmStatic
        fun values(): Array<KClass<out DocumentType>> {
            return arrayOf(
                    Feasibility::class,
                    SRS::class,
                    SDS::class,
                    UserManual::class
            )
        }
    }

}

class Feasibility : DocumentType("Feasibility Report") {
    init {
        headings.addAll(arrayOf(
                "1. Executive Summary",
                "2. Problem Statement",
                "3. Project Business Requirement",
                "4. Assessment of Options",
                "5. Risk Assessment of Viable Options",
                "6. Recommended Options for Further Analysis"
        ))
    }
}

class SRS : DocumentType("Requirements Specification") {
    init {
        headings.addAll(arrayOf(
                "1. Introduction",
                "1.1. Purpose",
                "1.2. Document Conventions",
                "1.3. Intended Audience and Reading Suggestions",
                "1.4. Product Scope",
                "1.5. References",

                "2. Overall Description",
                "2.1. Product Perspective",
                "2.2. Product Features",
                "2.3. User Classes and Characteristics",
                "2.4. Operating Environment",
                "2.5. Design and Implementation Constraints",
                "2.6. User Documentation",
                "2.7. Assumptions and Dependencies",

                "3. External Interface Requirements",
                "3.1. User Interfaces",
                "3.2. Hardware Interfaces",
                "3.3. Software Interfaces",
                "3.4. Communication Interfaces",

                "4. System Features",

                "5. Other Nonfunctional Requirements",
                "5.1. Performance Requirements",
                "5.2. Safety Requirements",
                "5.3. Security Requirements",
                "5.4. Software Quality Attributes",
                "5.5. Business Rules",

                "Appendix A: Glossary",
                "Appendix B: Analysis Models",
                "Appendix C: To Be Determined List"
        ))
    }
}

class SDS : DocumentType("Design Specification") {
    init {
        headings.addAll(arrayOf(
                "Window type (Casement/awning)",
                "Window configuration (Single, double, multi etc)",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height of W (O/A + other)",
                "Width of W (O/A + other)",
                "Frame Depth",
                "Sill Depth",
                "Sash detail (profile, section sizes)",
                "Sashes swing in or out",
                "Active sash on double sets",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Hardware system (Truth, hinged, standard friction etc)",
                "Motorised winder system",
                "Hardware finish/ colour",
                "Locking requirements (drop-bolts for double casement etc)",
                "Flyscreen requirements/mesh type",
                "Flyscreen pelmet detail",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        ))
    }
}

class UserManual : DocumentType("User Manual") {
    init {
        headings.addAll(arrayOf(
                "Door/Window Configuration",
                "Timber type (Jamb)",
                "Timber type (Sill)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Door/ Sash Profile",
                "Top-rail & Stile Section size",
                "Bottom-rail Section size",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Balance type",
                "Track Finish (if CW)",
                "Locking Hardware & Finish",
                "Flyscreen",
                "Flyscreen mesh type",
                "Sash horns",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        ))
    }
}