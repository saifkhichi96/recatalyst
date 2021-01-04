package co.aspirasoft.catalyst.models

import kotlin.reflect.KClass

sealed class DocumentType(val name: String) {

    val headings = ArrayList<String>()

    companion object {
        @JvmStatic
        fun values(): List<KClass<out DocumentType>> {
            return DocumentType::class.sealedSubclasses
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
            "1. Introduction",
            "1.1. Purpose",
            "1.2. Scope",
            "1.3. Overview",
            "1.4. References",
            "1.5. Definitions and Acronyms",

            "2. System Overview",

            "3. System Architecture",
            "3.1. Architectural Design",
            "3.2. Decomposition Description",
            "3.3. Design Rationale",

            "4. Data Design",
            "4.1. Data Description",
            "4.2. Data Dictionary",

            "5. Component Design",

            "6. Human Interface Design",
            "6.1. Overview of User Interface",
            "6.2. Screen Mockups",
            "6.3. Screen Objects and Actions",

            "7. Requirements Matrix",

            "8. Appendices",
        ))
    }
}

class UserManual : DocumentType("User Manual") {
    init {
        headings.addAll(arrayOf(
            "1. Overview",
            "1.1. Scope",
            "1.2. Purpose",
            "1.3. Organization of the standard",
            "1.4. Candidate uses",

            "2. Definitions",

            "3. Structure of software user documentation",
            "3.1. Overall structure of documentation",
            "3.2. Initial components",
            "3.3. Placement of critical information",

            "4. Information content of software user documentation",
            "4.1. Completeness of information",
            "4.2. Accuracy of information",
            "4.3. Content of identification data",
            "4.4. Information for use of the documentation",
            "4.5. Concept of operations",
            "4.6. Information for general use of the software",
            "4.7. Information for procedures and tutorials",
            "4.8. Information on software commands",
            "4.9. Information on error messages and problem resolution",
            "4.10. Information on terminology",
            "4.11. Information on related information sources",

            "5. Format of software user documentation",
            "5.1. Consistency of terminology and formats",
            "5.2. Use of printed or electronic formats",
            "5.3. Legibility",
            "5.4. Formats for warnings, cautions, and notes",
            "5.5. Format for instructions",
            "5.6. Formats for representing user interface elements",
            "5.7. Formats for documentation features for accessing information",
            "5.8. Formats for features for navigation",
            "5.9. Formats for illustrations",

            "Appendix A: Bibliography",
            "Appendix B: Indexing software user documentation",
        ))
    }
}