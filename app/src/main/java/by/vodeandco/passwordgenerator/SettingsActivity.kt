package by.vodeandco.passwordgenerator

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
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
        initExtensionListeners()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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
        for (position in 0 until spinnerAlgorithm.count) {
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

    private fun getSymbolClassCount() : Int {
        var symbolClassCount = 0
        if (Preferences.addLowercase) {
            symbolClassCount++
        }
        if (Preferences.addUppercase) {
            symbolClassCount++
        }
        if (Preferences.addDigits) {
            symbolClassCount++
        }
        if (Preferences.addSpecialSymbols) {
            symbolClassCount++
        }
        return symbolClassCount
    }

    private fun symbolClassCountChanged() {
        val symbolClassCount = getSymbolClassCount()
        var passwordLength = Preferences.passwordLength
        seekLength.max = Constants.maxPasswordLength - symbolClassCount
        if (passwordLength < symbolClassCount) {
            passwordLength = symbolClassCount
        }
        passwordLengthChanged(null, passwordLength)
    }

    private fun initSymbolChecks() {
        val saveMainPassword = {v: Boolean -> sharedPref().edit().putString(Constants.mainPasswordKey, if (v) Preferences.mainPassword else "").apply()}
        initSwitch(switchSaveMainPassword, Constants.saveMainPasswordKey,  {Preferences.saveMainPassword},  { v -> Preferences.saveMainPassword  = v}, saveMainPassword)
        initSwitch(switchLowercase,        Constants.addLowercaseKey,      {Preferences.addLowercase},      { v -> Preferences.addLowercase      = v; symbolClassCountChanged()})
        initSwitch(switchUppercase,        Constants.addUppercaseKey,      {Preferences.addUppercase},      { v -> Preferences.addUppercase      = v; symbolClassCountChanged()})
        initSwitch(switchDigits,           Constants.addDigitsKey,         {Preferences.addDigits},         { v -> Preferences.addDigits         = v; symbolClassCountChanged()})
        initSwitch(switchSpecialSymbols,   Constants.addSpecialSymbolsKey, {Preferences.addSpecialSymbols}, { v -> Preferences.addSpecialSymbols = v; symbolClassCountChanged()})
    }

    private fun initPasswordLengthControls() {
        Preferences.passwordLength = sharedPref().getInt(Constants.passwordLengthKey, Preferences.passwordLength)
        editLength.setText(Preferences.passwordLength.toString())
        seekLength.max = Constants.maxPasswordLength - getSymbolClassCount()
        seekLength.progress = Preferences.passwordLength - getSymbolClassCount()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            updateSeekColor(Preferences.passwordLength)
        }
        seekLength.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) { passwordLengthChanged(seekLength, progress + getSymbolClassCount())}
        })

        editLength.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editLength.text.isNotEmpty()) {
                    passwordLengthChanged(editLength, editLength.text.toString().toInt())
                }
            }
        })
    }

    private fun passwordLengthChanged(sender: Any?, value: Int) {
        if (!passwordLengthEditing && value <= Constants.maxPasswordLength) {
            passwordLengthEditing = true
            if (sender != editLength) {
                editLength.setText(value.toString())
            }
            if (sender != seekLength) {
                seekLength.progress = value - getSymbolClassCount()
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
            val color = when {
                passwordLength <= Constants.easiestPasswordLength -> Constants.easiestPasswordColor
                passwordLength <= Constants.easyPasswordLength -> Constants.easyPasswordColor
                passwordLength <= Constants.mediumPasswordLength -> Constants.mediumPasswordColor
                else -> Constants.hardPasswordColor
            }
        //seekLength.progressDrawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        //seekLength.thumb.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    }

    private fun getExtensionListener(extensionUrl: String) : (View) -> Unit {
        return { _: View ->
            val browse = Intent(Intent.ACTION_VIEW, Uri.parse(extensionUrl))
            startActivity(browse)
        }
    }

    private fun initExtensionListeners() {
        buttonFirefox.setOnClickListener(getExtensionListener("https://addons.mozilla.org/ru/firefox/addon/constant-passgen/"))
        buttonChrome.setOnClickListener(getExtensionListener("https://chrome.google.com/webstore/detail/constant-passgen/nafjpdjfpmgcciefgoiklbdiglcfgegh"))
    }
}
