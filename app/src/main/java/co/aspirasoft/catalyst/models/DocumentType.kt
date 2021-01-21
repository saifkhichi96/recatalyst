package co.aspirasoft.catalyst.models

class DocumentType {
    var name: String = ""
    var standardId: String = ""
    var standardName: String = ""
    var abstract: String = ""
    var keywords: String = ""
    val sections = ArrayList<DocumentSection>()
}