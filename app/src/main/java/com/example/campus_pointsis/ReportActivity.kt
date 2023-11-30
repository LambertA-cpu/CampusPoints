package com.example.campus_pointsis

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream


class ReportActivity : AppCompatActivity() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var textViewHeading: TextView
    private lateinit var textViewReport: TextView
    private lateinit var button: Button
    private val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        textViewHeading = findViewById(R.id.textViewHeading)
        textViewReport = findViewById(R.id.textViewReport)
        button = findViewById(R.id.button)

        button.setOnClickListener {
            generatePdf()
        }


        val user: FirebaseUser? = currentUser
        if (user != null) {
            val userUid = user.uid
            val databaseReference = database.getReference("users").child(userUid)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.child("user_data")
                        val userName = userData.child("name").value.toString()
                        //val creationTimestamp = userData.child("creation_timestamp").value.toString()

                        val unitsSnapshot = snapshot.child("units")
                        val reportStringBuilder = StringBuilder()

                        for (unitSnapshot in unitsSnapshot.children) {
                            val unitName = unitSnapshot.child("unit_name").value.toString()
                            val semester = unitSnapshot.child("semester").value.toString()
                            val grade = unitSnapshot.child("grades").value.toString()
                            val attendance = unitSnapshot.child("attendance").value.toString()

                            reportStringBuilder.append("Unit Name: $unitName\n")
                            reportStringBuilder.append("Semester: $semester\n")
                            reportStringBuilder.append("Grade: $grade\n")
                            reportStringBuilder.append("Attendance: $attendance\n\n")
                        }

                        textViewHeading.text = "Report for $userName"
                        textViewReport.text = reportStringBuilder.toString()
                    } else {
                        // Handle the case where user data doesn't exist
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                }
            })
        }
    }
    private fun generatePdf() {
        val reportContent = textViewReport.text.toString()

        if (reportContent.isNotBlank()) {
            try {
                val pdfFilePath = createPdf(reportContent)

                Toast.makeText(this, "PDF saved: $pdfFilePath", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Report content is empty", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createPdf(content: String): String {
        val pdfFileName = "report.pdf"
        val pdfFilePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), pdfFileName)

        val pdfWriter = PdfWriter(FileOutputStream(pdfFilePath))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Add content to PDF
        document.add(Paragraph(content))

        // Close the document
        document.close()

        return pdfFilePath.absolutePath
    }

}

