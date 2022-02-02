package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 1
    private  var url:String? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var filename : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        // create channel for our notification
        createChannel(getString(R.string.download_channel_id),getString(R.string.download_channel_name))

        custom_button.setOnClickListener {
            if(url != null){

                //here the user pressed on radio button we know that by check url
                download()
            }else Toast.makeText(this,"Select a file to Download",Toast.LENGTH_LONG).show()

        }
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener{_,checkedId ->
            val selectedButton = findViewById<RadioButton>(checkedId)
            filename = selectedButton.text.toString()
            when(checkedId){
                // set url based on the radio button that user pressed
                R.id.load_app_repository -> {
                    url = LOAD_APP_URL
                }
                R.id.retrofit_http -> url = RETROFIT_URL
                R.id.glide_image -> url = GLIDE_URL
            }

        }
    }
// receiver for our download manager
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query().setFilterById(id!!)
            val cursor = downloadManager.query(query)
            cursor.moveToFirst()
            // here the download finished and we set our button state to completed
            custom_button.buttonState = ButtonState.Completed
            // get download manger status
            val status = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.download_success)
                else -> getString(R.string.download_failed)
            }

            notificationManager.sendNotification(getString(R.string.message),context!!,filename,status)

        }
    }


    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }



    private fun createChannel(channelId: String, channelName: String){

        //Channels are available from level 26 API and above
        //We use a channel Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,
                channelName,
                //LOW priority makes no sound
                //DEFAULT makes a sound
                //HIGH Makes a sound and appears as a heads-up notification
                NotificationManager.IMPORTANCE_HIGH)
                //Disable badges for this Channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)
            //Register the channel with the System. You cannot change the importance
            // or notification behaviours after this
            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"


    }
    override fun onResume() {
        super.onResume()
        // START THE RECEIVER
        registerReceiver(receiver,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    override fun onDestroy() {
        super.onDestroy()
        // WE MUST CLOSE THE RECEIVER THAT PREVENT MEMORY leak
        unregisterReceiver(receiver)

    }
}

