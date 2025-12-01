package com.appmovil.inventorywidget.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @DocumentId
    val id: String = "",

    val code: Int = 0,

    val name: String = "",

    val price: Int = 0,

    val quantity: Int = 0
) : Parcelable
