package com.example.personalspending.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "Notify",
    foreignKeys = [
        ForeignKey(entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ]
)

data class Notify(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userId: Int,
    val name: String,
    val quantityRemind: Int,
    val auto: Boolean = false,
    val startDate: Date,
    val currentDate: Date,
    @NonNull
    val note: String,
)