package com.shofyou.android

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val btn = findViewById<Button>(R.id.btnRetry)

        btn.setOnClickListener {
            if (isConnected()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}
