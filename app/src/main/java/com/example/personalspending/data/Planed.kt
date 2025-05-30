package com.example.personalspending.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Planed",
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
data class Planed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val accountId: Int,
    val areaId: Int,
    val money: Double,
    val startDate: Date,
    val endDate: Date,
    val type: Int = 0
)