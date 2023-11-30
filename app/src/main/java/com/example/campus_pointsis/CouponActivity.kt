package com.example.campus_pointsis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CouponActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)

        val textViewCode = findViewById<TextView>(R.id.textViewCode)
        textViewCode.text = "CODE: ${generateRandomCode()}"
    }

    private fun generateRandomCode(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 7
        val random = java.util.Random()

        val code = StringBuilder()
        repeat(codeLength) {
            val randomIndex = random.nextInt(characters.length)
            code.append(characters[randomIndex])
        }

        return code.toString()
    }
}
