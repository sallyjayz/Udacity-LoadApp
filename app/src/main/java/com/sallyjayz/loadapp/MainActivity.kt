package com.sallyjayz.loadapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sallyjayz.loadapp.databinding.ActivityMainBinding

/*
class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var toolbar: Toolbar
    private lateinit var custom_button: Button


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button = findViewById(R.id.custom_button)

        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}*/


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var toolbar: Toolbar

    private lateinit var radio_group: RadioGroup
    private lateinit var glide_radio: RadioButton
    private lateinit var loadapp_radio: RadioButton
    private lateinit var retrofit_radio: RadioButton
    private lateinit var custom_button: LoadingButton

    enum class LinksToDownload(val title: Int, val link: String) {
        GLIDE(R.string.glide, "https://github.com/bumptech/glide"),
        LOADAPP(R.string.load_app,"https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"),
        RETROFIT(R.string.retrofit,"https://github.com/square/retrofit")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button = findViewById(R.id.custom_button)
        radio_group = findViewById(R.id.rg_options)
        glide_radio = findViewById(R.id.glide_option)
        loadapp_radio = findViewById(R.id.load_app_option)
        retrofit_radio = findViewById(R.id.option_retrofit)


        custom_button.setOnClickListener {
            createChannel(getString(R.string.channel_id), getString(R.string.notification_channel))

            if (radio_group.checkedRadioButtonId == -1)
                toastMessge()
        }

    }

    private fun toastMessge() {
        Toast.makeText(this,"Please select the file do download",Toast.LENGTH_SHORT).show()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val query = DownloadManager.Query()
                val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val success = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = success == DownloadManager.STATUS_SUCCESSFUL
                    val downloadTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    sendNotificaiton(isSuccess, downloadTitle)
                }
                custom_button.downloadCompleted()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download(linksToDownload: LinksToDownload) {
        custom_button.startDownload()
        val request =
            DownloadManager.Request(Uri.parse(linksToDownload.link))
                .setTitle(getString(linksToDownload.title))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotificaiton(isSuccess: Boolean, title: String) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
            title, isSuccess)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onRadioClicked(view: View) {
        if (view is RadioButton) {
            val isChecked = view.isChecked
            when (view.getId()) {
                R.id.glide_option ->
                    if (isChecked) {
                        download(LinksToDownload.GLIDE)
                    }

                R.id.load_app_option ->
                    if (isChecked) {
                        download(LinksToDownload.LOADAPP)
                    }

                R.id.option_retrofit -> {
                    if (isChecked) {
                        download(LinksToDownload.RETROFIT)
                    }
                }
            }
        }
    }
}



