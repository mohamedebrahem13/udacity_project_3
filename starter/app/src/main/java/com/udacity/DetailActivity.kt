package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(applicationContext,
            NotificationManager::class.java)
        //Cancels the Notification when we press when we start the activity
        notificationManager?.cancelNotification()
        // get extras from intent
        val intent = intent.extras!!
        val fileName = intent.getString("filename")
        val status = intent.getString("status")
        // set extras to text views
     findViewById<TextView>(R.id.file_name).text = fileName
     findViewById<TextView>(R.id.download_status).text =status
        findViewById<Button>(R.id.button_ok).setOnClickListener{
            //close the activity and remove from stack
            finish()
        }
    }

}
