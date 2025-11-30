package com.appmovil.inventorywidget.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val createAt: LocalDate = LocalDate.now()
)
