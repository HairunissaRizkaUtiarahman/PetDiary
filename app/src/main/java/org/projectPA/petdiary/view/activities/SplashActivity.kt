package org.projectPA.petdiary.view.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.databinding.ActivitySplashBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale() // Memuat pengaturan bahasa

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.alpha = 0f

        binding.imageView.animate().setDuration(2000).alpha(1f).withEndAction {

            // Setelah animasi selesai, memulai SigninActivity
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // animasi memudar
            finish()
        }
    }

    // Fungsi untuk memuat pengaturan bahasa dari SharedPreferences
    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE) // Mendapatkan SharedPreferences
        val lang = sharedPreferences.getString("lang", "en") ?: "en" // Mendapatkan bahasa yang tersimpan (default/tidak ada menggunakan bahasa inggris)
        setLocale(lang) // Mengatur locale sesuai bahasa yang tersimpan
    }

    // Fungsi untuk mengatur locale berdasarkan bahasa yang dipilih
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
