package org.projectPA.petdiary.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.projectPA.petdiary.databinding.ActivityChoosePetCategoryBinding
import org.projectPA.petdiary.viewmodel.ChoosePetCategoryViewModel

class ChoosePetCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoosePetCategoryBinding
    private val viewModel: ChoosePetCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChoosePetCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.chooseCatButton.setOnClickListener {
            viewModel.selectPetType("cat")
        }

        binding.chooseDogButton.setOnClickListener {
            viewModel.selectPetType("dog")
        }

        binding.chooseRabbitButton.setOnClickListener {
            viewModel.selectPetType("rabbit")
        }

        binding.chooseHamsterButton.setOnClickListener {
            viewModel.selectPetType("hamster")
        }

        binding.chooseFishButton.setOnClickListener {
            viewModel.selectPetType("fish")
        }

        binding.chooseBirdButton.setOnClickListener {
            viewModel.selectPetType("bird")
        }

        viewModel.selectedPetType.observe(this, Observer { petType ->
            startActivity(Intent(this, ActivityChooseProductCategory::class.java).apply {
                putExtra("petType", petType)
            })
        })
    }
}
