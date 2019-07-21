package com.android.oleksandrpriadko.demo.loggalitic

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.main_activity_log_publish_demo.*

class LogPublishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_log_publish_demo)

        publishLogButton.setOnClickListener { click() }
    }

    private fun click() {
        if (!isInputFilled(nameEditText)) {
            Toast.makeText(this, "empty event name!", Toast.LENGTH_SHORT).show()
        } else {
            if (isInputFilled(descriptionEditText)) {
                event(nameEditText.text.toString().replace(" ", "_"),
                        descriptionEditText.text.toString().replace(" ", "_"))
            } else {
                event(nameEditText.text.toString().replace(" ", "_"))
            }
        }
    }

    private fun isInputFilled(textInputEditText: TextInputEditText): Boolean {
        return !TextUtils.isEmpty(textInputEditText.text)
    }

    private fun event(eventName: String) {
        val isSent = LogPublishService.publisher().event(eventName)
        val concatenated = extendLogs(eventName)
        displayLogs(isSent, concatenated)
    }

    private fun event(eventName: String, description: String) {
        val isSent = LogPublishService.publisher().event(eventName, description)
        val concatenated = extendLogs(eventName, description)
        displayLogs(isSent, concatenated)
    }

    private fun extendLogs(eventName: String): String {
        return "${publishedLoggedTextView.text}\n$eventName"
    }

    private fun extendLogs(eventName: String, description: String): String {
        return "${extendLogs(eventName)}, $description"
    }

    private fun displayLogs(isSent: Boolean, concatenated: String) {
        if (isSent) {
            publishedLoggedTextView.text = concatenated
            publishedLoggedScrollView.scrollTo(0, publishedLoggedScrollView.bottom)
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
        }
    }
}
