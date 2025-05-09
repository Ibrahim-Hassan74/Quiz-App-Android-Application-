package com.example.exams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exams.Adabter.QuizListAdapter
import com.example.exams.Models.QuestionModel
import com.example.exams.Models.QuizModel
import com.example.exams.api.RetrofitClient
import com.example.exams.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        var quizModelList: MutableList<QuizModel> = mutableListOf()
    }

    lateinit var binding: ActivityMainBinding
    lateinit var quizListAdapter: QuizListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddQuiz.setOnClickListener {
            val intent = Intent(this, AddQuestionActivity::class.java)
            startActivity(intent)
        }

        getData()

//        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        binding.progressBar.visibility = View.GONE
        quizListAdapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = quizListAdapter
    }

    private fun getData() {
        binding.progressBar.visibility = View.VISIBLE

        // Using coroutine to make the API call
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.quizApi.getQuizzes()
                if (response.isSuccessful) {
                    val quizzes = response.body()
                    if (quizzes != null) {
                        quizModelList.clear()
                        quizModelList.addAll(quizzes)
                        initRecyclerView()
                    } else {
                        showError("No quizzes available")
                    }
                } else {
                    showError("Failed to load quizzes: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
                Log.e("MainActivity", "Error: ${e.message}", e)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}