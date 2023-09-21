package com.example.campus_pointsis

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var img_arrow_back: ImageView
    private lateinit var btnReset: Button
    private lateinit var emailVar: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        img_arrow_back = findViewById(R.id.img_arrow_back)
        btnReset = findViewById(R.id.btnReset)
        emailVar = findViewById(R.id.emailVar)

        img_arrow_back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        btnReset.setOnClickListener {
            val email = emailVar.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email is invalid!", Toast.LENGTH_SHORT).show()

            } else {
                sendForgotEmail(email)
            }
        }
    }

    private fun sendForgotEmail(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val successMessage = "Reset password link"
                    Snackbar.make(btnReset, successMessage, Snackbar.LENGTH_LONG).show()
                } else {
                    val errorMessage = "Password reset email"
                    Snackbar.make(btnReset, errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
