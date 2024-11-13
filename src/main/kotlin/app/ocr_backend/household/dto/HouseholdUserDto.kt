package app.ocr_backend.household.dto

import java.util.*

data class HouseholdUserDto(
    val id: UUID,
    val name:String,
    val email:String,
    val isAdmin:Boolean)