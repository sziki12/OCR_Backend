package app.ocr_backend.place.dto

data class PlaceCreateRequest(
    var name: String,
    var description: String,
    var lat: Double,
    var lng: Double
)
