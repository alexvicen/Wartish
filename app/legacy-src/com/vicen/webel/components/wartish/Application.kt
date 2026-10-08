package com.vicen.webel.components.wartish


import android.app.Application
import com.vicen.webel.components.wartish.utils.ObjectBox

class Application : Application() {


    override fun onCreate() {
        super.onCreate()
        ObjectBox.instance.init(applicationContext)
    }
}
