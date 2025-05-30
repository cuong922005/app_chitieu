package com.example.personalspending.repository

import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SpendingRepository {

    fun getAllUser(): Flow<List<User>>

    fun getUserById(id: Int): Flow<User?>

    fun getUserByEmail(email: String): Flow<User>

    fun getAreaByType(userId: Int, type: Int): Flow<Area>

    fun getAreaByUser(userId: Int): Flow<List<Area>>

    fun getAccount(id: Int): Flow<Account?>

    fun getAccountByUser(userId: Int): Flow<List<Account>>

    fun getSpend(accountId: Int): Flow<List<Spend>>

    fun getSpendById(id: Int): Flow<Spend?>

    fun getSpendByArea(idArea: Int, date: Date): Flow<Spend>

    fun getSpendByPlan(areaId: Int, dateStart: Date, dateEnd: Date, accountId: Int): Flow<List<Spend>>

    fun getPayByAccount(accountId: Int): Flow<List<AutoPay>>

    fun getNotifyByAccount(userId: Int): Flow<List<Notify>>

    fun getPlanByAccount(accountId: Int): Flow<List<Planed>>

    suspend fun insertUser(user: User)

    suspend fun insertAccount(account: Account)

    suspend fun insertArea(area: Area)

    suspend fun insertSpend(spend: Spend)

    suspend fun insertPay(pay: AutoPay)

    suspend fun insertNotify(notify: Notify)

    suspend fun insertPlan(plan: Planed)

    suspend fun updateAccountById(id: Int, money: Double)

    suspend fun updateAccount(account: Account)

    suspend fun updateSpend(id: Int, money: Double)

    suspend fun updateSpend(spend: Spend)

    suspend fun updatePay(pay: AutoPay)

    suspend fun updateNotify(notify: Notify)

    suspend fun updatePlan(plan: Planed)

    suspend fun updatePayStatus(id: Int, status: Boolean)

    suspend fun updatePayDate(id: Int, currentDate: Date)

    suspend fun updateNotifyStatus(id: Int, status: Boolean)

    suspend fun updateNotifyDate(id: Int, currentDate: Date)

    suspend fun deleteAccount(id: Int)

    suspend fun deleteSpend(id: Int)

    suspend fun deleteAllUser()

    suspend fun deleteAllAccount()

    suspend fun deletePay(id: Int)

    suspend fun deleteNotify(id: Int)

    suspend fun deletePlan(id: Int)

}