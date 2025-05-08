package com.example.exams.Models

data class QuestionModel(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOption: String = ""
)
