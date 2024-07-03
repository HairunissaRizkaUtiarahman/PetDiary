package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostMyProfileBinding
import org.projectPA.petdiary.view.adapters.PostMyProfileAdapter
import org.projectPA.petdiary.viewmodel.PostMyProfileViewModel

class PostMyProfileFragment : Fragment() {
    private lateinit var binding: FragmentPostMyProfileBinding
    private lateinit var adapter: PostMyProfileAdapter

    private val viewModel: PostMyProfileViewModel by navGraphViewModels(R.id.my_profile_nav) { PostMyProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostMyProfileAdapter(onClick = { post, _ ->
            viewModel.setPost(post)

            findNavController().navigate(R.id.action_myProfileFragment_to_commentPostMyFragmentFragment)
        }, onLike = { post ->
            viewModel.setLike(post.id ?: "")
        }, onDelete = { post ->
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setMessage("Are you sure you want to delete this post?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteData(post.id ?: "")
                    findNavController().popBackStack()
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            alertDialogBuilder.create().show()
        })

        binding.myPostRV.adapter = adapter

        viewModel.myPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)

            binding.noPostTV.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE

            binding.myPostRV.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loadData()
    }

}