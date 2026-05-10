package com.ssm.study.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QuestionEntity::class, AttemptEntity::class, QuestionFlagEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SsmDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        fun create(context: Context): SsmDatabase = Room.databaseBuilder(
            context.applicationContext,
            SsmDatabase::class.java,
            "ssm-study.db"
        ).build()
    }
}
