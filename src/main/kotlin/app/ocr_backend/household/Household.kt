package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.place.Place
import app.ocr_backend.receipt.Receipt
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "Household")
data class Household(var name: String) {
    @Column(name = "household_id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @JsonIgnore
    @OneToMany(mappedBy = "household")
    var householdUsers: MutableSet<HouseholdUser> = mutableSetOf()

    @JsonIgnore
    @OneToMany(mappedBy = "household")
    var receipts: MutableList<Receipt> = mutableListOf()

    @JsonIgnore
    @OneToMany(mappedBy = "household")
    var places: MutableList<Place> = mutableListOf()
}
