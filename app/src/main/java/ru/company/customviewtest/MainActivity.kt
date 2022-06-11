package ru.company.customviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<StatsView>(R.id.cv_stats).data = listOf(
            100F,
            200F,
            300F,
            500F,
            50F,
            150F
        )
    }
}