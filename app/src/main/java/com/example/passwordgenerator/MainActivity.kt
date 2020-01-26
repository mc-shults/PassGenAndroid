package com.example.passwordgenerator

import android.R.attr.label
import android.content.*
import android.os.Bundle
import android.support.design.widget.CheckableImageButton
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.InputType.*
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initChecks()
        loadMainPassword()
        loadSymbolClasses()
        buttonSettings.setOnClickListener { _ ->
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        buttonCopyPassword.setOnClickListener { _ ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", editResult.text)
            clipboard.primaryClip = clip
        }
        val watcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { dataChanged(); }
        }
        editMainPassword.addTextChangedListener(watcher)
        editSite.addTextChangedListener(watcher)
        editLogin.addTextChangedListener(watcher)
    }

    private fun initPasswordToggle(layout: TextInputLayout, key: String, getPref: () -> Boolean, setPref: (Boolean) -> Unit) {
        setPref(sharedPref().getBoolean(key, getPref()))
        if (!getPref()) {
            layout.passwordVisibilityToggleRequested(true)
        }

        try {
            val field = layout::class.java.getDeclaredField("passwordToggleView");
            field.isAccessible = true;
            val toggle = field.get(layout) as CheckableImageButton
            toggle.setOnClickListener {_ ->
                val checked = !getPref();
                setPref(checked);
                putBoolean(key, checked);
                layout.passwordVisibilityToggleRequested(false)
            }
        }
        catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

    }

    private fun initChecks() {
        initPasswordToggle(editMainPasswordLayout, Constants.hideMainPasswordKey, {Preferences.hideMainPassword}, {v -> Preferences.hideMainPassword = v})
        initPasswordToggle(editSiteLayout,         Constants.hideSiteKey,         {Preferences.hideSite},         {v -> Preferences.hideSite         = v})
        initPasswordToggle(editLoginLayout,        Constants.hideLoginKey,        {Preferences.hideLogin},        {v -> Preferences.hideLogin        = v})
        initPasswordToggle(editResultLayout,       Constants.hideResultKey,       {Preferences.hideResult},       {v -> Preferences.hideResult       = v})
    }

    private fun loadMainPassword() {
        val password = sharedPref().getString(Constants.mainPasswordKey, "")
        editMainPassword.setText(password)
    }

    private fun loadSymbolClasses() {
        Preferences.addLowercase      = sharedPref().getBoolean(Constants.addLowercaseKey,      Preferences.addLowercase)
        Preferences.addUppercase      = sharedPref().getBoolean(Constants.addUppercaseKey,      Preferences.addUppercase)
        Preferences.addDigits         = sharedPref().getBoolean(Constants.addDigitsKey,         Preferences.addDigits)
        Preferences.addSpecialSymbols = sharedPref().getBoolean(Constants.addSpecialSymbolsKey, Preferences.addSpecialSymbols)
    }

    private fun updateHiding(edit: EditText, hide: Boolean) {
        val passwordInputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
        val textInputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        edit.inputType = if (hide) passwordInputType else textInputType
    }

    private fun dataChanged() {
        updateResult()
        updateSavedMainPassword()

    }

    private fun updateResult() {
        if (!editMainPassword.text.isEmpty()) {
            Preferences.passwordLength = sharedPref().getInt(Constants.passwordLengthKey, Preferences.passwordLength)
            val raw = editMainPassword.text.toString() + editSite.text.toString() + editLogin.text.toString()
            val generator = PasswordGenerator()
            editResult.setText(generator.generate(raw))
        } else {
            editResult.setText("")
        }
    }

    private fun updateSavedMainPassword() {
        Preferences.mainPassword = editMainPassword.text.toString()
        val editor = sharedPref().edit()
        val password = if (Preferences.saveMainPassword) editMainPassword.text.toString() else ""
        editor.putString(Constants.mainPasswordKey, password)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        updateResult()
    }
}
