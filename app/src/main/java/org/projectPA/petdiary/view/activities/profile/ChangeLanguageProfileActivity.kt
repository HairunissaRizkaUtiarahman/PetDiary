package org.projectPA.petdiary.view.activities.profile

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.databinding.ActivityChangeLanguageProfileBinding
import org.projectPA.petdiary.view.activities.DashboardActivity
import java.util.Locale

class ChangeLanguageProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeLanguageProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeLanguageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("lang", "en") ?: "en"
        setLocale(this, currentLanguage)
        updateCheckImage(currentLanguage)

        binding.englishBtn.setOnClickListener {
            setLocale(this, "en")
            showLanguageSelected("English")
            saveLanguageSelection("en")
            updateCheckImage("en")
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.indonesianBtn.setOnClickListener {
            setLocale(this, "in")
            showLanguageSelected("Bahasa Indonesia")
            saveLanguageSelection("in")
            updateCheckImage("in")
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setLocale(context: Context, lang: String) {
        val locale = Locale(lang)
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
        binding.checkEnglishIV.visibility = if (lang == "en") View.VISIBLE else View.GONE
        binding.uncheckEnglishIV.visibility = if (lang == "en") View.GONE else View.VISIBLE
        binding.checkIndonesianIV.visibility = if (lang == "in") View.VISIBLE else View.GONE
        binding.uncheckIndonesianIV.visibility = if (lang == "in") View.GONE else View.VISIBLE
    }
}
