package com.ssm.study

import android.app.Application
import com.ssm.study.data.SsmDatabase
import com.ssm.study.data.SsmRepository

class SsmStudyApplication : Application() {
    val database by lazy { SsmDatabase.create(this) }
    val repository by lazy { SsmRepository(database.questionDao()) }
}
