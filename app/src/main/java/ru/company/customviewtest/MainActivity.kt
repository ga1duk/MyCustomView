package ru.company.customviewtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statsView = findViewById<StatsView>(R.id.cv_stats)
        statsView.data = listOf(
            0.25F,
            0.25F,
            0.25F,
            0.25F
        )
        with(statsView) {
            setOnClickListener {
                data = listOf(
                    0.25F,
                    0.25F,
                    0.25F,
                    0.25F
                )
            }
        }
    }
}