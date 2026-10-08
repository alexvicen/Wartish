package com.vicen.webel.components.wartish

import android.app.Application
import com.vicen.webel.components.wartish.data.GameDatabase

class WartishApplication : Application() {
    val database by lazy { GameDatabase.get(this) }
}
