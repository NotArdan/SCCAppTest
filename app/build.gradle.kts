plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
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

    packaging {
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources {
            excludes += "META-INF/io.netty.versions.properties"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //implementation(platform("com.google.cloud:libraries-bom:26.49.0"))
    //implementation("com.google.cloud:google-cloud-storage")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(platform("software.amazon.awssdk:bom:2.29.29"))
    implementation("software.amazon.awssdk:auth:2.29.29")
    implementation("software.amazon.awssdk:regions:2.29.29")
    implementation("software.amazon.awssdk:s3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("software.amazon.awssdk:core:2.29.29")
    implementation("software.amazon.awssdk:apache-client:2.29.29")

    //amazon andoird sdk
    implementation("com.amazonaws:aws-android-sdk-core:2.77.1")
    implementation("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.77.1")
    implementation("com.amazonaws:aws-android-sdk-s3:2.77.1")

    //amazon sqs
    implementation("software.amazon.awssdk:sqs:2.29.29")

}
