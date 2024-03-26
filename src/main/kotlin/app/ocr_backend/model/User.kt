package app.ocr_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "App_User")
data class User(
    @Column(name="user_name")
    var userName:String,
    var password:String,
    )
{
    @Column(name="user_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1
}