package org.projectPA.petdiary

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.projectPA.petdiary.repository.MyProfileRepository

class PetDiaryApplication: Application() {
    val myProfileRepository: MyProfileRepository
        get() = MyProfileRepository(Firebase.firestore, FirebaseAuth.getInstance(), Firebase.storage)
}