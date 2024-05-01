package org.projectPA.petdiary.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.projectPA.petdiary.databinding.ActivityFillProductInformationBinding
import org.projectPA.petdiary.model.Product
import java.util.*


class FillProductInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillProductInformationBinding
    private var imageUrl: String? = null // Variabel untuk menyimpan URL gambar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillProductInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.uploadPhotoButton.setOnClickListener {
            // Logika untuk mengunggah foto ke Firebase Storage
            uploadPhotoToStorage()
        }

        binding.submitButton.setOnClickListener {
            val brandName = binding.formInputBrandName.text.toString()
            val productName = binding.formInputProductName.text.toString()
            val description = binding.formInputDescription.text.toString()

            // Simpan ke Firebase
            saveProductToFirebase(brandName, productName, description)
        }
    }

    private fun uploadPhotoToStorage() {
        // Membuka galeri untuk memilih foto
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Mendapatkan URI gambar yang dipilih dari galeri
            val imageUri = data.data

            // Mendapatkan referensi Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef: StorageReference = storageRef.child("images/${UUID.randomUUID()}")

            // Mengunggah gambar ke Firebase Storage
            if (imageUri != null) {
                imageRef.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Gambar berhasil diunggah, dapatkan URL-nya
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString() // Simpan URL gambar
                            // Tampilkan pesan sukses atau gambar, dll.
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle error jika gagal mengunggah gambar
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun saveProductToFirebase(brandName: String, productName: String, description: String) {
        // Generate ID produk baru
        val productId = Firebase.firestore.collection("products").document().id

        // Buat objek produk dengan URL gambar (jika ada)
        val product = Product(
            id = productId,
            petType = intent.getStringExtra("petType") ?: "",
            category = intent.getStringExtra("category") ?: "",
            brandName = brandName,
            productName = productName,
            description = description,
            imageUrl = imageUrl // URL gambar disimpan di dalam objek produk
        )

        // Simpan ke Firestore
        Firebase.firestore.collection("products").document(productId)
            .set(product)
            .addOnSuccessListener {
                // Navigasi ke ProductDetailActivity setelah produk berhasil disimpan
                val intent = Intent(this@FillProductInformationActivity, ProductDetailActivity::class.java)
                intent.putExtra("productId", productId) // Kirim ID produk sebagai extra
                startActivity(intent)
                // Tampilkan pesan sukses atau gambar, dll.
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                finish() // Optional: Selesai dengan aktivitas FillProductInformationActivity setelah navigasi
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding product", e)
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        private const val TAG = "FillProductInformationActivity"
        private const val PICK_IMAGE_REQUEST = 1 // Request code untuk memilih gambar dari galeri
    }
}
