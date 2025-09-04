package com.cit.mycomposeapplication;


import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;



/**
 * Class to overwrite application class
 */
class QuranApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var appContext: Context
            private set

        @JvmStatic
        lateinit var cw: ContextWrapper

    }


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        cw = ContextWrapper(applicationContext)

    }
}