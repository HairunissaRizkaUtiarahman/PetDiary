package org.projectPA.petdiary.viewmodel

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.view.activities.GiveRatingActivity
import org.projectPA.petdiary.view.activities.UsageProductActivity

class GiveRatingViewModel : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _rating = MutableLiveData<Double>()
    val rating: LiveData<Double> get() = _rating

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun loadProductDetails(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _product.value = document.toObject(Product::class.java)
                }
            }
            .addOnFailureListener {
                _product.value = null
            }
    }

    fun setRating(rating: Double) {
        _rating.value = rating
    }

    fun navigateToUsageProductActivity(activity: GiveRatingActivity) {
        val product = _product.value
        val rating = _rating.value

        if (rating == 0.0) {
            Toast.makeText(activity, "Please provide a rating", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(activity, UsageProductActivity::class.java).apply {
                putExtra("productId", product?.id)
                putExtra("rating", rating)
                putExtra("brandName", product?.brandName)
                putExtra("productName", product?.productName)
                putExtra("petType", product?.petType)
                putExtra("imageUrl", product?.imageUrl)
            }
            activity.startActivity(intent)
        }
    }
}
