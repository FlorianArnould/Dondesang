package fr.socket.florian.dondesang.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.model.QuestionResult

class ResultFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_and_text, container, false)
        val result = arguments?.getParcelable<QuestionResult>(RESULT_ARG)
        if (result != null) {
            val imageView = view.findViewById<ImageView>(R.id.image)
            if (result.succeed) {
                imageView.setColorFilter(context?.getColor(R.color.green) ?: Color.GREEN)
                imageView.setImageResource(R.drawable.ic_check)
            } else {
                imageView.setColorFilter(context?.getColor(R.color.red) ?: Color.RED)
                imageView.setImageResource(R.drawable.ic_close)
            }
            view.findViewById<TextView>(R.id.text).text = result.errorMessage
        }
        return view
    }

    companion object {
        const val RESULT_ARG = "result_arg"

        fun newInstance(result: QuestionResult): ResultFragment {
            val fragment = ResultFragment()
            val bundle = Bundle()
            bundle.putParcelable(RESULT_ARG, result)
            fragment.arguments = bundle
            return fragment
        }
    }
}