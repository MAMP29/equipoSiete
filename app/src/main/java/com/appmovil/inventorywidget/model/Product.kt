package com.appmovil.inventorywidget.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

data class Product(
    @DocumentId
    val id: Int = 0,

    val code: Int = 0,

    val name: String = "",

    val price: Int = 0,

    val quantity: Int = 0
)
