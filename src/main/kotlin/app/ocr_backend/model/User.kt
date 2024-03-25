package app.ocr_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "User")
data class User(
    val name:String,
    val login:String,
    val token:String,
    )
{
    @Column(name="user_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1
}