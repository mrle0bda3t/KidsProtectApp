package com.loan555.kisdapplication2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KidsDao {
    @Insert
    suspend fun insertKid(kid: KidsEntity): Long

    @Query("SELECT * from KidsEntity")
    fun getAllKids(): List<KidsEntity>

    @Query("DELETE FROM KidsEntity")
    suspend fun deleteAllKids()
}