package app.ocr_backend.user

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.security.refresh_token.RefreshToken
import jakarta.persistence.*

@Entity
@Table(name = "App_User")
data class User(
    @Column(name = "name")
    var name: String,
    var email: String,
    var password: String,
    var salt: String
) {
    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1

    @OneToMany(mappedBy = "user")
    var refreshTokens: MutableList<RefreshToken> = mutableListOf()

    @OneToMany(mappedBy = "user")
    var householdUsers: MutableList<HouseholdUser> = mutableListOf()

    var isEmailConfirmed: Boolean = false
}