import org.gradle.api.JavaVersion

object Config {
    val minSdk = 23
    val compileSdk = 28
    val targetSdk = 28
    val javaVersion = JavaVersion.VERSION_1_8
    val buildTools = "28.0.3"
}

object Version {
    // <editor-fold desc="google">
    val androidx_core = "1.0.1"
    val androidx_recyclerview = "1.0.0"
    val androidx_navigation = "2.0.0"
    val androidx_constraintLayout = "1.1.3"
    val material = "1.1.0-alpha04"
    // </editor-fold>

    // <editor-fold desc="testing">
    val junit = "4.12"
    val androidx_espresso = "3.1.0"
    val androidx_testing = "1.1.1"
    //</editor-fold>

    // <editor-fold desc="tools">
    val gradleandroid = "3.5.0-beta04"
    val kotlin = "1.3.20"
    val gradleversions = "0.21.0"
    // </editor-fold>

}

object Deps {
    val androidx_core = "androidx.core:core-ktx:${Version.androidx_core}"
    val androidx_constraintlayout = "androidx.constraintlayout:constraintlayout:${Version.androidx_constraintLayout}"
    val androidx_material = "com.google.android.material:material:${Version.material}"
    val androidx_navigation_fragment = "androidx.navigation:navigation-fragment-ktx:${Version.androidx_navigation}"
    val androidx_navigation_ui = "androidx.navigation:navigation-ui-ktx:${Version.androidx_navigation}"
    val androidx_recyclerview = "androidx.recyclerview:recyclerview:${Version.androidx_recyclerview}"

    val testlib_junit = "junit:junit:${Version.junit}"

    val testandroidx_rules = "androidx.test:rules:${Version.androidx_testing}"
    val testandroidx_runner = "androidx.test:runner:${Version.androidx_testing}"
    val testandroidx_espressocore = "androidx.test.espresso:espresso-core:${Version.androidx_espresso}"

    val tools_gradleandroid = "com.android.tools.build:gradle:${Version.gradleandroid}"
    val tools_kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    val tools_gradleversions = "com.github.ben-manes:gradle-versions-plugin:${Version.gradleversions}"

}