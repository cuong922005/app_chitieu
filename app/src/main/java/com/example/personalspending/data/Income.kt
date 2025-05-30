package com.example.personalspending.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Income",
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
data class Incomes(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val accountId: Int,
    val areaId: Int,
    val money: Double,
    val date: Date,
    val note: String,
)
