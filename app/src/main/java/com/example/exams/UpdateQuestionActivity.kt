package com.example.exams

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.exams.Models.QuizModel
import com.example.exams.Models.QuizTransferHelper
import com.example.exams.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateQuestionActivity : AppCompatActivity() {
    private lateinit var titleEditText: TextInputEditText
    private lateinit var subTitleEditText: TextInputEditText
    private lateinit var timeEditText: TextInputEditText
    private lateinit var updateButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var quiz: QuizModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_question)

        titleEditText = findViewById(R.id.et_quiz_title)
        subTitleEditText = findViewById(R.id.et_subtitle)
        timeEditText = findViewById(R.id.et_time)
        updateButton = findViewById(R.id.btn_update)
        progressBar = findViewById(R.id.progress_bar)

        quiz = QuizTransferHelper.selectedQuiz

        quiz?.let {
            titleEditText.setText(it.title)
            subTitleEditText.setText(it.subTitle)
            timeEditText.setText(it.time)
        }

        updateButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString().trim()
            val updatedSubTitle = subTitleEditText.text.toString().trim()
            val updatedTime = timeEditText.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedSubTitle.isEmpty() || updatedTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = updatedTime.toIntOrNull()
            if (time == null || time <= 0) {
                Toast.makeText(this, "Time must be a positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedQuiz = quiz?.copy(
                title = updatedTitle,
                subTitle = updatedSubTitle,
                time = updatedTime
            )

            if (updatedQuiz != null) {
                progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitClient.quizApi.updateQuiz(updatedQuiz.id, updatedQuiz)
                        }

                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@UpdateQuestionActivity,
                                "Quiz updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(
                                this@UpdateQuestionActivity,
                                "Update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@UpdateQuestionActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}