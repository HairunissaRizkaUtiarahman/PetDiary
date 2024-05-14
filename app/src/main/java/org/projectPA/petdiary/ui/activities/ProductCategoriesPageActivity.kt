package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityProductCategoriesPageBinding


class ProductCategoriesPageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProductCategoriesPageBinding
    private val PET_TYPE_KEY = "pet_type"
    private val CATEGORY_KEY = "category"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCategoriesPageBinding.inflate(layoutInflater)
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
            val petType = intent.getStringExtra("petType")
            val category = "Health"
            val intent = Intent(this, ProductPageActivity::class.java)
            intent.putExtra("petType", petType)
            intent.putExtra("category", category)
            startActivity(intent)
        }

    }

}