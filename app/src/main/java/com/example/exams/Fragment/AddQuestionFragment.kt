package com.example.exams.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.exams.MainActivity
import com.example.exams.Models.QuestionModel
import com.example.exams.Models.QuizModel
import com.example.exams.QuizActivity
import com.example.exams.R
import com.example.exams.databinding.FragmentAddQuestionBinding
import com.example.exams.databinding.FragmentAddSingleQuestionBinding
import com.google.android.material.button.MaterialButton
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
                    Title = title,
                    subTitle = subTitle,
                    time = time.toString(),
                    question = copiedQuestions.toMutableList()
                )
                questionModelList.clear()
                MainActivity.quizModelList.add(quiz)
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
