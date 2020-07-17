package com.antoniosj.doges.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DogBreed::class], version = 1)
abstract class DogDatabase: RoomDatabase() {

    abstract fun dogDao(): DogDao

    companion object {
        @Volatile // n vai ser compartilhada em outras threads
        private  var instance: DogDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DogDatabase::class.java,
            "dogDatabase"
        ).build()
    }
}