package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChooseProductCategoryBinding

class ActivityChooseProductCategory : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProductCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToChoosePetCategoryPageButton.setOnClickListener {
            onBackPressed()
        }

        val petType = intent.getStringExtra("petType")

        when (petType) {
            "cat" -> {
                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_cat_button, binding.petTypeChoosen, false))
            }
            "dog" -> {
                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_dog_button, binding.petTypeChoosen, false))
            }
            "rabbit" -> {

                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_rabbit_button, binding.petTypeChoosen, false))
            }
            "hamster" -> {

                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_hamster_button, binding.petTypeChoosen, false))
            }
            "fish" -> {

                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_fish_button, binding.petTypeChoosen, false))
            }
            "bird" -> {

                binding.petTypeChoosen.removeAllViews()
                binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_bird_button, binding.petTypeChoosen, false))
            }

        }

        binding.buttonHealthCategory.setOnClickListener {
            startActivity(Intent(this, FillProductInformationActivity::class.java))
        }

    }
}