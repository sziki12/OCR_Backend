package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.item.Item
import app.ocr_backend.receipt.Receipt
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "Household")
class Household() {
    @Column(name = "household_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    lateinit var id: UUID

    @OneToMany(mappedBy = "household")
    var householdUser:MutableSet<HouseholdUser> = mutableSetOf()

    @OneToMany(mappedBy = "household")
    var receipts:MutableList<Receipt> = mutableListOf()
}
