package org.projectPA.petdiary.view.fragment.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import org.projectPA.petdiary.R
import org.projectPA.petdiary.SnackbarIdlingResource
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
    ): View {
        binding = FragmentPostMyProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostMyProfileAdapter(onClick = { post, _ ->
            viewModel.setPost(post)
            findNavController().navigate(R.id.action_myProfileFragment_to_commentPostMyFragmentFragment) // Navigasi ke Fragment Komentar Post
        }, onLike = { post ->
            viewModel.setLike(post.id ?: "") // Memperbarui status like
        }, onDelete = { post ->
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setMessage("Are you sure you want to delete this post?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteData(post.id ?: "") // Delete Post
                    findNavController().popBackStack()
                    showSnackbar("Post deleted successfully")
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            alertDialogBuilder.create().show()
        })

        binding.myPostRV.adapter = adapter

        // Mengamati perubahan pada daftar post di viewModel
        viewModel.myPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)

            // Tampilkan jika tidak ada post atau semua post telah dihapus
            binding.noPostTV.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.myPostRV.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        // Memuat daftar post dari viewModel
        viewModel.loadData()
    }

    // Pesan Snackbar
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        SnackbarIdlingResource.SnackbarManager.registerSnackbar(snackbar)
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                SnackbarIdlingResource.SnackbarManager.unregisterSnackbar(snackbar)
            }
        })
        snackbar.show()
    }

}