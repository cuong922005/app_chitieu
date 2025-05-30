package com.example.personalspending.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Converters
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User

@Database(
    entities = [User::class, Account::class, Area::class, Spend::class, AutoPay::class, Notify::class, Planed::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpendingDatabase: RoomDatabase() {
    abstract fun spendingDao(): SpendingDao

    companion object {
        @Volatile                                                                                   // Khi ghi vào thuộc tính này sẽ hiển thị ngay lập tức với các luồng khác
        private var Instance: SpendingDatabase ?= null                                             // 1 luồng thay đổi vs 'Íntance' thì các luồng khcas cx thay đổi

        fun getDatabase(context: Context): SpendingDatabase {                                      // Tạo DB
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SpendingDatabase::class.java, "personal_spending_test") // Tên DB
//                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { Instance = it }                                                         // Giu lai thong tin tham chieu den phien ban the hien db moi tao gan day
            }
        }

        private val MIGRATION_2_3 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Xóa bảng cũ
//                database.execSQL("DROP TABLE Planed")
//                database.execSQL("DELETE FROM Account")
        //        database.execSQL("DELETE FROM User")

                // Tạo bảng mới với cấu trúc mới
//                database.execSQL("""
//                    CREATE TABLE Planed_new (
//                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                        accountId INTEGER NOT NULL,
//                        money REAL,
//                        areaId INTEGER NOT NULL,
//                        startDate INTEGER NOT NULL,
//                        endDate INTEGER NOT NULL,
//                        type INTEGER NOT NULL DEFAULT 0,
//                        FOREIGN KEY(accountId) REFERENCES Account(id) ON DELETE CASCADE,
//                        FOREIGN KEY(areaId) REFERENCES Area(id) ON DELETE CASCADE
//                    )
//                """.trimIndent())
//
//                database.execSQL("""
//                    INSERT INTO Account_new (id, userId, name, money)
//                    SELECT id, userId, name, money
//                    FROM Account
//                """)
//
//                // Xóa bảng cũ
//                database.execSQL("DROP TABLE Account")

//                database.execSQL("ALTER TABLE notify ADD COLUMN currentDate INTEGER NOT NULL DEFAULT 0")

                // Đổi tên bảng mới thành tên bảng cũ
//                database.execSQL("ALTER TABLE Planed_new RENAME TO Planed")

                database.execSQL("CREATE TABLE Planed_new ("
                        + "id INTEGER PRIMARY KEY NOT NULL, "
                        + "accountId INTEGER NOT NULL, "
                        + "money REAL NOT NULL, "
                        + "areaId INTEGER NOT NULL, "
                        + "startDate INTEGER NOT NULL, "
                        + "endDate INTEGER NOT NULL, "
                        + "type INTEGER NOT NULL DEFAULT 0, "
                        + "FOREIGN KEY(areaId) REFERENCES Area(id) ON DELETE NO ACTION, "
                        + "FOREIGN KEY(accountId) REFERENCES Account(id) ON DELETE NO ACTION)");

                // Chuyển dữ liệu từ bảng cũ sang bảng mới
//                database.execSQL("INSERT INTO Planed_new (id, accountId, money, areaId, startDate, endDate, type) "
//                        + "SELECT id, accountId, money, areaId, startDate, endDate, type FROM Planed");

                // Xóa bảng cũ
                database.execSQL("DROP TABLE Planed");

                // Đổi tên bảng mới thành tên bảng cũ
                database.execSQL("ALTER TABLE Planed_new RENAME TO Planed");
            }
        }
    }
}