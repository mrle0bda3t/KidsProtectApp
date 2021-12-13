package com.loan555.kisdapplication2.database

import android.app.Application
import com.loan555.kisdapplication2.model.DanhSachTreEm

class Repository(application: Application) {
    private val notificationDao: NotificationDao =
        AppDatabase.getDatabase(application).notificationDao()
    private val kidsDao: KidsDao = AppDatabase.getDatabase(application).kidsDao()

    suspend fun insert(notificationEntity: NotificationEntity) =
        notificationDao.insertNotification(notificationEntity)

    suspend fun deleteNotification(id: Long) = notificationDao.delete(id)

    suspend fun updateNotification(id: Long) = notificationDao.updateNotification(true, id)

    fun getAllNotification() = notificationDao.getAllNotification()

    //Danh sach tre
    suspend fun insertKids(tre: KidsEntity) = kidsDao.insertKid(tre)
    suspend fun deleteListKids() = kidsDao.deleteAllKids()
    fun getAllKids() = kidsDao.getAllKids()

}