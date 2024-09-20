package app.ocr_backend.household.household_user

import app.ocr_backend.household.Household
import app.ocr_backend.user.User
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "Household_User")
data class HouseholdUser(
    @Column(name = "is_admin")
    var isAdmin: Boolean
) {
    @Column(name = "household_user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @ManyToOne
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @ManyToOne
    @JoinColumn(name = "household_id")
    lateinit var household: Household
}
