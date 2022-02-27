package io.github.yuk7.idspooflinex.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import androidx.preference.PreferenceManager
import io.github.yuk7.idspooflinex.BuildConfig
import io.github.yuk7.idspooflinex.preferences.PreferenceKeys
import java.io.File

class SettingProvider : ContentProvider() {
    companion object {
        const val keyPreferences = "preferences"
        const val providerUri = "content://" + BuildConfig.APPLICATION_ID + ".SettingProvider"
    }
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selectionClause: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val name = File(uri.path!!).name
        if (context == null || callingPackage == null) {
            return null
        }

        return when (name) {
            keyPreferences -> {
                val cursor = MatrixCursor(arrayOf("key", "value"))
                val pref = PreferenceManager.getDefaultSharedPreferences(context!!)
                val newAndroidId = pref.getString(PreferenceKeys.keyAndroidId, "")
                Log.d("SettingProvider", newAndroidId!!)
                cursor.addRow(arrayOf(PreferenceKeys.keyAndroidId, newAndroidId))
                cursor
            }
            else -> {
                Log.d(
                    "SettingProvider",
                    "Query Argument Invalid [$name] from: $callingPackage"
                )
                null
            }
        }
    }

    override fun getType(p0: Uri): String? {
        return ""
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }
}