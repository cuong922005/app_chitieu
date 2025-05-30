package com.example.personalspending.repository

import com.example.personalspending.dao.SpendingDao
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

class OfflineSpendingRepository(private val spendingDao: SpendingDao): SpendingRepository {

    override suspend fun insertUser(user: User) = spendingDao.insertUser(user = user)

    override suspend fun insertAccount(account: Account) = spendingDao.insertAccount(account = account)

    override suspend fun insertArea(area: Area) = spendingDao.insertArea(area = area)

    override suspend fun insertSpend(spend: Spend) = spendingDao.insertSpend(spend)

    override suspend fun insertPay(pay: AutoPay) = spendingDao.insertPay(pay)

    override suspend fun insertNotify(notify: Notify) =  spendingDao.insertNotify(notify)

    override suspend fun insertPlan(plan: Planed)  = spendingDao.insertPlan(plan)

    override suspend fun updateAccountById(id: Int, money: Double) = spendingDao.updateAccountById(id, money)

    override suspend fun updateAccount(account: Account) = spendingDao.updateAccount(account)

    override suspend fun updateSpend(id: Int, money: Double) = spendingDao.updateSpend(id, money)

    override suspend fun updateSpend(spend: Spend) = spendingDao.updateSpend(spend)

    override suspend fun updatePay(pay: AutoPay) = spendingDao.updatePay(pay)

    override suspend fun updateNotify(notify: Notify) = spendingDao.updateNotify(notify)

    override suspend fun updatePlan(plan: Planed) = spendingDao.updatePlan(plan)

    override suspend fun updatePayStatus(id: Int, status: Boolean) = spendingDao.updatePayStatus(id, status)

    override suspend fun updatePayDate(id: Int, currentDate: Date) = spendingDao.updatePayDate(id, currentDate)

    override suspend fun updateNotifyStatus(id: Int, status: Boolean) =  spendingDao.updateNotifyStatus(id, status)

    override suspend fun updateNotifyDate(id: Int, currentDate: Date) = spendingDao.updateNotifyDate(id, currentDate)

    override fun getUserById(id: Int): Flow<User>  = spendingDao.getUserById(id)

    override fun getAccount(id: Int): Flow<Account?> = spendingDao.getAccount(id)

    override fun getAccountByUser(userId: Int): Flow<List<Account>> = spendingDao.getAccountByUser(userId)

    override fun getUserByEmail(email: String): Flow<User> = spendingDao.getUserByEmail(email)

    override fun getAreaByType(userId: Int, type: Int): Flow<Area> = spendingDao.getAreaByType(userId, type)

    override fun getAreaByUser(userId: Int): Flow<List<Area>> = spendingDao.getAreaByUser(userId = userId)

    override fun getAllUser(): Flow<List<User>> = spendingDao.getAllUser()

    override fun getSpendById(id: Int): Flow<Spend?> = spendingDao.getSpendById(id)

    override fun getSpend(accountId: Int): Flow<List<Spend>> = spendingDao.getSpend(accountId)

    override fun getSpendByArea(idArea: Int, date: Date): Flow<Spend> = spendingDao.getSpendByArea(idArea, date)

    override fun getSpendByPlan(
        areaId: Int,
        dateStart: Date,
        dateEnd: Date,
        accountId: Int
    ): Flow<List<Spend>> = spendingDao.getSpendByPlan(areaId, dateStart, dateEnd, accountId)

    override fun getPayByAccount(accountId: Int): Flow<List<AutoPay>> = spendingDao.getPayByAccount(accountId)

    override fun getNotifyByAccount(userId: Int): Flow<List<Notify>> = spendingDao.getNotifyByAccount(userId)

    override fun getPlanByAccount(accountId: Int): Flow<List<Planed>> = spendingDao.getPlanByAccount(accountId)

    override suspend fun deleteAccount(id: Int) = spendingDao.deleteAccount(id)

    override suspend fun deleteSpend(id: Int) = spendingDao.deleteSpend(id)

    override suspend fun deleteAllUser() = spendingDao.deleteAllUser()

    override suspend fun deleteAllAccount() = spendingDao.deleteAllAccount()

    override suspend fun deletePay(id: Int) = spendingDao.deletePay(id)

    override suspend fun deleteNotify(id: Int) = spendingDao.deleteNotify(id)

    override suspend fun deletePlan(id: Int) = spendingDao.deletePlan(id)

}