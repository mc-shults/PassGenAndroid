package com.example.passwordgenerator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_guide.*


class GuideActivity : BaseActivity() {

    private var step = 0
    private var needBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        buttonNext.setOnClickListener { _ -> if (step < 3) setStep(step + 1) else endGuide() }
        buttonBack.setOnClickListener { _ -> setStep(step - 1) }
        buttonSkip.setOnClickListener { _ -> endGuide() }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        checkNeedBack()
    }

    private fun setStep(newStep: Int) {
        step = newStep
        buttonBack.isEnabled = step != 0
        imageIcon.visibility = if (step > 0) View.INVISIBLE else View.VISIBLE
        editMainPasswordLayout.visibility = if (step < 1) View.INVISIBLE else View.VISIBLE
        textMainPassword.visibility = editMainPasswordLayout.visibility
        editSiteLayout.visibility = if (step < 2) View.INVISIBLE else View.VISIBLE
        textSite.visibility = editSiteLayout.visibility
        editResultLayout.visibility = if (step == 3) View.VISIBLE else View.INVISIBLE
        textResult.visibility = editResultLayout.visibility
        buttonNext.text = if (step == 3) resources.getText(R.string.button_lets_go) else resources.getText(R.string.button_next)
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
            3 -> {
                textDescription.text = resources.getText(R.string.text_guide3)
            }
        }
    }

    private fun endGuide() {
        sharedPref().edit().putBoolean("firstStart", false).apply()
        finish()
        if (!needBack) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkNeedBack() {
        needBack = intent.extras?.getBoolean("needBack") ?: false
        if (needBack) {
            setStep(1)
        } else {
            setStep(0)
        }
    }


}
