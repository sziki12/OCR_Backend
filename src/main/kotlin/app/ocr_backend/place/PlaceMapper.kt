package app.ocr_backend.place

import app.ocr_backend.place.dto.PlaceCreateRequest
import app.ocr_backend.place.dto.PlaceResponse
import app.ocr_backend.place.dto.ReceiptResponsePlace
import app.ocr_backend.receipt.Receipt
import app.ocr_backend.receipt.toResponse

fun Place.toResponse() = PlaceResponse(
    id = this.id,
    name = this.name,
    lat = this.lat,
    lng = this.lng,
    receipts = this.receipts.map { it.toResponse() },
    isValidated = this.isValidated,
)

fun Place.toReceiptResponse() = ReceiptResponsePlace(
    id = this.id,
    name = this.name,
    lat = this.lat,
    lng = this.lng,
    isValidated = this.isValidated,
)

fun ReceiptResponsePlace.toPlace(receipts: List<Receipt>) = Place(
    name = this.name,
    lat = this.lat,
    lng = this.lng,
).also {
    it.id = this.id
    it.receipts.addAll(receipts)
}

fun PlaceCreateRequest.toPlace() = Place(
    name = this.name,
    lat = this.lat,
    lng = this.lng,
)