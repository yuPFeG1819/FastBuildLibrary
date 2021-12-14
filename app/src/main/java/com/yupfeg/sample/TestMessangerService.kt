package com.yupfeg.sample

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Messenger

/**
 *
 * @author yuPFeG
 * @date
 */
class TestMessengerService : Service(){

    private val mMessenger = Messenger(object : Handler(Looper.getMainLooper()){

    })

    private val mTestServiceBinder = object : TestServiceAIDL.Stub(){
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun getNewTestName(): String {
            TODO("Not yet implemented")
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mTestServiceBinder
    }
}