package com.appmovil.inventorywidget.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val createAt: LocalDate = LocalDate.now()
) : Parcelable
