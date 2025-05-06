package com.example.doriclingo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.doriclingo.model.CourseProgress
import com.example.doriclingo.R
import kotlin.math.roundToInt

@Composable
//function for the progress bar
fun CourseProgressBar(courses: List<CourseProgress>){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Text(context.getString(R.string.your_progress), style = MaterialTheme.typography.headlineMedium)

        //loops through the courses
        //there were more courses but didn't work - explain in crit eval
        courses.forEach { course ->
            CourseProgressPoint(course)
        }
    }
}

@Composable
fun CourseProgressPoint(course: CourseProgress){
    //
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = course.course,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${(course.progress * 10).roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            //predefined function for progress bar
            LinearProgressIndicator(
                progress = { course.progress/10 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = colorResource(R.color.light_grey).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    ),
                color = MaterialTheme.colorScheme.primary,
            )

        }
    }
}
