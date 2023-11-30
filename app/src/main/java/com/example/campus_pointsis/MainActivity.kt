package com.example.campus_pointsis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChart.pie
import com.anychart.AnyChartView
import com.anychart.chart.common.*
import com.anychart.chart.common.dataentry.*
import com.anychart.enums.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var myButton: Button
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var totalPoints = 0.0
    private val customColors = mapOf(
        "A" to "#4CAF50",
        "B" to "#2196F3",
        "C" to "#FFC107",
        "D" to "#FF5722",
        "Other" to "#9E9E9E"
    )

    // Get the current user
    private val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initializing views
        myButton = findViewById(R.id.redeemBtn)
        val pointsTextView = findViewById<TextView>(R.id.pointsView)
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val anyChartView = findViewById<AnyChartView>(R.id.idBarChart)
        //val anyChartView1 = findViewById<AnyChartView>(R.id.pieChartView)


        // Variable to store the current user
        val user: FirebaseUser? = currentUser
        if (user != null) {
            val userUid = user.uid
            val databaseReference = database.getReference("users").child(userUid)
            databaseReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val studentData = snapshot.child("student")
                        val studentName = studentData.child("student_name").value.toString()

                        welcomeTextView.text = "Welcome: $studentName"

                        // Retrieve the student's grades and calculate their total points.
                        val unitsSnapshot = snapshot.child("units")
                        var calculatedPoints = 0.0

                        for (unitSnapshot in unitsSnapshot.children) {
                            val grade = unitSnapshot.child("grades").value.toString()
                            val attendance = unitSnapshot.child("grades").value?.toString()?.toFloatOrNull() ?: 0.0f


                            val points = when (grade) {
                                "A" -> 90.0
                                "B" -> 70.0
                                "C" -> 50.0
                                "D" -> 30.0
                                else -> 0.0
                            }

                            calculatedPoints += points - (attendance*100)
                        }

                        totalPoints = calculatedPoints
                        pointsTextView.text = "Points: $totalPoints"
                        myButton.isEnabled = totalPoints >= 500
                        myButton.setOnClickListener {
                            val intent = Intent(this@MainActivity, RedeemActivity::class.java)
                            startActivity(intent)
                        }
//                        val intent = Intent(this@MainActivity, RedeemActivity::class.java)
//                        intent.putExtra("totalPoints", totalPoints)
//                        startActivity(intent)

                        // Call the createChart function with the AnyChartView and unitsSnapshot
                        createChart(anyChartView, unitsSnapshot)
                        //createPieChart(anyChartView1, unitsSnapshot)
                    } else {
                        // Data not found in the database
                        welcomeTextView.text = "Welcome: Data not found"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                    Log.e("MainActivity", "Error reading data: ${error.message}")
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    fun createChart(anyChartView: AnyChartView, unitsSnapshot: DataSnapshot) {
        val cartesian = AnyChart.cartesian()

        cartesian.title("Grades for Semesters 1 and 2 (Comparison)")
        cartesian.xAxis(0).title("Subjects")
        cartesian.yAxis(0).title("Grades")

        val semesterToGradesMap = mutableMapOf<String, MutableList<Pair<String, Double>>>()

        for (unitSnapshot in unitsSnapshot.children) {
            val semester = unitSnapshot.child("semester").getValue(String::class.java) ?: ""
            val unitID = unitSnapshot.key ?: ""
            val gradeString = unitSnapshot.child("grades").getValue(String::class.java) ?: "0.0"

            val grade = when (gradeString) {
                "A" -> 90.0
                "B" -> 70.0
                "C" -> 50.0
                "D" -> 30.0
                else -> gradeString.toDoubleOrNull() ?: 0.0
            }

            if (semesterToGradesMap.containsKey(semester)) {
                semesterToGradesMap[semester]?.add(Pair(unitID, grade))
            } else {
                semesterToGradesMap[semester] = mutableListOf(Pair(unitID, grade))
            }
        }

        for ((semester, dataEntries) in semesterToGradesMap) {
            val seriesData = dataEntries.map { ValueDataEntry(it.first, it.second) }

            // Create a separate series for each semester
            cartesian.line(seriesData).name("Semester $semester").color(
                when (semester) {
                    "1" -> "darkblue"
                    "2" -> "yellow"
                    else -> "black"
                }
            )
        }

        // Calculate the maximum grade to set as the upper limit of the y-axis

        anyChartView.setChart(cartesian)
        anyChartView.visibility = View.VISIBLE
    }

    fun createPieChart(anyChartView1: AnyChartView, unitsSnapshot: DataSnapshot) {
        val pie = pie()

        pie.title("Student Grades Distribution")

        val gradeCounts = mutableMapOf<String, Int>()

        for (unitSnapshot in unitsSnapshot.children) {

            val grade = when (unitSnapshot.child("grades").getValue(String::class.java) ?: "0.0") {
                "A" -> "A"
                "B" -> "B"
                "C" -> "C"
                "D" -> "D"
                else -> "Other"
            }

            if (gradeCounts.containsKey(grade)) {
                gradeCounts[grade] = gradeCounts[grade]!! + 1
            } else {
                gradeCounts[grade] = 1
            }
        }

        // Convert the gradeCounts map to a list of ValueDataEntry objects.
        val dataEntries = gradeCounts.map { (grade, count) ->
            ValueDataEntry(grade, count)
        }

        // Set the data for the pie chart.
        pie.data(dataEntries)

        // Set the palette for the pie chart.
        pie.palette(customColors.values.toTypedArray())

        // Set the chart to the AnyChartView1.
        anyChartView1.setChart(pie)
        anyChartView1.visibility = View.VISIBLE
    }
}