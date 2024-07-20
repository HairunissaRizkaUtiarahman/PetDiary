package org.projectPA.petdiary.view.fragment.community.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.view.adapters.PostAdapter
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentPostBinding
import org.projectPA.petdiary.view.activities.community.AddPostCommunityActivity
import org.projectPA.petdiary.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private lateinit var adapter: PostAdapter

    private val viewModel: PostViewModel by navGraphViewModels(R.id.community_nav) { PostViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi adapter untuk RecyclerView
        adapter = PostAdapter(onClick = { post, _ ->
            viewModel.setPost(post)
            findNavController().navigate(R.id.action_communityFragment_to_communityCommentFragment) // Navigasi ke Fragment ComentarPostFragment
        }, onLike = { post ->
            viewModel.setLike(post.id ?: "") // Memperbarui status like
        })

        binding.postRV.adapter = adapter

        // Mengamati perubahan pada daftar post di viewModel
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)

            // Tampilkan jika tidak ada post atau semua post telah dihapus
            binding.noPostTV.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.postRV.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE

            // Tampilkan tombol See More jika jumlah postingan lebih dari 5
            binding.seeMoreBtn.visibility = if (posts.size > 5) View.GONE else View.VISIBLE
        }

        // Memuat daftar post dari viewModel
        viewModel.loadPosts()

        // Mengamati perubahan status loading di ViewModel
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.addPostFAB.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.addPostFAB.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }

        // Tombol Add Post
        binding.addPostFAB.setOnClickListener {
            val intent = Intent(requireContext(), AddPostCommunityActivity::class.java)
            startActivity(intent)
        }

        // Tombol Searching
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    findNavController().navigate(R.id.action_communityFragment_to_userSearchFragment)
                    true
                }
                else -> false
            }
        }

        // Tombol See All Post
        binding.seeMoreBtn.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_allPostFragment)
        }

        // Tombol Back di TopAppBar untuk mengakhiri activity
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}
