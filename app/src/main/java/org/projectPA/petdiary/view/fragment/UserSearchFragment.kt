package org.projectPA.petdiary.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUserSearchBinding
import org.projectPA.petdiary.view.adapters.UserSearchAdapter
import org.projectPA.petdiary.viewmodel.UserSearchViewModel

class UserSearchFragment : Fragment() {
    private lateinit var binding: FragmentUserSearchBinding
    private lateinit var adapter: UserSearchAdapter
    private val viewModel: UserSearchViewModel by navGraphViewModels(R.id.community_nav) { UserSearchViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = UserSearchAdapter(onClick = { user, _ ->
            viewModel.setUser(user)
            Log.d("UserSearchFragment", user.toString())
            Log.d("UserSearchFragment", viewModel.user.value.toString())

            findNavController().navigate(R.id.action_userSearchFragment_to_userProfileFragment)
        })

        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.searchUser(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadData()
                } else {
                    viewModel.searchUser(newText)
                }
                return true
            }
        })

        binding.userRV.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadData()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}