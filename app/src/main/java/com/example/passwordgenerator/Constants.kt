package com.example.passwordgenerator

import android.graphics.Color

class Constants {
    companion object {
        const val mainPasswordKey = "MainPassword"
        const val saveMainPasswordKey = "SaveMainPassword"
        const val hideMainPasswordKey = "HideMainPassword"
        const val hideSiteKey = "HideSite"
        const val hideLoginKey = "HideLogin"
        const val hideResultKey = "HideResult"
        const val algorithmKey = "Algorithm"
        const val addLowercaseKey = "AddLowercase"
        const val addUppercaseKey = "AddUppercase"
        const val addDigitsKey = "AddDigits"
        const val addSpecialSymbolsKey = "AddSpecialSymbols"
        const val passwordLengthKey = "PasswordLength"
        const val maxPasswordLength = 32
        const val easiestPasswordLength = 3
        const val easyPasswordLength = 7
        const val mediumPasswordLength = 11
        const val easiestPasswordColor = Color.RED
        val easyPasswordColor = Color.parseColor("#FFA500")
        val mediumPasswordColor = Color.parseColor("#32CD32")
        val hardPasswordColor = Color.parseColor("#008000")
    }
}