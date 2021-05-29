package com.sallyjayz.loadapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var file_name: TextView
    private lateinit var ok_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        status = findViewById(R.id.status)
        file_name = findViewById(R.id.file_name)
        ok_button = findViewById(R.id.ok_button)

        if (intent.hasExtra(getString(R.string.success)))
            if (intent.getBooleanExtra(getString(R.string.success), false)) {
                status.text = getString(R.string.success)
            }else {
                status.text = getString(R.string.fail)
            }
        if (intent.hasExtra(getString(R.string.title))) {
            file_name.text = intent.getStringExtra(getString(R.string.title))
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}