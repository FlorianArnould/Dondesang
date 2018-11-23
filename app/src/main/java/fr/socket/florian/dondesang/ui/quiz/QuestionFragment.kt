package fr.socket.florian.dondesang.ui.quiz

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import fr.socket.florian.dondesang.model.Question

class QuestionFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_and_text, container, false)
        val question = arguments?.getParcelable<Question>(QUESTION_ARG)
        if (question != null) {
            val imageView = view.findViewById<ImageView>(R.id.image)
            var imageLoaded = false
            Handler().postDelayed({ if (!imageLoaded) imageView.setImageResource(R.drawable.image_placeholder) }, 500)
            Loader().downloadImage(question.image) {
                if (it != null) {
                    imageLoaded = true
                    imageView.setImageBitmap(it)
                }
            }
            view.findViewById<TextView>(R.id.text).text = question.text
        }
        return view
    }

    companion object {
        const val QUESTION_ARG = "question_arg"

        fun newInstance(question: Question): QuestionFragment {
            val fragment = QuestionFragment()
            val bundle = Bundle()
            bundle.putParcelable(QUESTION_ARG, question)
            fragment.arguments = bundle
            return fragment
        }
    }
}