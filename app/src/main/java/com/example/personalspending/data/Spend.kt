package com.example.personalspending.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(tableName = "Spend",
    foreignKeys = [
        ForeignKey(entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"]
        ),
        ForeignKey(entity = Area::class,
            parentColumns = ["id"],
            childColumns = ["areaId"]
        )
    ]
)
data class Spend(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val accountId: Int,
    val areaId: Int,
    val money: Double,
    val date: Date,
    @NonNull
    val note: String,
    @NonNull
    val type: Int,
    val img: String
)

@Serializable
data class ImageTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String,
)