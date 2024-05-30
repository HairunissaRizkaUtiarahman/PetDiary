package org.projectPA.petdiary.view.fragment.mypet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentMyPetBinding
import org.projectPA.petdiary.view.adapters.MyPetAdapter
import org.projectPA.petdiary.viewmodel.MyPetViewModel

class MyPetFragment : Fragment() {
    private lateinit var binding: FragmentMyPetBinding
    private lateinit var adapter: MyPetAdapter
    private val viewModel: MyPetViewModel by navGraphViewModels(R.id.my_pet_nav) { MyPetViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MyPetAdapter { myPet, _ ->
            viewModel.pet = myPet
            findNavController().navigate(R.id.action_myPetFragment_to_myPetDetailsFragment)
        }

        binding.addPetBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPetFragment_to_myPetAddFragment)
        }

        binding.myPetRV.adapter = adapter

        viewModel.pets.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadData()

    }
}