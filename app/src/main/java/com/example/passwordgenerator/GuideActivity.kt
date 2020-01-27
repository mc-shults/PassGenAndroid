package com.example.passwordgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_guide.*


class GuideActivity : AppCompatActivity() {

    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        setStep(0)
        buttonNext.setOnClickListener { _ -> setStep(step + 1) }
        buttonBack.setOnClickListener { _ -> setStep(step - 1) }
        buttonSkip.setOnClickListener { _ -> finish() }
    }

    fun setStep(newStep: Int) {
        step = newStep
        buttonBack.isEnabled = step != 0
        buttonNext.isEnabled = step != 2
        editSiteLayout.visibility = if (step == 0) View.INVISIBLE else View.VISIBLE
        textSite.visibility = editSiteLayout.visibility
        editResultLayout.visibility = if (step == 2) View.VISIBLE else View.INVISIBLE
        textResult.visibility = editResultLayout.visibility
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
