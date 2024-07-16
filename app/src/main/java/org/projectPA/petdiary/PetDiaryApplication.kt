package org.projectPA.petdiary


import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.projectPA.petdiary.repository.PostRepository
import org.projectPA.petdiary.repository.PetRepository
import org.projectPA.petdiary.repository.MyProfileRepository
import org.projectPA.petdiary.repository.ReviewRepository
import org.projectPA.petdiary.repository.UserRepository

class PetDiaryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    // Inisialisasi repository untuk berbagai entitas aplikasi dengan Firebase Firestore dan Auth
    val myProfileRepository: MyProfileRepository
        get() = MyProfileRepository(Firebase.firestore, FirebaseAuth.getInstance(), Firebase.storage)
    val petRepository: PetRepository
        get() = PetRepository(Firebase.firestore, FirebaseAuth.getInstance(), Firebase.storage)
    val postRepository: PostRepository
        get() = PostRepository(Firebase.firestore, FirebaseAuth.getInstance(), Firebase.storage)
    val reviewRepository: ReviewRepository
        get() = ReviewRepository(Firebase.firestore, FirebaseAuth.getInstance())
    val userRepository: UserRepository
        get() = UserRepository(Firebase.firestore, FirebaseAuth.getInstance())
}