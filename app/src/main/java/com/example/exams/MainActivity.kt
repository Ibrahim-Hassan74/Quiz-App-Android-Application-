package com.example.exams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exams.Adabter.QuizListAdapter
import com.example.exams.Models.QuizModel
import com.example.exams.Models.QuizTransferHelper
import com.example.exams.api.RetrofitClient
import com.example.exams.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object {
        var quizModelList: MutableList<QuizModel> = mutableListOf()
    }

    lateinit var binding: ActivityMainBinding
    lateinit var quizListAdapter: QuizListAdapter
    private val updateQuizResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddQuiz.setOnClickListener {
            val intent = Intent(this, AddQuestionActivity::class.java)
            startActivity(intent)
        }

        binding.availableQuizzes.setOnClickListener {
            getData()
        }

        getData()

//        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        binding.progressBar.visibility = View.GONE

        quizListAdapter = QuizListAdapter(
            quizModelList,
            onDelete = { quiz ->
                lifecycleScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitClient.quizApi.deleteQuiz(quiz.id)
                        }

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity,
                                "Deleted: ${quiz.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                            getData()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to delete quiz",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            onEdit = { quiz ->
                QuizTransferHelper.selectedQuiz = quiz
                val intent = Intent(this, UpdateQuestionActivity::class.java)
                updateQuizResultLauncher.launch(intent)
            }
        )


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