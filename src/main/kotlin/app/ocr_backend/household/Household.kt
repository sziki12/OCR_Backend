package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.item.Item
import app.ocr_backend.receipt.Receipt
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "Household")
data class Household(var name: String) {
    @Column(name = "household_id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @OneToMany(mappedBy = "household")
    var householdUsers: MutableSet<HouseholdUser> = mutableSetOf()

    @OneToMany(mappedBy = "household")
    var receipts: MutableList<Receipt> = mutableListOf()
}
