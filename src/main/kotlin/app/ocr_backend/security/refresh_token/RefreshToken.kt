package app.ocr_backend.security.refresh_token

import app.ocr_backend.user.User
import jakarta.persistence.*

@Entity
data class RefreshToken(
    @Column(columnDefinition = "TEXT")
    val token:String
){

    @Column(name="token_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Long = -1

    @ManyToOne
    @JoinColumn(name="user_id")
    lateinit var user: User
}
