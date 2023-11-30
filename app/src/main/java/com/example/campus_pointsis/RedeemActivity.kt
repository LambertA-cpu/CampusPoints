package com.example.campus_pointsis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView


class RedeemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem)
        val totalPoints = intent.getIntExtra("totalPoints", 0)
        val pointsTextView = findViewById<TextView>(R.id.textView3)
        pointsTextView.text = "Points: $totalPoints"

        val studentName = intent.getIntExtra("studentName", 0)
        val welcomeTextView = findViewById<TextView>(R.id.UserId)
        welcomeTextView.text = "$studentName"

        // register all the ImageButtons with their appropriate IDs
        val backB: ImageButton = findViewById(R.id.backB)

        // register all the Buttons with their appropriate IDs
        val todoB: Button = findViewById(R.id.todoB)

        // register all the card views with their appropriate IDs
        val contributeCard: CardView = findViewById(R.id.firstPrize)
        val practiceCard: CardView = findViewById(R.id.secondPrize)
        val learnCard: CardView = findViewById(R.id.thirdPrize)
        val interestsCard: CardView = findViewById(R.id.forthPrize)
        val helpCard: CardView = findViewById(R.id.fifthPrize)
        val settingsCard: CardView = findViewById(R.id.infoCard)


        // handle each of the image buttons with the OnClickListener
        backB.setOnClickListener {
            Toast.makeText(this, "Back Button", Toast.LENGTH_SHORT).show()
        }



        // handle each of the buttons with the OnClickListener
        todoB.setOnClickListener {
            Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show()
        }
        todoB.setOnClickListener {
            val intent = Intent(this@RedeemActivity, ReportActivity::class.java)
            startActivity(intent)
        }

//        editProfileB.setOnClickListener {
//            Toast.makeText(this, "Editing Profile", Toast.LENGTH_SHORT).show()
//        }


        // handle each of the cards with the OnClickListener
        contributeCard.setOnClickListener {
            Toast.makeText(this, "Top Prize", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }
        practiceCard.setOnClickListener {
            Toast.makeText(this, "Second Prize", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }
        learnCard.setOnClickListener {
            Toast.makeText(this, "Third Prize", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }
        interestsCard.setOnClickListener {
            Toast.makeText(this, "Fourth Prize", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }
        helpCard.setOnClickListener {
            Toast.makeText(this, "Fifth Prize", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }
        settingsCard.setOnClickListener {
            Toast.makeText(this, "Informatics", Toast.LENGTH_SHORT).show()
        }
    }
}
