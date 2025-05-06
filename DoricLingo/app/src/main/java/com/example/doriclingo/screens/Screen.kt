package com.example.doriclingo.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Star


import androidx.compose.ui.graphics.vector.ImageVector

//holds all screens with titles and icons
sealed class Screen(val route: String, var title: String, val icon: ImageVector) {
    object Home :
        Screen(
            "home",
            "Home",
            androidx.compose.material.icons.Icons.Filled.Home
        )

    object Training : Screen(
        "training", "Learn Doric",
        androidx.compose.material.icons.Icons.Filled.School
    )

    object SelectCourse : Screen(
        "select", "Choose Course",
        androidx.compose.material.icons.Icons.Filled.School
    )

    object Testing: Screen(
        "testing", "Test your Knowledge!",
        androidx.compose.material.icons.Icons.Filled.Quiz
    )

    object Signup: Screen(
        route = "Signup", title = "Signup",
        androidx.compose.material.icons.Icons.Filled.Quiz
    )

    object Map: Screen(
        "Map", "Where are you?",
        androidx.compose.material.icons.Icons.Filled.LocationOn
    )

    object Settings: Screen(
        route = "settings", title = "Settings",
        icon = androidx.compose.material.icons.Icons.Filled.Settings
    )

    object About: Screen(
        route = "about", title = "About Aberdeen",
        icon = androidx.compose.material.icons.Icons.Filled.LocationCity
    )

    object Account: Screen(
        route = "account", title = "My Account",
        icon = androidx.compose.material.icons.Icons.Filled.AccountCircle
    )


    object Login: Screen(
        route = "login", title = "Log In",
        icon = androidx.compose.material.icons.Icons.Filled.Login
    )


}
