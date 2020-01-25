package com.example.passwordgenerator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : BaseActivity()  {
    @Volatile
    private var passwordLengthEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initSpinnerAlgorithm()
        initSymbolChecks()
        initPasswordLengthControls()
    }

    private fun initSpinnerAlgorithm() {
        setAlgorithmAdapter()
        setAlgorithmSelection()
        setAlgorithmListener()
    }

    private fun setAlgorithmAdapter() {
        ArrayAdapter.createFromResource(
            this,
            R.array.algorithm_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAlgorithm.adapter = adapter
        }
    }

    private fun setAlgorithmSelection() {
        for (position in 0 until spinnerAlgorithm.getCount()) {
            if (spinnerAlgorithm.getItemAtPosition(position) == sharedPref().getString(Constants.algorithmKey, "SHA3")) {
                spinnerAlgorithm.setSelection(position)
                break
            }
        }
    }

    private fun setAlgorithmListener() {
        spinnerAlgorithm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val edit = sharedPref().edit()
                Preferences.algorithm = spinnerAlgorithm.selectedItem.toString()
                edit.putString(Constants.algorithmKey, Preferences.algorithm)
                edit.apply()
            }
        }
    }

    private fun initSymbolChecks() {
        initCheck(checkBoxLowercase,      Constants.addLowercaseKey,      {Preferences.addLowercase},      {v -> Preferences.addLowercase      = v})
        initCheck(checkBoxUppercase,      Constants.addUppercaseKey,      {Preferences.addUppercase},      {v -> Preferences.addUppercase      = v})
        initCheck(checkBoxDigits,         Constants.addDigitsKey,         {Preferences.addDigits},         {v -> Preferences.addDigits         = v})
        initCheck(checkBoxSpecialSymbols, Constants.addSpecialSymbolsKey, {Preferences.addSpecialSymbols}, {v -> Preferences.addSpecialSymbols = v})
    }

    private fun initPasswordLengthControls() {
        Preferences.passwordLength = sharedPref().getInt(Constants.passwordLengthKey, Preferences.passwordLength)
        editLength.setText(Preferences.passwordLength.toString())
        seekLength.max = Constants.maxPasswordLength
        seekLength.progress = Preferences.passwordLength
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            updateSeekColor(Preferences.passwordLength)
        }
        seekLength.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) { passwordLengthChanged(seekLength, progress)}
        })

        editLength.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!editLength.text.isEmpty()) {
                    passwordLengthChanged(editLength, editLength.text.toString().toInt());
                }
            }
        })
    }

    private fun passwordLengthChanged(sender: Any?, value: Int) {
        if (!passwordLengthEditing && value >= Constants.minPasswordLength && value <= Constants.maxPasswordLength) {
            passwordLengthEditing = true
            if (sender != editLength) {
                editLength.setText(value.toString())
            }
            if (sender != seekLength) {
                seekLength.progress = value
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                updateSeekColor(value)
            }
            Preferences.passwordLength = value
            putInt(Constants.passwordLengthKey, value)
            passwordLengthEditing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun updateSeekColor(passwordLength: Int) {
            val color = if (passwordLength <= Constants.easiestPasswordLength) Constants.easiestPasswordColor
            else if (passwordLength <= Constants.easyPasswordLength) Constants.easyPasswordColor
            else if (passwordLength <= Constants.mediumPasswordLength) Constants.mediumPasswordColor
            else Constants.hardPasswordColor
        seekLength.progressDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        seekLength.thumb.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}
