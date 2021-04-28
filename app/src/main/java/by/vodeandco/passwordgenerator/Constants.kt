package by.vodeandco.passwordgenerator

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
        const val easiestPasswordLength = 3
        const val easyPasswordLength = 7
        const val mediumPasswordLength = 11
        const val easiestPasswordColor = Color.RED
        val easyPasswordColor = Color.parseColor("#ef6c00")
        val mediumPasswordColor = Color.parseColor("#6ec5ff")
        val hardPasswordColor = Color.parseColor("#2195F2")
    }
}