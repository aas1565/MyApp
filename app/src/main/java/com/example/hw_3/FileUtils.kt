// FileUtils.kt
package com.example.hw_3

import android.content.Context
import android.content.Intent
import android.net.Uri

fun downloadAndShowResume(context: Context, resumeUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(resumeUrl), "application/pdf")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Если нет приложения для просмотра PDF
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl))
        context.startActivity(browserIntent)
    }
}