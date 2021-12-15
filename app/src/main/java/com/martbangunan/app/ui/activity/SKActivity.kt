package com.martbangunan.app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.martbangunan.app.R

class SKActivity : AppCompatActivity() {
    private val back: ImageView by lazy {findViewById(R.id.imgBack)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_k)

        back.setOnClickListener {
            finish()
        }
    }
}