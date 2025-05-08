package com.example.exams

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exams.Adabter.QuizListAdapter
import com.example.exams.Fragment.AddQuestionFragment
import com.example.exams.Models.QuestionModel
import com.example.exams.Models.QuizModel
import com.example.exams.databinding.ActivityMainBinding

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
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = quizListAdapter
    }

    private fun getData() {
        binding.progressBar.visibility = View.VISIBLE
        val listQustionModel = mutableListOf<QuestionModel>()

        listQustionModel.add(
            QuestionModel(
                "What is android OS?", mutableListOf("Language", "OS", "App", "Software"), "OS"
            )
        )
        listQustionModel.add(
            QuestionModel(
                "Who owner of andriod",
                mutableListOf("Google", "Apple", "Microsoft", "Facebook"),
                "Google"
            )
        )
        listQustionModel.add(
            QuestionModel(
                "What is programming language can build mobile app?",
                mutableListOf("Kotlie", "Java", "C++", "A & B"),
                "A & B"
            )
        )

//        quizModelList.add(QuizModel("1", "Quiz 1", "All Math basic", "10", listQustionModel))

        if (quizModelList.isEmpty())
            quizModelList.add(QuizModel("1", "Quiz 1", "All Math basic", "10", listQustionModel))

        initRecyclerView()


    }

}