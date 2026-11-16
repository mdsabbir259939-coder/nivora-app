package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppConfigEntity::class,
        BannerEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        UserEntity::class,
        OrderEntity::class,
        WithdrawalRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NivoraDatabase : RoomDatabase() {
    abstract fun nivoraDao(): NivoraDao

    companion object {
        @Volatile
        private var INSTANCE: NivoraDatabase? = null

        fun getInstance(context: Context): NivoraDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NivoraDatabase::class.java,
                    "nivora_relational.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
