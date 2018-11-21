package fr.socket.florian.dondesang.ui.quiz

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import fr.socket.florian.dondesang.model.Question
import fr.socket.florian.dondesang.model.QuestionResult
import fr.socket.florian.dondesang.ui.fragments.TitledFragment

class QuizFragment : TitledFragment() {
    private val loader = Loader()
    private var currentQuestion: Question? = null
    private var isFinished = false

    override val title: String
        get() = getString(R.string.questions_pre_don)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        loadNextQuestion(view)
        view.findViewById<Button>(R.id.yesButton).setOnClickListener {
            onQuizFinished(view, QuestionResult(false, currentQuestion?.message ?: ""))
        }
        view.findViewById<Button>(R.id.noButton).setOnClickListener {
            loadNextQuestion(view)
        }
        view.findViewById<Button>(R.id.restart_button).setOnClickListener {
            currentQuestion = Question("", "", "", "")
            isFinished = false
            loadNextQuestion(view)
        }
        return view
    }

    private fun loadNextQuestion(view: View) {
        if (isFinished) {
            onQuizFinished(view, QuestionResult(true, getString(R.string.quiz_success_message)))
            return
        }
        hideView(view.findViewById<View>(R.id.restart_button_layout))
        loader.getQuestion(currentQuestion?.next ?: "") { question ->
            if (question != null) {
                revealView(view.findViewById<View>(R.id.buttons_layout))
                view.findViewById<View>(R.id.progress_bar).visibility = View.INVISIBLE
                inflate(QuestionFragment.newInstance(question))
                currentQuestion = question
                isFinished = question.next.isEmpty()
            }
        }
    }

    private fun hideView(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.animate().alpha(0f).duration = 500
            Handler().postDelayed({ view.visibility = View.GONE }, 500)
        }
    }

    private fun revealView(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1f).duration = 500
        }
    }

    private fun inflate(fragment: Fragment) {
        fragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            ?.replace(R.id.frameLayout, fragment)
            ?.commitAllowingStateLoss()
    }

    private fun onQuizFinished(view: View, result: QuestionResult) {
        hideView(view.findViewById<View>(R.id.buttons_layout))
        revealView(view.findViewById<View>(R.id.restart_button_layout))
        inflate(ResultFragment.newInstance(result))
    }

    companion object {
        fun newInstance(): QuizFragment {
            return QuizFragment()
        }
    }

}