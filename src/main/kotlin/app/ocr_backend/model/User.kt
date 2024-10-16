package app.ocr_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "App_User")
data class User(
    @Column(name="user_name")
    var userName:String,
    var password:String,
    var salt:String
    )
{
    @Column(name="user_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1

    @OneToMany(mappedBy = "user")
    lateinit var receipts:MutableList<Receipt>

    @OneToMany(mappedBy = "user")
    var places:MutableList<Place> = mutableListOf()

    @Column(name = "is_admin")
    var isAdmin = false

    override fun toString(): String {
        return "User(id: $id, userName: $userName isAdmin: $isAdmin)"
    }

    fun toDetailedString(): String {
        return "User(id: $id, userName: $userName isAdmin: $isAdmin, places: $places, receipts: $receipts)"
    }
}