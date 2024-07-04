package org.projectPA.petdiary.view.activities.profile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChangeLanguageProfileBinding
import java.util.Locale

class ChangeLanguageProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeLanguageProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeLanguageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("lang", "system") ?: "system"
        setLocale(this, currentLanguage)
        updateCheckImage(currentLanguage)

        binding.systemBtn.setOnClickListener {
            setLocale(this, "system")
            showLanguageSelected("System")
            saveLanguageSelection("system")
            updateCheckImage("system")
        }

        binding.englishBtn.setOnClickListener {
            setLocale(this, "en")
            showLanguageSelected("English")
            saveLanguageSelection("en")
            updateCheckImage("en")
        }

        binding.indonesianBtn.setOnClickListener {
            setLocale(this, "in")
            showLanguageSelected("Bahasa Indonesia")
            saveLanguageSelection("in")
            updateCheckImage("in")
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish() // Kembali ke aktivitas sebelumnya saat tombol navigasi ditekan
        }
    }

    private fun setLocale(context: Context, lang: String) {
        val locale: Locale = if (lang == "system") {
            Locale.getDefault()
        } else {
            Locale(lang)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun showLanguageSelected(language: String) {
        Toast.makeText(this, "Language changed to $language", Toast.LENGTH_SHORT).show()
    }

    private fun saveLanguageSelection(lang: String) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("lang", lang)
            apply()
        }
    }

    private fun updateCheckImage(lang: String) {
        binding.checkSystemIV.visibility = if (lang == "system") View.VISIBLE else View.GONE
        binding.checkEnglishIV.visibility = if (lang == "en") View.VISIBLE else View.GONE
        binding.checkIndonesianIV.visibility = if (lang == "in") View.VISIBLE else View.GONE
    }
}
