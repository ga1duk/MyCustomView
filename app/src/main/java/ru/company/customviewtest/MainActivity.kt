package ru.company.customviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<StatsView>(R.id.cv_stats).data = listOf(
            0.25F,
            0.25F,
            0.25F,
            0.25F
        )
    }
}