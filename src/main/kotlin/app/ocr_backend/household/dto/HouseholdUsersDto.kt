package app.ocr_backend.household.dto

data class HouseholdUsersDto(
    val currentUser: HouseholdUserDto,
    val otherUsers:List<HouseholdUserDto>
)