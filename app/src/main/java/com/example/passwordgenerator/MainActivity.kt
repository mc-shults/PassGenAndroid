package com.example.passwordgenerator

import android.R.attr.label
import android.content.*
import android.os.Bundle
import android.text.Editable
import android.text.InputType.*
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initChecks()
        loadMainPassword()
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

    private fun initChecks() {
        initCheck(checkBoxSavePassword, Constants.saveMainPasswordKey, {Preferences.saveMainPassword}, {v -> Preferences.saveMainPassword = v}, {v -> updateSavedMainPassword(null)})
        initCheck(checkBoxHideSite,     Constants.hideSiteKey,         {Preferences.hideSite},         {v -> Preferences.hideSite         = v; updateHiding(editSite,     v)})
        initCheck(checkBoxHideLogin,    Constants.hideLoginKey,        {Preferences.hideLogin},        {v -> Preferences.hideLogin        = v; updateHiding(editLogin,    v)})
        initCheck(checkBoxHideResult,   Constants.hideResultKey,       {Preferences.hideResult},       {v -> Preferences.hideResult       = v; updateHiding(editResult,   v)})
    }

    private fun loadMainPassword() {
        val password = sharedPref().getString(Constants.mainPasswordKey, "")
        editMainPassword.setText(password)
    }

    private fun updateHiding(edit: EditText, hide: Boolean) {
        val passwordInputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
        val textInputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        edit.inputType = if (hide) passwordInputType else textInputType
    }

    private fun dataChanged() {
        updateResult()
        updateSavedMainPassword(null)

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

    private fun updateSavedMainPassword(externalEditor : SharedPreferences.Editor?) {
        val editor = externalEditor ?: sharedPref().edit()
        val password = if (checkBoxSavePassword.isChecked) editMainPassword.text.toString() else ""
        editor.putString(Constants.mainPasswordKey, password)
        if (externalEditor == null) {
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        updateResult()
    }
}
