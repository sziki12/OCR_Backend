package app.ocr_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "App_User")
data class User(
    @Column("user_name")
    var name:String,
    var login:String,
    var password:String,
    )
{
    @Column(name="user_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1
}