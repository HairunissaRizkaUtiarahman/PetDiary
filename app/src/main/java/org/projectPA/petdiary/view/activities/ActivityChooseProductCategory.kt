package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityChooseProductCategoryBinding
import org.projectPA.petdiary.viewmodel.ChooseProductCategoryViewModel

class ActivityChooseProductCategory : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProductCategoryBinding
    private val viewModel: ChooseProductCategoryViewModel by viewModels()
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
        petType?.let { viewModel.setPetType(it) }

        viewModel.petType.observe(this, Observer { type ->
            when (type) {
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
        })

        binding.buttonFoodCategory.setOnClickListener {
            startFillProductInformationActivity("Food")
        }

        binding.buttonToolsCategory.setOnClickListener {
            startFillProductInformationActivity("Tools")
        }

        binding.buttonOthersCategory.setOnClickListener {
            startFillProductInformationActivity("Others")
        }

        binding.buttonGroomingCategory.setOnClickListener {
            startFillProductInformationActivity("Grooming")
        }

        binding.buttonHealthCategory.setOnClickListener {
            startFillProductInformationActivity("Health")
        }
    }

    private fun startFillProductInformationActivity(category: String) {
        val petType = viewModel.petType.value
        val intent = Intent(this, FillProductInformationActivity::class.java).apply {
            putExtra(PET_TYPE_KEY, petType)
            putExtra(CATEGORY_KEY, category)
        }
        startActivity(intent)
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
