package by.vodeandco.passwordgenerator

import org.bouncycastle.jcajce.provider.digest.*
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.security.NoSuchAlgorithmException

class PasswordGenerator {
    private abstract class SymbolClass {
        abstract val size: Int
        abstract fun getSymbol(index: Int): Char
        var used = false
    }
    private class LowercaseSymbols : SymbolClass(){
        override val size = 'z'.toInt() - 'a'.toInt() + 1
        override fun getSymbol(index: Int): Char {
            return ('a'.toInt() + index).toChar()
        }
    }
    private class UppercaseSymbols: SymbolClass(){
        override val size = 'Z'.toInt() - 'A'.toInt() + 1
        override fun getSymbol(index: Int): Char {
            return ('A'.toInt() + index).toChar()
        }
    }
    private class DigitSymbols: SymbolClass(){
        override val size = '9'.toInt() - '0'.toInt() + 1
        override fun getSymbol(index: Int): Char {
            return ('0'.toInt() + index).toChar()
        }
    }

    private class SpecialSymbols: SymbolClass(){
        private val symbols = arrayOf('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',',
            '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '`', '{', '|',
            '}', '~')
        override val size = symbols.size
        override fun getSymbol(index: Int): Char {
            return symbols[index]
        }
    }


    private fun Byte.toPositiveInt() = toInt() and 0xFF
    private var digest: BCMessageDigest = SHA3.Digest256()
    private val symbolClasses = arrayListOf<SymbolClass>()
    init {
        when (Preferences.algorithm) {
            "SHA3-256" -> digest = SHA3.Digest256()
            "SHA-256" -> digest = SHA256.Digest()
            "SHA-512" -> digest = SHA512.Digest()
            "SHA-1" -> digest = SHA1.Digest()
            "MD5" -> digest = MD5.Digest()
        }
        if (Preferences.addLowercase) {
            symbolClasses.add(LowercaseSymbols())
        }
        if (Preferences.addUppercase) {
            symbolClasses.add(UppercaseSymbols())
        }
        if (Preferences.addDigits) {
            symbolClasses.add(DigitSymbols())
        }
        if (Preferences.addSpecialSymbols) {
            symbolClasses.add(SpecialSymbols())
        }
    }

    private fun getChar(value: Byte): Char {
        var currentIndex = value.toPositiveInt()
        while (true) {
            for (symbolClass in symbolClasses) {
                if (currentIndex < symbolClass.size) {
                    symbolClass.used = true
                    return symbolClass.getSymbol(currentIndex)
                } else {
                    currentIndex -= symbolClass.size
                }
            }
        }
    }

    private fun createPasswordFromHash(hash : ByteArray) : String? {
        val result = StringBuilder(8)
        for (i in 0 until Preferences.passwordLength) {
            result.append(getChar(hash[i]))
        }
        var allUsed = true
        for (symbolClass in symbolClasses) {
            allUsed = allUsed and symbolClass.used
        }
        return if (allUsed) result.toString() else null
    }

    fun generate(source : String) : String? {
        try {
            if (symbolClasses.count() == 0) {
                return ""
            }
            val hash = digest.digest(source.toByteArray())
            val result = createPasswordFromHash(hash)
            if (result != null) {
                return result
            } else {
                symbolClasses.forEach{c -> c.used = false}
                return generate(source + hash.fold("", { str, it -> str + "%02x".format(it) }))
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }
}