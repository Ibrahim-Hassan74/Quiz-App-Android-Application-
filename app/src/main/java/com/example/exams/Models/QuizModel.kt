package com.example.exams.Models

data class QuizModel(
    val id: String = "",
    val Title: String = "",
    val subTitle: String = "",
    val time: String = "",
    val question: List<QuestionModel> = emptyList()
)