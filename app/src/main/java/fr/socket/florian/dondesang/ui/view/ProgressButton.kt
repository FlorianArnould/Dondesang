package fr.socket.florian.dondesang.ui.view

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import fr.socket.florian.dondesang.R
import kotlinx.android.synthetic.main.progress_button.view.*

class ProgressButton : ConstraintLayout, View.OnClickListener {
    private var onClickListener: OnClickListener? = null

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        inflate(context, R.layout.progress_button, this)
        button.setOnClickListener(this)
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.text))
            buttonText.text = array.getString(0)
            array.recycle()
        }
    }

    override fun onClick(view: View?) {
        displayButtonToProgress(Runnable {
            onClickListener?.onClick(this)
        })
    }

    override fun setOnClickListener(l: OnClickListener?) {
        onClickListener = l
    }

    private fun displayButtonToProgress(runnable: Runnable) {
        button.isEnabled = false
        buttonText.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            buttonProgress.animate().alpha(1f).duration = 500
            runnable.run()
        }, 500)
    }

    fun success(runnable: Runnable) {
        buttonProgress.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            val transitionDrawable = TransitionDrawable(
                arrayOf(
                    context.getDrawable(R.drawable.login_button_normal),
                    context.getDrawable(R.drawable.login_button_success)
                )
            )
            button.background = transitionDrawable
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(500)
            resultButtonIcon.setImageResource(R.drawable.ic_check)
            resultButtonIcon.animate().alpha(1f).duration = 500
            Handler().postDelayed({ runnable.run() }, 500)
        }, 500)
    }

    fun error() {
        buttonProgress.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            val transitionDrawable = TransitionDrawable(
                arrayOf(
                    context.getDrawable(R.drawable.login_button_normal),
                    context.getDrawable(R.drawable.login_button_error)
                )
            )
            button.background = transitionDrawable
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(500)
            resultButtonIcon.setImageResource(R.drawable.ic_close)
            resultButtonIcon.animate().alpha(1f).duration = 500
            Handler().postDelayed({
                resultButtonIcon.animate().alpha(0f).duration = 500
                transitionDrawable.reverseTransition(500)
                Handler().postDelayed({
                    buttonText.animate().alpha(1f).duration = 500
                    button.isEnabled = true
                }, 500)
            }, 1000)
        }, 500)
    }

    fun reinitialize() {
        val transitionDrawable = TransitionDrawable(
            arrayOf(
                context.getDrawable(R.drawable.login_button_success),
                context.getDrawable(R.drawable.login_button_normal)
            )
        )
        button.background = transitionDrawable
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(500)
        resultButtonIcon.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            buttonText.animate().alpha(1f).duration = 500
            button.isEnabled = true
        }, 500)
    }
}