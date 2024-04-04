package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.projectPA.petdiary.R

class DashboardActivity : AppCompatActivity() {
    private lateinit var signOut_Btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        signOut_Btn = findViewById(R.id.signOut_Btn)

        signOut_Btn.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}