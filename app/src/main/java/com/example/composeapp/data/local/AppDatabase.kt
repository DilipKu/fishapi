package com.example.composeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.composeapp.data.local.dao.FisheryDao
import com.example.composeapp.data.local.entity.*

@Database(
    entities = [
        HunterEntity::class,
        FishCatchEntity::class,
        SaleEntity::class,
        ExpenseEntity::class,
        FishCategoryEntity::class,
        ExpenseCategoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fisheryDao(): FisheryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fishery_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
