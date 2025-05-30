package com.example.personalspending.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.sql.Time
import java.time.LocalTime
import java.util.Date

@Entity(tableName = "Auto_pay",
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

data class AutoPay(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val accountId: Int,
    val areaId: Int,
    val name: String,
    val quantityRemind: Int,
    val auto: Boolean = false,
    val startDate: Date,
    @NonNull
    val currentDate: Date,
    val money: Double,
    @NonNull
    val note: String,
)
