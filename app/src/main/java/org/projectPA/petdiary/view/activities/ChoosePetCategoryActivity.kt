package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.databinding.ActivityChoosePetCategoryBinding

class ChoosePetCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoosePetCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChoosePetCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.chooseCatButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "cat")
            })
        }

        binding.chooseDogButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "dog")
            })
        }

        binding.chooseRabbitButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "rabbit")
            })
        }

        binding.chooseHamsterButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "hamster")
            })
        }

        binding.chooseFishButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "fish")
            })
        }

        binding.chooseBirdButton.setOnClickListener {
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", "bird")
            })
        }

    }
}
