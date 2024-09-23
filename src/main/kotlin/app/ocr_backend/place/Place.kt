package app.ocr_backend.place

import app.ocr_backend.household.Household
import app.ocr_backend.receipt.Receipt
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "Place")
data class Place(
    var name: String,
    var lat: Double,
    var lng: Double
) {
    @Column(name = "place_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1

    @OneToMany(mappedBy = "place")
    var receipts: MutableList<Receipt> = mutableListOf()

    /*@JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    lateinit var user: User*/
    //TODO Replace with household

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "household_id")
    lateinit var household: Household

    @Column(name = "is_validated")
    var isValidated = false
}