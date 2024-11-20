package app.ocr_backend.household

import app.ocr_backend.household.dto.HouseholdUserDto
import app.ocr_backend.household.household_user.HouseholdUser

fun HouseholdUser.toDto()=HouseholdUserDto(
    id = this.id,
    name = this.user.name,
    email = this.user.email,
    isAdmin = this.isAdmin
)

fun List<HouseholdUser>.toDto(): List<HouseholdUserDto> {
    val out = mutableListOf<HouseholdUserDto>()
    for(item in this){
        out.add(item.toDto())
    }
    return out
}