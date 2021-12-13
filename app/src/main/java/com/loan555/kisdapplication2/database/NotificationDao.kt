package com.loan555.kisdapplication2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("SELECT * from NotificationEntity ORDER BY time DESC")
    fun getAllNotification(): LiveData<List<NotificationEntity>>

    @Query("DELETE FROM NotificationEntity WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE NotificationEntity SET read= :newRead WHERE id = :id")
    suspend fun updateNotification(newRead: Boolean, id: Long)

    @Query("DELETE FROM NotificationEntity")
    suspend fun deleteAllNotification()

}