package com.demo.wakeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import android.content.Context.POWER_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.content.Context.SENSOR_SERVICE
import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.util.Log


class MainActivity : AppCompatActivity() {

    private var mSensorManager: SensorManager? = null
    private var mPowerManager: PowerManager? = null
    private var mGravitySensor: Sensor? = null
    private var mWakelock: PowerManager.WakeLock? = null
    private var shakeTime: Long = 0 // 手机触发拿起动作时间
    private var showTime: Long = 0
    private var oldY = 0f
    private var subY = 0f

    // 加速度传感器监听
    private val mSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            smartAwake(event)
        }

        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mPowerManager = getSystemService(Activity.POWER_SERVICE) as PowerManager
        mGravitySensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mWakelock = mPowerManager!!.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "smartAwake"
        )

        mSensorManager!!.registerListener(
            mSensorEventListener,
            mGravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        //        mWakelock.acquire();

    }

    // 抬手亮屏逻辑
    private fun smartAwake(event: SensorEvent) {
        val values = event.values
        val x = values[0]
        val y = values[1]
        val z = values[2]
        Log.i(TAG, "smartAwake  x= $x  y= $y z=$z")
        subY = y - oldY
        if (Math.abs(x) < 3 && y > 0 && z < 9) {
            if (subY > 1) {
                shakeTime = System.currentTimeMillis()
                Log.i(TAG, "1")
            }
            oldY = y
        }

        if (Math.abs(x) < 3 && y > 4 && y < 9 && z > 2 && z < 9) {
            showTime = System.currentTimeMillis()
            Log.i(TAG, "2")
            if (showTime - shakeTime >= 0 && showTime - shakeTime < 200) {
                mWakelock!!.acquire()
                mWakelock!!.release()
                startActivity(Intent(this,Main2Activity::class.java))
//                mPowerManager.wakeUp(SystemClock.uptimeMillis(), "smartAwake")
                Log.i(TAG, "smartAwake  Awake")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager!!.unregisterListener(mSensorEventListener)
    }

    companion object {

        private val TAG = "ansen"
    }
    
}
