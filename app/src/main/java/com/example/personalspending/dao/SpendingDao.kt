package com.example.personalspending.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SpendingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)                                                 // Bo qua xung dot
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)                                                 // Bo qua xung dot
    suspend fun insertAccount(account: Account)

    @Insert(onConflict = OnConflictStrategy.IGNORE)                                                 // Bo qua xung dot
    suspend fun insertSpend(spend: Spend)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArea(area: Area)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPay(pay: AutoPay)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotify(notify: Notify)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlan(plan: Planed)

    @Update
    suspend fun updateAccount(account: Account)

    @Update
    suspend fun updateSpend(spend: Spend)

    @Update
    suspend fun updatePay(pay: AutoPay)

    @Update
    suspend fun updateNotify(notify: Notify)

    @Update
    suspend fun updatePlan(plan: Planed)

    @Query("UPDATE account SET money = :money WHERE id = :id")
    suspend fun updateAccountById(id: Int, money: Double)

    @Query("UPDATE spend SET money = :money WHERE id = :id")
    suspend fun updateSpend(id: Int, money: Double)

    @Query("UPDATE Auto_pay SET auto = :status WHERE id = :id")
    suspend fun updatePayStatus(id: Int, status: Boolean)

    @Query("UPDATE Auto_pay SET currentDate = :currentDate WHERE id = :id")
    suspend fun updatePayDate(id: Int, currentDate: Date)

    @Query("UPDATE notify SET auto = :status WHERE id = :id")
    suspend fun updateNotifyStatus(id: Int, status: Boolean)

    @Query("UPDATE notify SET currentDate = :currentDate WHERE id = :id")
    suspend fun updateNotifyDate(id: Int, currentDate: Date)

    @Query("SELECT * from user WHERE id = :id")
    fun getUserById(id: Int): Flow<User>

    @Query("SELECT * from user WHERE email = :email")
    fun getUserByEmail(email: String): Flow<User>

    @Query("SELECT * from user")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * from account WHERE id = :id")
    fun getAccount(id: Int): Flow<Account?>

    @Query("SELECT * FROM account WHERE userId = :userId")
    fun getAccountByUser(userId: Int): Flow<List<Account>>

    @Query("SELECT * from area WHERE userId = :userId AND type = :type")
    fun getAreaByType(userId: Int, type: Int): Flow<Area>

    @Query("SELECT * from area WHERE userId = :userId")
    fun getAreaByUser(userId: Int): Flow<List<Area>>

    @Query("SELECT * from spend WHERE id = :id")
    fun getSpendById(id: Int): Flow<Spend?>

    @Query("SELECT * from spend WHERE accountId = :accountId")
    fun getSpend(accountId: Int): Flow<List<Spend>>

    @Query("SELECT * from spend WHERE areaId = :areaId AND date = :date")
    fun getSpendByArea(areaId: Int, date: Date): Flow<Spend>

    @Query("SELECT * from spend WHERE areaId = :areaId AND date >= :dateStart AND date <= :dateEnd AND accountId = :accountId AND type = 0")
    fun getSpendByPlan(areaId: Int, dateStart: Date, dateEnd: Date, accountId: Int): Flow<List<Spend>>

    @Query("SELECT * from Auto_pay WHERE accountId = :accountId")
    fun getPayByAccount(accountId: Int): Flow<List<AutoPay>>

    @Query("SELECT * from Planed WHERE accountId = :accountId")
    fun getPlanByAccount(accountId: Int): Flow<List<Planed>>

    @Query("SELECT * from notify WHERE userId = :userId")
    fun getNotifyByAccount(userId: Int): Flow<List<Notify>>

    @Query("DELETE FROM account WHERE id = :id")
    suspend fun deleteAccount(id: Int)

    @Query("DELETE FROM spend WHERE id = :id")
    suspend fun deleteSpend(id: Int)

    @Query("DELETE FROM Auto_pay WHERE id = :id")
    suspend fun deletePay(id: Int)

    @Query("DELETE FROM notify WHERE id = :id")
    suspend fun deleteNotify(id: Int)

    @Query("DELETE FROM planed WHERE id = :id")
    suspend fun deletePlan(id: Int)

    @Query("DELETE FROM user")
    fun deleteAllUser()

    @Query("DELETE FROM account")
    fun deleteAllAccount()

}