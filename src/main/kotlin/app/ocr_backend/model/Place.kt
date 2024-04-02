package app.ocr_backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "Place")
data class Place(
    var name:String,
    var lat:Double,
    var lng:Double)
{
    @Column(name="place_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1

    @OneToMany(mappedBy = "place")
    var receipts:MutableList<Receipt> = mutableListOf()
}