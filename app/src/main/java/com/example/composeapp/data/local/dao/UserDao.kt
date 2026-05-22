package com.example.composeapp.data.local.dao


import androidx.room.*
import com.example.composeapp.data.local.UserEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // INSERT — add new user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // SELECT ALL — get all users as Flow (auto updates UI!)
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    // SELECT ONE — get single user by id
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    // UPDATE — update existing user
    @Update
    suspend fun updateUser(user: UserEntity)

    // DELETE ONE — delete specific user
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // DELETE ALL — clear table
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // SEARCH — find users by name
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>
}