package io.github.yuk7.idspooflinex.ui

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import io.github.yuk7.idspooflinex.R
import io.github.yuk7.idspooflinex.preferences.PreferenceKeys
import io.github.yuk7.idspooflinex.utils.AndroidIDUtils


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        val btnSave = findViewById<Button>(R.id.buttonSave)
        val switchVisible = findViewById<Switch>(R.id.switchVisibleInLauncher)
        val editTextAndroidID = findViewById<EditText>(R.id.editTextAndroidID)

        editTextAndroidID.setText(pref.getString(PreferenceKeys.keyAndroidId, ""))
        val ces = packageManager.getComponentEnabledSetting(componentName)
        switchVisible.isChecked = ces != PackageManager.COMPONENT_ENABLED_STATE_DISABLED

        btnSave.setOnClickListener {
            editTextAndroidID.setText(editTextAndroidID.text.toString().lowercase())
            val newAndroidID = editTextAndroidID.text.toString()
            if (!AndroidIDUtils.isValidAndroidID(newAndroidID)) {
                AlertDialog.Builder(this)
                    .setTitle("Caution")
                    .setMessage("The Android ID you entered is not a valid 16-digit alphanumeric value.\n" +
                            "Therefore, it cannot be used in the hook.")
                    .setPositiveButton(
                        "Ok"
                    ) { _, _ -> }
                    .show()
            }
            pref.edit().putString(PreferenceKeys.keyAndroidId, newAndroidID).apply()
        }

        switchVisible.setOnCheckedChangeListener { _, isChecked  ->
            if (isChecked) {
                packageManager.setComponentEnabledSetting(
                    componentName,
                    COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Caution")
                    .setMessage(("Press \"Yes\" to hide the app from the launcher.\n" +
                            "You may need to reinstall the application to reconfigure it."))
                    .setPositiveButton(
                        "Yes"
                    ) { _, _ -> packageManager.setComponentEnabledSetting(
                        componentName,
                        COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP)
                    }
                    .setNegativeButton("No") {_, _ ->}
                    .show()
            }
        }
    }
}