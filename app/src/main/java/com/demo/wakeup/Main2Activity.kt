package com.demo.wakeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class Main2Activity : AppCompatActivity() {

    private lateinit var handler:Handler
    private val runnable={
        finish()
    }
    private val timeRunnable=object : Runnable {
        override fun run() {
            tv_time.text="时间:"+Date( System.currentTimeMillis() ).toLocaleString()
            handler.postDelayed(this,1000)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        handler= Handler(Looper.getMainLooper())
        handler.postDelayed(runnable,15000)
        handler.postDelayed(timeRunnable,1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        handler.removeCallbacks(timeRunnable)
    }
}
