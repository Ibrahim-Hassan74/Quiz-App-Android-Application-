package com.example.exams.Adabter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exams.Models.QuizModel
import com.example.exams.QuizActivity
import com.example.exams.databinding.QuizItemRecyclerRowBinding
import com.example.exams.R

class QuizListAdapter(
    private val quizModelList: List<QuizModel>,
    private val onDelete: (QuizModel) -> Unit // كولباك الحذف
) : RecyclerView.Adapter<QuizListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quizModelList[position])
    }

    override fun getItemCount() = quizModelList.size

    inner class ViewHolder(private val binding: QuizItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: QuizModel) {
            binding.apply {
                quizTitleText.text = model.title
                quizSubtitleText.text = model.subTitle
                quizTimeText.text = model.time + " min"

                root.setOnClickListener {
                    val intent = Intent(root.context, QuizActivity::class.java)
                    QuizActivity.questionModelList = model.question
                    QuizActivity.time = model.time
                    root.context.startActivity(intent)
                }

                root.setOnLongClickListener {
                    showPopupMenu(root, model)
                    true
                }
            }
        }

        private fun showPopupMenu(view: View, model: QuizModel) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.quiz_item_menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        Toast.makeText(
                            view.context,
                            "Edit clicked: ${model.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }

                    R.id.menu_delete -> {
                        onDelete(model)
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }
}
