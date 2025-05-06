package com.example.doriclingo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.doriclingo.R

@Composable
fun DoricLingoTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
){
    //colour scheme
    val pastelGreen = colorResource(id = R.color.pastelGreen)
    val darkGreen = colorResource(id = R.color.darkGreen)
    val lightGreen = colorResource(id = R.color.lightGreen)
    val textColor = colorResource(id = R.color.textColor)
    val surface = colorResource(R.color.surface)
    val primaryDark = colorResource(R.color.color_primary_dark)
    val secondaryDark = colorResource(R.color.color_secondary_dark)
    val backgroundDark = colorResource(R.color.color_background_dark)
    val surfaceDark = colorResource(R.color.color_surface_dark)
    val onPrimaryDark = colorResource(R.color.color_on_primary_dark)
    val onSecondaryDark = colorResource(R.color.color_on_secondary_dark)
    val onBackgroundDark = colorResource(R.color.color_on_background_dark)
    val onSurfaceDark = colorResource(R.color.color_on_surface_dark)


    //defines light colours
    val lightColors = lightColorScheme(
        primary = pastelGreen,
        secondary = darkGreen,
        background = lightGreen,
        surface = surface,
        onPrimary = Color.Black,
        onSecondary = Color.White,
        onBackground = textColor,
        onSurface = Color.Black,
    )

    val darkColors = darkColorScheme(
        primary = primaryDark,
        secondary = secondaryDark,
        background = backgroundDark,
        surface = surfaceDark,
        onPrimary = onPrimaryDark,
        onSecondary = onSecondaryDark,
        onBackground = onBackgroundDark,
        onSurface = onSurfaceDark,
    )

    //set colours based on theme
    val colors = if (darkTheme) darkColors else lightColors

    //defines custom typography styles
    val AppTypography = Typography(
        titleLarge = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        ),
        titleMedium = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
        )
    )

//defines shapes
    val shapes = androidx.compose.material3.Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(16.dp)
    )


    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = AppTypography,
        shapes = shapes
    )
}





