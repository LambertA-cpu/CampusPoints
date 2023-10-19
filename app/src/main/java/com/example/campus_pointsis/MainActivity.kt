package com.example.campus_pointsis

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var myButton: Button
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var totalPoints = 0.0


    //Get the curr user
    private val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Initializing views
        myButton = findViewById(R.id.redeemBtn)
        val pointsTextView = findViewById<TextView>(R.id.pointsView)
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)

        //var to store curr user
        val user: FirebaseUser? = currentUser
        if (user != null) {
            val userUid = user.uid
            val databaseReference = database.getReference("users").child(userUid)
            Log.d(TAG,"$user" )


            // Fetch the studentID for the currently logged-in user.
//            val studentIdRef = databaseReference.child("studentID")
//            studentIdRef.addValueEventListener(object : ValueEventListener {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val studentID =   snapshot.child("studentID").value

//                        val studentID = snapshot.getValue()//value.toString()
                        welcomeTextView.text = "Welcome: $studentID"
                    } else {
                        welcomeTextView.text = "Welcome: Unknown"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("MainActivity", "Error reading data: ${error.message}")
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })


            // Retrieve the student's grades and calculate their total points.
            val query = databaseReference.child("units/grades")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        var calculatedPoints = 0.0

                        for (userSnapshot in dataSnapshot.children) {
                            val grade = userSnapshot.value.toString()

                            var points = when (grade) {
                                "A" -> 90.0
                                "B" -> 70.0
                                "C" -> 50.0
                                "D" -> 0.0
                                else -> 0.0
                            }

                            calculatedPoints += points
                        }

                        totalPoints = calculatedPoints
                        pointsTextView.text = "Points: $totalPoints"
                        myButton.isEnabled = totalPoints >= 500
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error in onDataChange: ${e.message}")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MainActivity", "Database error: ${databaseError.message}")
                }
            })
        }
    }

    private fun createChart(anyChartView: AnyChartView) {
        val cartesian = AnyChart.column()
        cartesian.animation(true)
        cartesian.title("Grades for Semesters 1 and 2")
        cartesian.yScale().minimum(0.0)
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)
        cartesian.xAxis(0).title("Subjects")
        cartesian.yAxis(0).title("Grades")

        val semester1Data = mutableListOf<DataEntry>()
        val semester2Data = mutableListOf<DataEntry>()

        // Fetch the current user's grades for semesters 1 and 2.
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userUid = currentUser.uid
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(userUid)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("units").exists()) {
                        for (unitSnapshot in snapshot.child("units").children) {
                            val semester = unitSnapshot.child("semester").getValue(Int::class.java)
                            val subject = unitSnapshot.child("subject").getValue(String::class.java)
                            val grade = unitSnapshot.child("grade").getValue(Double::class.java)

                            if (semester == 1) {
                                semester1Data.add(ValueDataEntry(subject, grade))
                            } else if (semester == 2) {
                                semester2Data.add(ValueDataEntry(subject, grade))
                            }
                        }

                        // Add series for semesters 1 and 2 with colors.
                        cartesian.column(semester1Data).name("Semester 1").color("darkblue")
                        cartesian.column(semester2Data).name("Semester 2").color("yellow")

                        anyChartView.setChart(cartesian)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity", "Error in onDataChange: ${error.message}")
                }
            })
        }
    }
}
