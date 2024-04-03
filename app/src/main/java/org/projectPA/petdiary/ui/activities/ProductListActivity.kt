package org.projectPA.petdiary.ui.activities

import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.projectPA.petdiary.R
import org.projectPA.petdiary.model.Product
import org.projectPA.petdiary.ui.adapters.ProductAdapter


class ProductListActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acticity_review_homepage)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Reference to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("products")

        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horizontal)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Inisialisasi adapter
        adapter = ProductAdapter()
        recyclerView.adapter = adapter

        // Get product data from Firebase Realtime Database
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in dataSnapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                // Update RecyclerView with the latest data
                adapter.submitList(productList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


}


