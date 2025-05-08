package com.example.exams

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exams.Models.QuestionModel
import com.example.exams.databinding.ActivityQuizBinding
import com.example.exams.databinding.ScoreDialogBinding
import com.google.android.material.button.MaterialButton

// inherrit from this interface View.OnClickListener to can override the onClick method of all buttons
class QuizActivity : AppCompatActivity(), View.OnClickListener {

    // static variable will use using call name like QuizActivity.questionModelList = value
    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var time: String = ""
    }

    // current question index
    var currentQuestionIndex = 0

    // Handles user selection and deselection of an answer button.
    // If the user clicks the same button again, it will be deselected by
    // resetting the button's state. This logic is implemented in the onClick()
    // method by checking if the clicked button is the same as the previously selected one.
    private var selectedButton: MaterialButton? = null


    // The user's current score in the quiz
    var score = 0

    // The answer currently selected by the user
    var selectedAnswer: String = ""

    // Stores the user's selected answers for each question by index.
    // This helps calculate the final score and restore selections when navigating between questions.
    val userAnswers = mutableMapOf<Int, String>()


    // enable binding
    lateinit var binding: ActivityQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz_activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadQuestion()
        startTimer()
        binding.backBtn.visibility = View.GONE
        updateNavigationButtons()

        binding.apply {
            // register onClick method to all buttons
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)

            // back to prev question logic
            backBtn.setOnClickListener {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--
                    loadQuestion()
                    updateNavigationButtons()
                    backButtonAction()
                }
            }

            // go to next question logic
            nextBtn.setOnClickListener {
                currentQuestionIndex++
                selectedAnswer = ""
                loadQuestion()
                updateNavigationButtons()
                backButtonAction()

            }
        }

    }

    // Updates the navigation buttons based on the current question index.
    // Hides the "Back" button on the first question and shows it otherwise.
    // Changes the "Next" button text to "Submit" on the last question.
    private fun updateNavigationButtons() {
        selectedButton = null
        resetChoices()
        if (currentQuestionIndex == 0) {
            binding.backBtn.visibility = View.GONE
        } else {
            binding.backBtn.visibility = View.VISIBLE
        }

        if (currentQuestionIndex == questionModelList.size - 1) {
            binding.nextBtn.text = "Submit"
        } else {
            binding.nextBtn.text = "Next"
        }
    }


    // Starts a countdown timer (e.g., from 10 minutes to 0).
    // When the timer finishes, the quiz ends (submission logic can be added in onFinish).
    private fun startTimer() {
        val totalTime = time.toInt() * 60 * 1000
        val timer = object : CountDownTimer(totalTime.toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = "$minutes:$remainingSeconds"
            }

            override fun onFinish() {
                finishQuiz()
            }
        }
        timer.start()
    }

    // Loads the current question and updates the progress bar accordingly.
    private fun loadQuestion() {

        if (currentQuestionIndex == questionModelList.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text =
                "Question ${currentQuestionIndex + 1}/ ${questionModelList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / questionModelList.size.toFloat()) * 100).toInt()

            questionTextview.text = questionModelList[currentQuestionIndex].question
            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]

            resetChoices()
        }
    }

    // Restores the previously selected answer when the user navigates back to a previous question.
    private fun backButtonAction() {
        binding.apply {
            val savedAnswer = userAnswers[currentQuestionIndex]
            if (savedAnswer == null) return;
            val buttons = listOf(btn0, btn1, btn2, btn3)
            for (btn in buttons) {
                if (btn.text.toString() == savedAnswer) {
                    btn.setBackgroundColor(
                        ContextCompat.getColor(
                            this@QuizActivity,
                            R.color.orange
                        )
                    )
                    btn.setTextColor(ContextCompat.getColor(this@QuizActivity, R.color.white))
                    btn.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@QuizActivity,
                            android.R.color.transparent
                        )
                    )
                    selectedButton = btn
                    selectedAnswer = savedAnswer
                    break
                }
            }
        }
    }

    // Resets all answer buttons to their default style when the user selects a different option.
    private fun resetChoices() {
        val buttons = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
        for (btn in buttons) {
            resetButtonToDefault(btn)
        }
        selectedButton = null
    }

    // Reset style back ground color to: transparent, text color to: black, stroke color to: orange
    private fun resetButtonToDefault(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        button.setTextColor(ContextCompat.getColor(this, R.color.black))
        button.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
    }

    // Handles the onClick action for all answer buttons, avoiding separate handlers for each.
    override fun onClick(view: View?) {
        val clickedButton = view as MaterialButton

        if (clickedButton == selectedButton) {
            resetButtonToDefault(clickedButton)
            selectedButton = null
            selectedAnswer = ""
            userAnswers.remove(currentQuestionIndex)
        } else {
            selectedButton?.let { resetButtonToDefault(it) }
            clickedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            clickedButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            clickedButton.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))
            selectedButton = clickedButton
            selectedAnswer = clickedButton.text.toString()
            if (!userAnswers.containsKey(currentQuestionIndex)) userAnswers.put(
                currentQuestionIndex,
                selectedAnswer
            )
            else
                userAnswers[currentQuestionIndex] = selectedAnswer
        }

    }

    private fun finishQuiz() {
        // Submit logic

        for ((index, answer) in userAnswers) {
            if (answer == questionModelList[index].correctOption) score++
        }

        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)

        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "${percentage} %"
            if (percentage > 60) {
                scoreTitle.text = "Congrats! You passed"
                scoreTitle.setTextColor(ContextCompat.getColor(this@QuizActivity, R.color.green))
                scoreProgressIndicator.setIndicatorColor(
                    ContextCompat.getColor(
                        this@QuizActivity,
                        R.color.green
                    )
                )
            } else {
                scoreTitle.text = "Oops! you have failed"
                scoreTitle.setTextColor(ContextCompat.getColor(this@QuizActivity, R.color.red))
                scoreProgressIndicator.setIndicatorColor(
                    ContextCompat.getColor(
                        this@QuizActivity,
                        R.color.red
                    )
                )
            }
            scoreSubTitle.text = "${score} out of ${totalQuestions} questions are correct"
            finishButton.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
            .show()

//        Toast.makeText(
//            this@QuizActivity,
//            "your score is ${score} and userAnswer is ${userAnswers.size}",
//            Toast.LENGTH_SHORT
//        )
//            .show()
//        score = 0   // After we will add logic this will remove
//        userAnswers.clear() // also remove this
    }
}