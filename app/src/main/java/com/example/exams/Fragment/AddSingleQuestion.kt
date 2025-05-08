package com.example.exams.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.exams.Models.QuestionModel
import com.example.exams.R
import com.example.exams.databinding.FragmentAddSingleQuestionBinding

class AddSingleQuestion : Fragment() {
    private var _binding: FragmentAddSingleQuestionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddSingleQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveQuestion.setOnClickListener {
            val questionText = binding.etQuestion.text.toString().trim()
            val option1 = binding.etOption1.text.toString().trim()
            val option2 = binding.etOption2.text.toString().trim()
            val option3 = binding.etOption3.text.toString().trim()
            val option4 = binding.etOption4.text.toString().trim()
            val correctOption = getSelectedCorrectOption()

            if (questionText.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty() &&
                option3.isNotEmpty() && option4.isNotEmpty() && correctOption != null
            ) {
                saveQuestion(questionText, option1, option2, option3, option4, correctOption)
                showToast("Question saved successfully")
                clearForm()
            } else {
                showToast("Please fill in all fields.")
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun getSelectedCorrectOption(): String? {
        return when {
            binding.rbOption1.isChecked -> binding.etOption1.text.toString().trim()
            binding.rbOption2.isChecked -> binding.etOption2.text.toString().trim()
            binding.rbOption3.isChecked -> binding.etOption3.text.toString().trim()
            binding.rbOption4.isChecked -> binding.etOption4.text.toString().trim()
            else -> null
        }
    }

    private fun saveQuestion(
        question: String,
        option1: String,
        option2: String,
        option3: String,
        option4: String,
        correctOption: String
    ) {
        AddQuestionFragment.questionModelList.add(
            QuestionModel(
                question,
                listOf(option1, option2, option3, option4),
                correctOption
            )
        )
    }

    private fun clearForm() {
        binding.etQuestion.text?.clear()
        binding.etOption1.text?.clear()
        binding.etOption2.text?.clear()
        binding.etOption3.text?.clear()
        binding.etOption4.text?.clear()
        binding.rbOption1.isChecked = false
        binding.rbOption2.isChecked = false
        binding.rbOption3.isChecked = false
        binding.rbOption4.isChecked = false
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}