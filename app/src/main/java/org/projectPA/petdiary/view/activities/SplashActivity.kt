package org.projectPA.petdiary.view.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        image = findViewById(R.id.imageView)
        image.alpha = 0f
        image.animate().setDuration(2000).alpha(1f).withEndAction {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "system") ?: "system"
        setLocale(lang)
    }

    private fun setLocale(lang: String) {
        val locale: Locale = if (lang == "system") {
            Locale.getDefault() // Get the default system language
        } else {
            Locale(lang)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}