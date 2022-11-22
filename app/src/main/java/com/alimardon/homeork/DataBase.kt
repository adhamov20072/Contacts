package com.alimardon.homeork

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 3)
abstract class DataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    object DataBaseBuilder {
        private var instanse: DataBase? = null
        fun getDataBase(context: Context): DataBase? {
            when {
                instanse == null -> synchronized(DataBase::class.java) {
                    instanse = buildDataBase(context)
                }
            }
            return instanse
        }
    fun buildDataBase(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            DataBase::class.java,
            "note"
        ).build()
    }
}