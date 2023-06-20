package net.ghiassy.smsmonitor

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class SMSService : Service() {

    private var smsReceiver: SMSReceiver? = null
    private val TAG = "SMS Service"
    override fun onCreate() {
        super.onCreate()
        smsReceiver = SMSReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "...... Started")
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filter)
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "...... Stopped")
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
