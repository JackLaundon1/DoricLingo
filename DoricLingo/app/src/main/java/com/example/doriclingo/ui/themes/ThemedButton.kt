package com.example.doriclingo.ui.themes

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.doriclingo.R


//function to define styled buttons
@Composable
fun ThemedButton(
    onClick: () -> Unit,
    text: String,
    //boolean to determine light or dark theme
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    //boolean to determine if button is secondary or primary
    isSecondary: Boolean = false
) {
    //button text style
    val ButtonTextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(R.color.white)
    )

    val ButtonPrimary = colorResource(R.color.buttonPrimary)
    val ButtonPrimaryDark = colorResource(R.color.buttonPrimaryDark)
    val ButtonSecondary = colorResource(R.color.buttonSecondary)
    val ButtonSecondaryDark = colorResource(R.color.buttonSecondaryDark)

    //changes the button colour based on the theme and if it is secondary or primary
    val buttonColor = if (darkTheme) {
        if (isSecondary) ButtonSecondaryDark else ButtonPrimaryDark
    } else {
        if (isSecondary) ButtonSecondary else ButtonPrimary
    }
    //defines button style
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        //defines text style
        Text(text = text, style = ButtonTextStyle)
    }
}

//function for "danger buttons" - buttons that perform actions like deleting accounts or progress
@Composable
//no dark theme boolean for this button as it should always be bright red to signify the significance of the button
fun DangerButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    //sets the styling
    //button text style
    val ButtonTextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(R.color.white)
    )
    val buttonColor = Color.Red
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        //sets text styling
        Text(text = text, style = ButtonTextStyle)

    }
}
