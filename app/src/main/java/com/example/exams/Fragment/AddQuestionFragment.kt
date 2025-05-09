package com.example.exams.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.exams.MainActivity
import com.example.exams.Models.QuestionModel
import com.example.exams.Models.QuizModel
import com.example.exams.R
import com.example.exams.api.RetrofitClient
import com.example.exams.databinding.FragmentAddQuestionBinding
import kotlinx.coroutines.launch
import java.util.UUID

class AddQuestionFragment : Fragment() {

    companion object {
        val questionModelList = mutableListOf<QuestionModel>()
    }

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnAddQuestion.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AddSingleQuestion())
                    .addToBackStack(null)
                    .commit()
            }
            btnSaveQuiz.setOnClickListener {
                val title = etQuizTitle.text.toString().trim()
                val subTitle = etSubtitle.text.toString().trim()
                val timeText = etTime.text.toString().trim()

                if (title.isEmpty() || subTitle.isEmpty() || timeText.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val time = timeText.toIntOrNull()
                if (time == null || time <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "Time must be a positive number",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (questionModelList.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please add at least one question",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val copiedQuestions = questionModelList.map {
                    QuestionModel(it.question, it.options.toList(), it.correctOption)
                }
                val quiz = QuizModel(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    subTitle = subTitle,
                    time = time.toString(),
                    question = copiedQuestions.toMutableList()
                )
//                questionModelList.clear()
//                MainActivity.quizModelList.add(quiz)
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)


                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.quizApi.createQuiz(quiz)
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Quiz added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to add quiz: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("AddQuiz", "Error posting quiz", e)
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}