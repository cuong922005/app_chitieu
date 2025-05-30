package com.example.personalspending.data

import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.personalspending.R


@Entity(tableName = "Area",
    foreignKeys = [ForeignKey(entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"])
    ]
)
data class Area(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @NonNull
    val userId: Int,
    @NonNull
    val name: String,
    @NonNull
    @DrawableRes val icon: Int,
    val color: Int,
    @NonNull
    val type: Int
)

data class ListColor(
    val id: Int,
    val color: Color
)

data class ListColorChart(
    val id: Int,
    val color: Int
)

val listColorChart = listOf<ListColorChart>(
    ListColorChart(
        id = 1,
        color = Color(119, 139, 133).toArgb()
    ),
    ListColorChart(
        id = 2,
        color = Color(72, 61, 139).toArgb()    // DarkSlateBlue
    ),
    ListColorChart(
        id = 3,
        color = Color(255, 255, 0).toArgb()    // Yellow
    ),
    ListColorChart(
        id = 4,
        color = Color(0, 255, 0).toArgb()      // Green
    ),
    ListColorChart(
        id = 5,
        color = Color(0, 0, 255).toArgb()      // Blue
    ),
    ListColorChart(
        id = 6,
        color = Color(169, 169, 169).toArgb()  // DarkGray
    ),
    ListColorChart(
        id = 7,
        color = Color(0, 255, 255).toArgb()    // Cyan
    ),
    ListColorChart(
        id = 8,
        color = Color(128, 128, 128).toArgb()  // Gray
    ),
    ListColorChart(
        id = 9,
        color = Color(255, 0, 255).toArgb()    // Magenta
    ),
    ListColorChart(
        id = 10,
        color = Color(211, 211, 211).toArgb()  // LightGray
    ),
    ListColorChart(
        id = 11,
        color = Color(255, 0, 0).toArgb()      // Red
    ),
    ListColorChart(
        id = 12,
        color = Color(255, 165, 0).toArgb()    // Orange
    ),
    ListColorChart(
        id = 13,
        color = Color(128, 0, 128).toArgb()    // Purple
    ),
    ListColorChart(
        id = 14,
        color = Color(255, 192, 203).toArgb()  // Pink
    ),
    ListColorChart(
        id = 15,
        color = Color(0, 128, 128).toArgb()    // Teal
    ),
    ListColorChart(
        id = 16,
        color = Color(75, 0, 130).toArgb()     // Indigo
    ),
    ListColorChart(
        id = 17,
        color = Color(255, 69, 0).toArgb()     // OrangeRed
    ),
    ListColorChart(
        id = 18,
        color = Color(240, 230, 140).toArgb()  // Khaki
    ),
    ListColorChart(
        id = 19,
        color = Color(173, 216, 230).toArgb()  // LightBlue
    ),
    ListColorChart(
        id = 20,
        color = Color(60, 179, 113).toArgb()   // MediumSeaGreen
    ),
    ListColorChart(
        id = 21,
        color = Color(123, 104, 238).toArgb()  // MediumSlateBlue
    ),
    ListColorChart(
        id = 22,
        color = Color(192, 192, 192).toArgb()  // Silver
    ),
    ListColorChart(
        id = 23,
        color = Color(255, 20, 147).toArgb()   // DeepPink
    ),
    ListColorChart(
        id = 24,
        color = Color(32, 178, 170).toArgb()   // LightSeaGreen
    ),
    ListColorChart(
        id = 25,
        color = Color(47, 79, 79).toArgb()     // DarkSlateGray
    ),
    ListColorChart(
        id = 26,
        color = Color(245, 245, 220).toArgb()  // Beige
    ),
    ListColorChart(
        id = 27,
        color = Color(255, 228, 181).toArgb()  // Moccasin
    ),
    ListColorChart(
        id = 28,
        color = Color(255, 215, 0).toArgb()    // Gold
    ),
    ListColorChart(
        id = 29,
        color = Color(250, 128, 114).toArgb()  // Salmon
    ),
    ListColorChart(
        id = 30,
        color = Color(0, 100, 0).toArgb()      // DarkGreen
    ),
    ListColorChart(
        id = 31,
        color = Color(70, 130, 180).toArgb()   // SteelBlue
    ),
    ListColorChart(
        id = 32,
        color = Color(106, 90, 205).toArgb()   // SlateBlue
    ),
    ListColorChart(
        id = 33,
        color = Color(244, 164, 96).toArgb()   // SandyBrown
    ),
    ListColorChart(
        id = 34,
        color = Color(64, 224, 208).toArgb()  // Turquoise
    ),
)

val listColor = listOf(
    ListColor(
        id = 1,
        color = Color(119, 139, 133)
    ),
    ListColor(
        id = 2,
        color = Color(72, 61, 139)    // DarkSlateBlue
    ),
    ListColor(
        id = 3,
        color = Color(255, 255, 0)    // Yellow
    ),
    ListColor(
        id = 4,
        color = Color(0, 255, 0)      // Green
    ),
    ListColor(
        id = 5,
        color = Color(0, 0, 255)      // Blue
    ),
    ListColor(
        id = 6,
        color = Color(169, 169, 169)  // DarkGray
    ),
    ListColor(
        id = 7,
        color = Color(0, 255, 255)    // Cyan
    ),
    ListColor(
        id = 8,
        color = Color(128, 128, 128)  // Gray
    ),
    ListColor(
        id = 9,
        color = Color(255, 0, 255)    // Magenta
    ),
    ListColor(
        id = 10,
        color = Color(211, 211, 211)  // LightGray
    ),
    ListColor(
        id = 11,
        color = Color(255, 0, 0)      // Red
    ),
    ListColor(
        id = 12,
        color = Color(255, 165, 0)    // Orange
    ),
    ListColor(
        id = 13,
        color = Color(128, 0, 128)    // Purple
    ),
    ListColor(
        id = 14,
        color = Color(255, 192, 203)  // Pink
    ),
    ListColor(
        id = 15,
        color = Color(0, 128, 128)    // Teal
    ),
    ListColor(
        id = 16,
        color = Color(75, 0, 130)     // Indigo
    ),
    ListColor(
        id = 17,
        color = Color(255, 69, 0)     // OrangeRed
    ),
    ListColor(
        id = 18,
        color = Color(240, 230, 140)  // Khaki
    ),
    ListColor(
        id = 19,
        color = Color(173, 216, 230)  // LightBlue
    ),
    ListColor(
        id = 20,
        color = Color(60, 179, 113)   // MediumSeaGreen
    ),
    ListColor(
        id = 21,
        color = Color(123, 104, 238)  // MediumSlateBlue
    ),
    // Các màu bổ sung
    ListColor(
        id = 22,
        color = Color(192, 192, 192)  // Silver
    ),
    ListColor(
        id = 23,
        color = Color(255, 20, 147)   // DeepPink
    ),
    ListColor(
        id = 24,
        color = Color(32, 178, 170)   // LightSeaGreen
    ),
    ListColor(
        id = 25,
        color = Color(47, 79, 79)     // DarkSlateGray
    ),
    ListColor(
        id = 26,
        color = Color(245, 245, 220)  // Beige
    ),
    ListColor(
        id = 27,
        color = Color(255, 228, 181)  // Moccasin
    ),
    ListColor(
        id = 28,
        color = Color(255, 215, 0)    // Gold
    ),
    ListColor(
        id = 29,
        color = Color(250, 128, 114)  // Salmon
    ),
    ListColor(
        id = 30,
        color = Color(0, 100, 0)      // DarkGreen
    ),
    ListColor(
        id = 31,
        color = Color(70, 130, 180)   // SteelBlue
    ),
    ListColor(
        id = 32,
        color = Color(106, 90, 205)   // SlateBlue
    ),
    ListColor(
        id = 33,
        color = Color(244, 164, 96)   // SandyBrown
    ),
    ListColor(
        id = 34,
        color = Color(64, 224, 208)   // Turquoise
    ),
)

data class ItemIcon(
    val icon: Int = 0,
    val name: String = "",
    val color: Int = 0
)

object AreaList {
    val listAccountIcon = listOf<ItemIcon>(
        ItemIcon(
            icon = R.drawable.money_bill_1_regular,
        ),
        ItemIcon(
            icon = R.drawable.building_columns_solid,
        ),
        ItemIcon(
            icon = R.drawable.bitcoin_sign_solid,
        ),
        ItemIcon(
            icon = R.drawable.credit_card_regular,
        ),
        ItemIcon(
            icon = R.drawable.cc_paypal,
        ),
        ItemIcon(
            icon = R.drawable.wallet_solid,
        ),
        ItemIcon(
            icon = R.drawable.piggy_bank_solid,
        ),
        ItemIcon(
            icon = R.drawable.graduation_cap_solid,
        )
    )

    val listDefaultIcon = listOf<ItemIcon>(
        ItemIcon(
            icon = R.drawable.heart_pulse_solid,
            name = "Sức khỏe",
            color = Color(255, 0, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.wallet_solid,
            name = "Giải trí",
            color = Color(60, 179, 113).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.people_roof_solid,
            name = "Gia đình",
            color = Color(255, 0, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.mug_saucer_solid,
            name = "Cafe",
            color = Color(255, 255, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.graduation_cap_solid,
            name = "Giáo dục",
            color = Color(255, 20, 147).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.gift_solid,
            name = "Quà tặng",
            color = Color(255, 69, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.dumbbell_solid,
            name = "Thể dục",
            color = Color(70, 130, 180).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.utensils_solid,
            name = "Ăn uống",
            color = Color(60, 179, 113).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.bus_solid,
            name = "Di chuyển",
            color = Color(60, 179, 113).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.question_solid,
            name = "Khác",
            color = Color(255, 0, 0).toArgb()
        )
    )

    val listDefaultIconIncome = listOf<ItemIcon>(
        ItemIcon(
            icon = R.drawable.money_bill_1_regular,
            name = "Lương",
            color = Color(255, 0, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.piggy_bank_solid,
            name = "Tiết kiệm",
            color = Color(60, 179, 113).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.building_columns_solid,
            name = "Đầu tư",
            color = Color(255, 255, 0).toArgb()
        ),
        ItemIcon(
            icon = R.drawable.question_solid,
            name = "Khác",
            color = Color(255, 0, 0).toArgb()
        )
    )

    val listAllIcon = listOf<ItemIcon>(
        ItemIcon(
            icon = R.drawable.money_bill_1_regular,
        ),
        ItemIcon(
            icon = R.drawable.building_columns_solid,
        ),
        ItemIcon(
            icon = R.drawable.bitcoin_sign_solid,
        ),
        ItemIcon(
            icon = R.drawable.credit_card_regular,
        ),
        ItemIcon(
            icon = R.drawable.cc_paypal,
        ),
        ItemIcon(
            icon = R.drawable.wallet_solid,
        ),
        ItemIcon(
            icon = R.drawable.piggy_bank_solid,
        ),
        ItemIcon(
            icon = R.drawable.heart_pulse_solid,
        ),
        ItemIcon(
            icon = R.drawable.hands_holding_child_solid,
        ),
        ItemIcon(
            icon = R.drawable.people_roof_solid,
        ),
        ItemIcon(
            icon = R.drawable.mug_saucer_solid,
        ),
        ItemIcon(
            icon = R.drawable.graduation_cap_solid,
        ),
        ItemIcon(
            icon = R.drawable.gift_solid,
        ),
        ItemIcon(
            icon = R.drawable.dumbbell_solid,
        ),
        ItemIcon(
            icon = R.drawable.utensils_solid,
        ),
        ItemIcon(
            icon = R.drawable.bus_solid,
        ),
        ItemIcon(
            icon = R.drawable.futbol_solid,
        ),
        ItemIcon(
            icon = R.drawable.volleyball_solid,
        ),
        ItemIcon(
            icon = R.drawable.book_solid,
        ),
        ItemIcon(
            icon = R.drawable.kitchen_set_solid,
        ),
        ItemIcon(
            icon = R.drawable.church_solid,
        ),
        ItemIcon(
            icon = R.drawable.mobile_screen_solid,
        ),
        ItemIcon(
            icon = R.drawable.smoking_solid,
        ),
        ItemIcon(
            icon = R.drawable.laptop_code_solid,
        ),
        ItemIcon(
            icon = R.drawable.plane_solid,
        ),
        ItemIcon(
            icon = R.drawable.palette_solid,
        ),
        ItemIcon(
            icon = R.drawable.parachute_box_solid,
        ),
        ItemIcon(
            icon = R.drawable.school_solid,
        ),
        ItemIcon(
            icon = R.drawable.question_solid,
        )
    )
}
