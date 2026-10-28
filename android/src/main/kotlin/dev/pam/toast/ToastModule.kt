package dev.pam.toast

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dev.pam.nativeapp.modules.NativeModule
import dev.pam.nativeapp.modules.ModuleCompletion
import dev.pam.nativeapp.modules.ModuleResultStatus
import dev.pam.nativeapp.protocol.WireMap
import dev.pam.nativeapp.protocol.WireValue

class ToastModule(private val context: Context) : NativeModule, AutoCloseable, Application.ActivityLifecycleCallbacks {
    private val application = context.applicationContext as? Application
    init { application?.registerActivityLifecycleCallbacks(this) }
    override fun onActivityStopped(activity: Activity) { if (activity === context) { visible = false; PamInAppToast.dismiss() } }
    override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) { if (activity === context) visible = true }
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) { if (activity === context) PamInAppToast.dismiss() }

    private val main = Handler(Looper.getMainLooper())
    @Volatile private var closed = false
    @Volatile private var visible = true
    override fun invoke(method: String, payload: ByteArray, completion: ModuleCompletion) {
        if (method != "show" || closed) { completion.complete(ModuleResultStatus.FAILURE, byteArrayOf()); return }
        val values = WireMap.decode(payload)
        val message = values.text("message")
        val activity = context as? Activity
        if (values.containsKey("title") && activity != null) {
            fun color(key: String, fallback: Long): Int =
                ((values[key] as? WireValue.Integer)?.value ?: fallback).toInt()
            fun number(key: String, fallback: Double): Double =
                (values[key] as? WireValue.Decimal)?.value ?: (values[key] as? WireValue.Integer)?.value?.toDouble() ?: fallback
            val spec = PamInAppToastSpec(
                title = values.text("title"),
                message = message,
                accentColor = color("accentColor", 0xFF87CEFA),
                backgroundColor = color("backgroundColor", 0xFFFFFFFF),
                titleColor = color("titleColor", 0xFF000000),
                messageColor = color("messageColor", 0xFF979797),
                bottom = values.flag("bottom", false),
                offsetDp = number("offset", 40.0).toFloat(),
                durationMs = number("durationMs", 4_000.0).toLong(),
                titleSize = number("titleSize", 12.0).toFloat(),
                messageSize = number("messageSize", 10.0).toFloat(),
                fontFamily = values.textOrNull("fontFamily"),
                borderColor = (values["borderColor"] as? WireValue.Integer)?.value?.toInt(),
                borderWidthDp = number("borderWidth", 0.0).toFloat(),
                cornerRadiusDp = number("cornerRadius", 6.0).toFloat(),
                accentWidthDp = number("accentWidth", 5.0).toFloat(),
                elevationDp = number("elevation", 2.0).toFloat(),
                paddingHorizontalDp = number("paddingHorizontal", 15.0).toFloat(),
                paddingVerticalDp = number("paddingVertical", 10.0).toFloat(),
                minHeightDp = number("minHeight", 60.0).toFloat(),
                titleLines = number("titleLines", 1.0).toInt().coerceIn(1, 4),
                fitContent = values.flag("fitContent", false),
                iconSource = values.textOrNull("iconSource"),
                iconGlyph = values.textOrNull("iconGlyph"),
                iconFontFamily = values.textOrNull("iconFontFamily"),
                iconColor = (values["iconColor"] as? WireValue.Integer)?.value?.toInt(),
                iconSizeDp = number("iconSize", 18.0).toFloat(),
            )
            val typefaces = ToastTypefaceLoader.shared(activity)
            main.post {
                if (!closed && visible) PamInAppToast.show(activity, spec) { family, weight -> typefaces.resolve(family, weight, false) }
                completion.success()
            }
            return
        }
        main.post {
            if (!closed && visible) Toast.makeText(context, message, if (values.flag("long", false)) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
            completion.success()
        }
    }
    override fun close() { closed = true; application?.unregisterActivityLifecycleCallbacks(this); main.post { PamInAppToast.dismiss() } }
}
private fun ModuleCompletion.success() = complete(ModuleResultStatus.SUCCESS, byteArrayOf())
private fun Map<String, WireValue>.text(key: String): String = textOrNull(key) ?: ""
private fun Map<String, WireValue>.textOrNull(key: String): String? = (this[key] as? WireValue.Text)?.value
private fun Map<String, WireValue>.flag(key: String, fallback: Boolean): Boolean = (this[key] as? WireValue.Flag)?.value ?: fallback
