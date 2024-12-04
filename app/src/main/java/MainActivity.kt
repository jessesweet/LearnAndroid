package com.learnandroid.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.learnandroid.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val channelId = "toggle_notification_channel"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create the Notification Channel
        createNotificationChannel()

        // Messages corresponding to each toggle
        val messages = listOf(
            "Option One Message",
            "Option Two Message",
            "Option Three Message",
            "Option Four Message",
            "Option Five Message"
        )

        binding.btnActivate.setOnClickListener {
            // Build the pool of messages based on toggles
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                } else {
                    val messagePool = mutableListOf<String>()
                    if (binding.switchOptionOne.isChecked) messagePool.add(messages[0])
                    if (binding.switchOptionTwo.isChecked) messagePool.add(messages[1])
                    if (binding.switchOptionThree.isChecked) messagePool.add(messages[2])
                    if (binding.switchOptionFour.isChecked) messagePool.add(messages[3])
                    if (binding.switchOptionFive.isChecked) messagePool.add(messages[4])

                    // Ensure the pool is not empty
                    if (messagePool.isEmpty()) {
                        Toast.makeText(
                            this,
                            "Please enable at least one toggle!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@setOnClickListener
                    }

                    // Pick a random message and show notification
                    val randomMessage = messagePool.random()
                    showNotification(randomMessage)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Toggle Notification"
            val descriptionText = "Channel for toggle notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(message: String) {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Random Message")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        with(NotificationManagerCompat.from(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}