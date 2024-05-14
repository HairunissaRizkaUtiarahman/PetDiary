package org.projectPA.petdiary.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChooseProductCategoryBinding

class ActivityChooseProductCategory : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductCategoryBinding
    private val PET_TYPE_KEY = "pet_type"
    private val CATEGORY_KEY = "category"

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

        binding.buttonFoodCategory.setOnClickListener {
            val category = "Health"
            val intent = Intent(this, FillProductInformationActivity::class.java).apply {
                putExtra(PET_TYPE_KEY, petType)
                putExtra(CATEGORY_KEY, category)
            }
            startActivity(intent)
        }

        binding.buttonFoodCategory.setOnClickListener {
            val category = "Food" // This is hardcoded for demonstration. It should dynamically match your UI logic.
            val intent = Intent(this, FillProductInformationActivity::class.java).apply {
                putExtra(PET_TYPE_KEY, petType)
                putExtra(CATEGORY_KEY, category)
            }
            startActivity(intent)
        }

        binding.buttonToolsCategory.setOnClickListener {
            val category = "Tools" // This is hardcoded for demonstration. It should dynamically match your UI logic.
            val intent = Intent(this, FillProductInformationActivity::class.java).apply {
                putExtra(PET_TYPE_KEY, petType)
                putExtra(CATEGORY_KEY, category)
            }
            startActivity(intent)
        }
        binding.buttonOthersCategory.setOnClickListener {
            val category = "Others" // This is hardcoded for demonstration. It should dynamically match your UI logic.
            val intent = Intent(this, FillProductInformationActivity::class.java).apply {
                putExtra(PET_TYPE_KEY, petType)
                putExtra(CATEGORY_KEY, category)
            }
            startActivity(intent)
        }
        binding.buttonGroomingCategory.setOnClickListener {
            val category = "Grooming" // This is hardcoded for demonstration. It should dynamically match your UI logic.
            val intent = Intent(this, FillProductInformationActivity::class.java).apply {
                putExtra(PET_TYPE_KEY, petType)
                putExtra(CATEGORY_KEY, category)
            }
            startActivity(intent)
        }

    }

    private fun getCategoryName(parentLayout: LinearLayout): String {
        for (i in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(i)
            if (childView is RadioButton && childView.isChecked) {
                return childView.text.toString()
            }
        }
        return ""
    }
}