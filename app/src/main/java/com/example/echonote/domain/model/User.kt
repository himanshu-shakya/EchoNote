    package com.example.echonote.domain.model

    import com.google.firebase.firestore.QuerySnapshot

    data class User(
        val userID:String="",
        val name:String="",
        val email:String="",
        val avatar:String=""
    )
    fun QuerySnapshot.toUser():User{
        val user = User()
        if (!this.isEmpty) {
            val document = this.documents[0] // Assuming you expect only one document per userID
            val userID = document.getString("userID") ?: ""
            val name = document.getString("name") ?: ""
            val email = document.getString("email") ?: ""
            val avatar = document.getString("avatar") ?: ""
            return User(userID = userID, name = name, email = email, avatar = avatar)
        }
        return user

    }