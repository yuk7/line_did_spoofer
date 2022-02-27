package io.github.yuk7.idspooflinex.utils

import java.util.regex.Pattern

class AndroidIDUtils {
    companion object {
        fun isValidAndroidID(str: String?): Boolean {
            return Pattern.matches("^[0-9a-z]{16}$", str!!)
        }
    }
}