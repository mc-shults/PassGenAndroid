package com.example.passwordgenerator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.CheckBox

abstract class BaseActivity : Activity() {
    protected fun sharedPref() : SharedPreferences {
        return getSharedPreferences("", Context.MODE_PRIVATE)
    }

    protected fun putInt(key: String, value: Int) {
        sharedPref().edit().putInt(key, value).apply()
    }

    protected fun putBoolean(key: String, value: Boolean) {
        sharedPref().edit().putBoolean(key, value).apply()
    }

    protected fun initCheck(check: CheckBox, key: String, getPref: () -> Boolean, setPref: (Boolean) -> Unit, onChange: ((Boolean) -> Unit)? = null) {
        setPref(sharedPref().getBoolean(key, getPref()))
        check.isChecked = getPref()
        check.setOnCheckedChangeListener {_, checked -> setPref(checked); putBoolean(key, checked); onChange?.invoke(checked) }
    }
}