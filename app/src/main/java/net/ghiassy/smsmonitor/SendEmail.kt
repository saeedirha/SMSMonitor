package net.ghiassy.smsmonitor

import java.util.Properties
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun sendEmail(receiverEmail: String,msgSubject: String, msgContent: String) {
    val properties = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.*****.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
            return javax.mail.PasswordAuthentication("sms-monitor@*****.com", "******")
        }
    })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("sms-monitor@hexor64.com"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail))
            subject = msgSubject
            setText(msgContent)
        }

        Transport.send(message)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}