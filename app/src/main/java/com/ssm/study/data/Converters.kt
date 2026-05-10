package com.ssm.study.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTopic(topic: Topic): String = topic.name

    @TypeConverter
    fun toTopic(value: String): Topic = Topic.valueOf(value)
}
