package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ActivityProductCategoriesPageBinding
import org.projectPA.petdiary.viewmodel.ProductCategoriesViewModel

class ProductCategoriesPageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProductCategoriesPageBinding
    private val viewModel: ProductCategoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCategoriesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToChoosePetCategoryPageButton.setOnClickListener {
            onBackPressed()
        }

        val petType = intent.getStringExtra("petType")
        viewModel.setPetType(petType)

        viewModel.petType.observe(this, Observer { petType ->
            when (petType) {
                "Cat" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_cat_button, binding.petTypeChoosen, false))
                }
                "Dog" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_dog_button, binding.petTypeChoosen, false))
                }
                "Rabbit" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_rabbit_button, binding.petTypeChoosen, false))
                }
                "Hamster" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_hamster_button, binding.petTypeChoosen, false))
                }
                "Fish" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_fish_button, binding.petTypeChoosen, false))
                }
                "Bird" -> {
                    binding.petTypeChoosen.removeAllViews()
                    binding.petTypeChoosen.addView(layoutInflater.inflate(R.layout.choose_bird_button, binding.petTypeChoosen, false))
                }
            }
        })

        binding.buttonHealthCategory.setOnClickListener {
            viewModel.setCategory("Health")
            navigateToProductPage()
        }

        binding.buttonFoodCategory.setOnClickListener {
            viewModel.setCategory("Food")
            navigateToProductPage()
        }
        binding.buttonGroomingCategory.setOnClickListener {
            viewModel.setCategory("Grooming")
            navigateToProductPage()
        }
        binding.buttonToolsCategory.setOnClickListener {
            viewModel.setCategory("Tools")
            navigateToProductPage()
        }
        binding.buttonOthersCategory.setOnClickListener {
            viewModel.setCategory("Others")
            navigateToProductPage()
        }
    }

    private fun navigateToProductPage() {
        viewModel.category.observe(this, Observer { category ->
            val intent = Intent(this, ProductPageActivity::class.java).apply {
                putExtra("petType", viewModel.petType.value)
                putExtra("category", category)
            }
            startActivity(intent)
        })
    }
}
