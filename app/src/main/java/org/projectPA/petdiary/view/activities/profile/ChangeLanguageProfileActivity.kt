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

        // Mendapatkan SharedPreferences dengan nama "settings" dan mode PRIVATE
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("lang", "en") ?: "en"

        // Mengatur locale sesuai bahasa saat ini
        setLocale(this, currentLanguage)

        // Memperbarui tampilan Radio button sesuai bahasa saat ini
        updateCheckImage(currentLanguage)

        // Tombol Bahasa Inggris
        binding.englishBtn.setOnClickListener {
            setLocale(this, "en") // Mengatur locale ke bahasa Inggris
            showLanguageSelected("English")
            saveLanguageSelection("en") // Menyimpan pilihan bahasa ke SharedPreferences
            updateCheckImage("en") // Memperbarui Radio button bahasa Inggris

            // Navigasi ke DashboardActivity setelah pilihan bahasa disimpan
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.indonesianBtn.setOnClickListener {
            setLocale(this, "in") // Mengatur locale ke bahasa Indonesia
            showLanguageSelected("Bahasa Indonesia")
            saveLanguageSelection("in") // Menyimpan pilihan bahasa ke SharedPreferences
            updateCheckImage("in") // Memperbarui Radio button bahasa Inggris

            // Navigasi ke DashboardActivity setelah pilihan bahasa disimpan
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // Tombol Back di TopAppBar untuk mengakahiri activity
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    // Fungsi untuk mengatur locale berdasarkan bahasa yang dipilih
    private fun setLocale(context: Context, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Fungsi untuk menampilkan TOAST
    private fun showLanguageSelected(language: String) {
        Toast.makeText(this, "Language changed to $language", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk menyimpan pilihan bahasa ke SharedPreferences
    private fun saveLanguageSelection(lang: String) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("lang", lang)
            apply()
        }
    }

    // Fungsi untuk memperbarui Radio button cek berdasarkan bahasa yang dipilih
    private fun updateCheckImage(lang: String) {
        binding.checkEnglishIV.visibility = if (lang == "en") View.VISIBLE else View.GONE
        binding.uncheckEnglishIV.visibility = if (lang == "en") View.GONE else View.VISIBLE
        binding.checkIndonesianIV.visibility = if (lang == "in") View.VISIBLE else View.GONE
        binding.uncheckIndonesianIV.visibility = if (lang == "in") View.GONE else View.VISIBLE
    }
}
