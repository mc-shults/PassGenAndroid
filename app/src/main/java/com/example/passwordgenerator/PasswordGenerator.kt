package com.example.passwordgenerator

import org.bouncycastle.jcajce.provider.digest.*
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.security.NoSuchAlgorithmException

class PasswordGenerator() {
    private abstract class SymbolClass {
        public abstract val size: Int
        public abstract fun getSymbol(index: Int): Char
    }
    private class lowercase : SymbolClass(){
        public override val size = 'z'.toInt() - 'a'.toInt() + 1
        public override fun getSymbol(index: Int): Char {
            return ('a'.toInt() + index).toChar()
        }
    }
    private class uppercase: SymbolClass(){
        public override val size = 'Z'.toInt() - 'A'.toInt() + 1
        public override fun getSymbol(index: Int): Char {
            return ('A'.toInt() + index).toChar()
        }
    }
    private class digit: SymbolClass(){
        public override val size = '9'.toInt() - '0'.toInt() + 1
        public override fun getSymbol(index: Int): Char {
            return ('0'.toInt() + index).toChar()
        }
    }

    private class specialSymbol: SymbolClass(){
        private val symbols = arrayOf('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',',
            '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '`', '{', '|',
            '}', '~')
        public override val size = symbols.size
        public override fun getSymbol(index: Int): Char {
            return symbols[index]
        }
    }


    private fun Byte.toPositiveInt() = toInt() and 0xFF
    private var digest: BCMessageDigest = SHA3.Digest256()
    private val symbolClasses = arrayListOf<SymbolClass>()
    init {
        when (Preferences.algorithm) {
            "SHA3" -> digest = SHA3.Digest256()
            "SHA256" -> digest = SHA256.Digest()
            "SHA512" -> digest = SHA512.Digest()
            "SHA1" -> digest = SHA1.Digest()
            "MD5" -> digest = MD5.Digest()
        }
        if (Preferences.addLowercase) {
            symbolClasses.add(lowercase())
        }
        if (Preferences.addUppercase) {
            symbolClasses.add(uppercase())
        }
        if (Preferences.addDigits) {
            symbolClasses.add(digit())
        }
        if (Preferences.addSpecialSymbols) {
            symbolClasses.add(specialSymbol())
        }
    }

    private fun getChar(value: Byte): Char {
        var currentIndex = value.toPositiveInt()
        while (true) {
            for (symbolClass in symbolClasses) {
                if (currentIndex < symbolClass.size) {
                    return symbolClass.getSymbol(currentIndex)
                } else {
                    currentIndex -= symbolClass.size
                }
            }
        }
    }

    private fun createPasswordFromHash(hash : ByteArray) : String {
        val result = StringBuilder(8)
        for (i in 0 until Preferences.passwordLength) {
            result.append(getChar(hash[i]))
        }
        return result.toString()
    }

    fun generate(source : String) : String? {
        try {
            val hash = digest.digest(source.toByteArray())
            return createPasswordFromHash(hash)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }
}