package com.example.composeapp.data.local.repository


import com.example.composeapp.data.local.UserEntity
import com.example.composeapp.data.local.dao.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Flow automatically emits new list when DB changes
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun addUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

    fun searchUsers(query: String): Flow<List<UserEntity>> {
        return userDao.searchUsers(query)
    }
}