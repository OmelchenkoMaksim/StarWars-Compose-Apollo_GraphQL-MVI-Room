plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.apolloGraphQL)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = libs.versions.namespace.get()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = libs.versions.testInstrumentationRunner.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.sourceCompatibility.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.targetCompatibility.get())
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}

apollo {
    service(libs.versions.apolloServiceName.get()) {
        packageName.set(libs.versions.apolloPackageName.get())
        schemaFile.set(file(libs.versions.apolloSchemaFile.get()))
        srcDir(libs.versions.apolloSrcDir.get())
    }
}

ksp {
    arg(libs.versions.roomSchemaLocation.get(), libs.versions.schemaLocation.get())
}

dependencies {
    // Compose dependencies
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.material3)

    // Lifecycle dependencies
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coroutines dependencies
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Koin dependencies
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    implementation(libs.koin.androidx.compose)

    // Room dependencies
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    // Apollo GraphQL dependencies
    implementation(libs.apollo.runtime)

    // Network dependencies
    implementation(libs.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Coil dependencies
    implementation(libs.coil.compose)

    // Paging dependencies
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Timber dependencies
    implementation(libs.jakewharton.timber)
}
