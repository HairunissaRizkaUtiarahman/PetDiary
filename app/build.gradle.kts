plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.projectPA.petdiary"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.projectPA.petdiary"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    testImplementation("androidx.test:core:1.5.0") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    testImplementation("androidx.test:runner:1.5.2") {
        exclude("com.google.protobuf", "protobuf-lite")
    }

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation("androidx.test:rules:1.5.0") {
        exclude("com.google.protobuf", "protobuf-lite")
    }

//    androidTestImplementation 'org.hamcrest:hamcrest-library:2.2'

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("com.google.protobuf:protobuf-javalite:4.27.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation("com.github.bumptech.glide:glide:4.13.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    androidTestImplementation("androidx.test:orchestrator:1.4.1") // Add orchestrator for better test isolation
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.23.0")
    }
}
