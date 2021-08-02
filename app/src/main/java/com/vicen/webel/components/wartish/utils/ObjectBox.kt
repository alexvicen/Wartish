package com.vicen.webel.components.wartish.utils

import android.content.Context
import com.vicen.webel.components.wartish.entidades.MyObjectBox
import io.objectbox.BoxStore


class ObjectBox {

    lateinit var boxStore: BoxStore

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context)
            .build()
    }

    companion object {
        val instance: ObjectBox by lazy { ObjectBox() }
    }

}