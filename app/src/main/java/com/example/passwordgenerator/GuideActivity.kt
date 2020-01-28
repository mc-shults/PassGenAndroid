package com.example.passwordgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_guide.*


class GuideActivity : AppCompatActivity() {

    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        setStep(0)
        buttonNext.setOnClickListener { _ -> if (step < 2) setStep(step + 1) else finish() }
        buttonBack.setOnClickListener { _ -> setStep(step - 1) }
        buttonSkip.setOnClickListener { _ -> finish() }
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    fun setStep(newStep: Int) {
        step = newStep
        buttonBack.isEnabled = step != 0
        editSiteLayout.visibility = if (step == 0) View.INVISIBLE else View.VISIBLE
        textSite.visibility = editSiteLayout.visibility
        editResultLayout.visibility = if (step == 2) View.VISIBLE else View.INVISIBLE
        textResult.visibility = editResultLayout.visibility
        buttonNext.text = if (step == 2) resources.getText(R.string.button_lets_go) else resources.getText(R.string.button_next)
        when (step) {
            0 -> {
                textDescription.text = resources.getText(R.string.text_guide0)
            }
            1 -> {
                textDescription.text = resources.getText(R.string.text_guide1)
            }
            2 -> {
                textDescription.text = resources.getText(R.string.text_guide2)
            }
        }
    }
}
