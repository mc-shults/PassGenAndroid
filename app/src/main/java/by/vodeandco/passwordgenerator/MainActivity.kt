package by.vodeandco.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkFirstStart()
        setContentView(R.layout.activity_main)

        initChecks()
        loadMainPassword()
        loadSymbolClasses()
        buttonGuide.setOnClickListener { startGuide(true) }
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        buttonCopyPassword.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", editResult.text)
            clipboard.setPrimaryClip(clip)
            val toast = Toast.makeText(
                applicationContext,
                resources.getText(R.string.toast_result_copied), Toast.LENGTH_SHORT
            )
            toast.show()
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

    private fun checkFirstStart() {
        val firstStart = sharedPref().getBoolean("firstStart", true)
        if (firstStart) {
            startGuide(false)
        }
    }

    private fun startGuide(needBack: Boolean) {
        val intent = Intent(this, GuideActivity::class.java)
        intent.putExtra("needBack", needBack)
        startActivity(intent)
    }

    private fun initPasswordToggle(layout: TextInputLayout, edit: EditText, key: String, getPref: () -> Boolean, setPref: (Boolean) -> Unit) {
        setPref(sharedPref().getBoolean(key, getPref()))
        val setTransformMethod = {
            edit.transformationMethod = if (getPref()) null
                else android.text.method.PasswordTransformationMethod.getInstance()
        }
        setTransformMethod()

        try {
            val field = layout::class.java.getDeclaredField("endIconView")
            field.isAccessible = true
            val toggle = field.get(layout) as CheckableImageButton

            toggle.setOnClickListener {
                val checked = !getPref()
                setPref(checked)
                putBoolean(key, checked)
                setTransformMethod()
            }
        }
        catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

    }

    private fun initChecks() {
        initPasswordToggle(editMainPasswordLayout, editMainPassword, Constants.hideMainPasswordKey, {Preferences.hideMainPassword}, {v -> Preferences.hideMainPassword = v})
        initPasswordToggle(editSiteLayout,         editSite,         Constants.hideSiteKey,         {Preferences.hideSite},         {v -> Preferences.hideSite         = v})
        initPasswordToggle(editLoginLayout,        editLogin,        Constants.hideLoginKey,        {Preferences.hideLogin},        {v -> Preferences.hideLogin        = v})
        initPasswordToggle(editResultLayout,       editResult,       Constants.hideResultKey,       {Preferences.hideResult},       {v -> Preferences.hideResult       = v})
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
        Preferences.saveMainPassword  = sharedPref().getBoolean(Constants.saveMainPasswordKey,  Preferences.saveMainPassword)
    }

    private fun dataChanged() {
        updateResult()
        updateSavedMainPassword()

    }

    private fun updateResult() {
        if (editMainPassword.text.isNotEmpty()) {
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
