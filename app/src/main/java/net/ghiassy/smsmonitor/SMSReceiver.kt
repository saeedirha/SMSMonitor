package net.ghiassy.smsmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class SMSReceiver : BroadcastReceiver() {

    private val TAG = "SMSReceiver---------->"


    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG, "called!!!!!!!!!!")

        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            val pdus = bundle?.get("pdus") as Array<*>?
            var message: String? = ""
            var sender :String? = "unknown"
            pdus?.let {
                for (pdu in pdus) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val messageBody = smsMessage.messageBody
                    sender = smsMessage.originatingAddress
                    // Handle the received SMS message here
                    message += messageBody
                }
                Log.d(TAG, "SMS Received: From $sender - Message: $message")

                CoroutineScope(Dispatchers.IO).launch{
                    val mySingleton = MySingleton.getInstance()
                    Log.d(TAG,"Sending email to : ${mySingleton.email}")

                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedDateTime = currentDateTime.format(formatter)

                    val subject = "Date: $formattedDateTime From: $sender"
                    message?.let {
                     sendEmail(mySingleton.email, subject, it)
                    }

                }

            }
        }
    }
}