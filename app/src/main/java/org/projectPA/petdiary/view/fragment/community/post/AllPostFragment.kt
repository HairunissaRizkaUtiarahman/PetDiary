package org.projectPA.petdiary.view.fragment.community.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentAllPostBinding
import org.projectPA.petdiary.view.adapters.PostAdapter
import org.projectPA.petdiary.viewmodel.PostViewModel

class AllPostFragment : Fragment() {
    private lateinit var binding: FragmentAllPostBinding
    private lateinit var adapter: PostAdapter

    private val viewModel: PostViewModel by navGraphViewModels(R.id.community_nav) { PostViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Inisialisasi adapter untuk RecyclerView
        adapter = PostAdapter(onClick = { post, _ ->
            viewModel.setPost(post)
            findNavController().navigate(R.id.action_allPostFragment_to_communityCommentFragment) // Navigasi ke Fragment ComentarPostFragment
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
        }

        // Memuat semua daftar post dari viewModel
        viewModel.loadAllPosts()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Tombol Back di TopAppBar untuk mengakahiri activity
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}