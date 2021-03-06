package io.github.yuk7.idspooflinex.hooks

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.yuk7.idspooflinex.preferences.PreferenceKeys
import io.github.yuk7.idspooflinex.providers.SettingProvider
import io.github.yuk7.idspooflinex.utils.AndroidIDUtils

class AndroidIDHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) {
            return
        }
        if (lpparam.packageName != "jp.naver.line.android") {
            return
        }
        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val context = param.args[0] as Context
                    val androidIdOrig = Settings.Secure.getString(context.contentResolver, ANDROID_ID)
                    XposedBridge.log("LINE Original ANDROID_ID:$androidIdOrig")

                    val cursorPreferences = context.contentResolver.query(
                        Uri.parse(SettingProvider.providerUri + "/" + SettingProvider.keyPreferences),
                        null, null, null, null
                    ) ?: return
                    XposedBridge.log("Getting from Content Provider OK")
                    cursorPreferences.moveToFirst()
                    val preferencesMap: MutableMap<String, String> = mutableMapOf()
                    if (cursorPreferences.count > 0) {
                        do {
                            preferencesMap[cursorPreferences.getString(0)] = cursorPreferences.getString(1)
                        } while (cursorPreferences.moveToNext())
                    }
                    cursorPreferences.close()

                    val newAndroidID = preferencesMap[PreferenceKeys.keyAndroidId]

                    XposedBridge.log("LINE MOD ANDROID_ID: $newAndroidID")
                    if (!AndroidIDUtils.isValidAndroidID(newAndroidID)) {
                        XposedBridge.log("ERROR: LINE MOD ANDROID_ID is invalid.")
                        return
                    }


                    XposedHelpers.findAndHookMethod(
                        Settings.Secure::class.java.name,
                        lpparam.classLoader,
                        "getString",
                        ContentResolver::class.java,
                        String::class.java,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                super.beforeHookedMethod(param)
                                if (param!!.args[1].equals(Settings.Secure.ANDROID_ID)) {
                                    param.result = newAndroidID
                                }
                            }
                        }
                    )
                }
            }
        )
    }

}