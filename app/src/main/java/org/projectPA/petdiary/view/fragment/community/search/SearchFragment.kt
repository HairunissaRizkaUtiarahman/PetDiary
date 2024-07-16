package org.projectPA.petdiary.view.fragment.community.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import org.projectPA.petdiary.view.adapters.PostAdapter
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.FragmentUserSearchBinding
import org.projectPA.petdiary.view.adapters.UserAdapter
import org.projectPA.petdiary.viewmodel.PostViewModel
import org.projectPA.petdiary.viewmodel.UserViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentUserSearchBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var postAdapter: PostAdapter

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.community_nav) { UserViewModel.Factory }
    private val postViewModel: PostViewModel by navGraphViewModels(R.id.community_nav) { PostViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Inisialisasi adapter untuk RecyclerView pengguna
        userAdapter = UserAdapter(onClick = { user, _ ->
            userViewModel.setUser(user)
            findNavController().navigate(R.id.action_userSearchFragment_to_userProfileFragment) // Navigasi ke Fragment profil pengguna
        })

        // Set adapter ke RecyclerView untuk pengguna
        binding.userRV.adapter = userAdapter

        // Mengamati perubahan pada daftar pengguna di viewModel
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)

            // Tampilkan jika tidak ada pengguna yang ditemukan
            binding.noUserTV.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
            binding.userRV.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
        }

        // Inisialisasi adapter untuk RecyclerView post
        postAdapter = PostAdapter(onClick = { post, _ ->
            postViewModel.setPost(post)
            findNavController().navigate(R.id.action_userSearchFragment_to_communityCommentFragment) // Navigasi ke Fragment ComentarPostFragment
        }, onLike = { post ->
            postViewModel.setLike(post.id ?: "") // Memperbarui status like
        })

        // Set adapter ke RecyclerView untuk post
        binding.postRV.adapter = postAdapter

        // Mengamati perubahan pada daftar post di viewModel
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
            binding.noPostTV.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.postRV.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        // Memuat daftar pengguna dan post dari viewModel
        userViewModel.loadUsers()
        postViewModel.loadPosts()


        // Fungsi ketika teks pencarian berubah atau disubmit pada SearchView
        binding.searchUserPostSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Memanggil fungsi pencarian pengguna dan post berdasarkan text yang dimasukkan pengguna
                    userViewModel.searchUser(it)
                    postViewModel.searchPost(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {

                        // Jika teks pencarian kosong, muat ulang semua pengguna dan post
                        userViewModel.loadUsers()
                        postViewModel.loadPosts()
                    } else {

                        // Jika tidak kosong, lakukan pencarian pengguna dan post berdasarkan teks yang dimasukkan
                        userViewModel.searchUser(it)
                        postViewModel.searchPost(it)
                    }
                }
                return true
            }
        })

        // Tombol Back di TopAppBar untuk kembali ke stack sebelumnya
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}