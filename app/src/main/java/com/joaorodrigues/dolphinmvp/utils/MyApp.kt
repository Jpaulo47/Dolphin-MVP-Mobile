package com.joaorodrigues.dolphinmvp.utils

import android.app.Application
/**
 * A classe MyApp é uma subclasse de Application que você pode criar para obter o contexto da
 * aplicação e usá-lo em outros pontos do aplicativo.
 */

class MyApp : Application() {
    companion object {
        @JvmStatic
        private lateinit var instance: MyApp

        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
