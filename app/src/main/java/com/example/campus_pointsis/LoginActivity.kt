package com.example.campus_pointsis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialise firebase auth
        auth = Firebase.auth

        val logIn_btn: TextView = findViewById(R.id.LogIn_btn)
        logIn_btn.setOnClickListener {
            performLogin()
        }

        val forgotText: TextView = findViewById(R.id.fgtbtn)
        forgotText.setOnClickListener {
            val intent = Intent(this, ForgotPassActivity::class.java)
            startActivity(intent)
        }
    }
    object UserData {
        var uid: String = ""
    }

    private fun performLogin() {
        //getting user input
        val studentID: EditText = findViewById(R.id.editID)
        val password: EditText = findViewById(R.id.editPassword)

        //null checks
        if (studentID.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val studentIDInput = studentID.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(studentIDInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If the sign in is a success, move to the main activity
                    val user = auth.currentUser
                    UserData.uid = user?.uid ?: ""
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("users", user)
                    startActivity(intent)

                        Toast.makeText(baseContext,"Successful!",Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText( baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,)
                    .show()
            }
    }
}