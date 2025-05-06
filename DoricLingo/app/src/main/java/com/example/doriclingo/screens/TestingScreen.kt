
import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.doriclingo.R
import com.example.doriclingo.screens.Screen
import com.example.doriclingo.ui.themes.ThemedButton
import com.example.doriclingo.util.isNetworkAvailable

//function to show the quiz
@Composable
fun Quiz(url: String){
    //android view to display the quiz in the app
    AndroidView(factory = { context ->
        WebView(context).apply{
         //displays the quiz in the webview
         webViewClient = WebViewClient()
            //enables javascript for forms
            settings.javaScriptEnabled = true
         //loads the url
         loadUrl(url)
        }
    })
}

@Composable
fun TestingScreen(
    darkTheme: Boolean
){
    //url to the quiz
    val url = "https://docs.google.com/forms/d/e/1FAIpQLSfS8hOG7IVJh8F2cCxMU5E_xaujcgrpKkDMcskUuA4AB69LNA/viewform?usp=preview"
    val context = LocalContext.current
    var showQuiz by rememberSaveable { mutableStateOf(false) }
    var hasNetwork by rememberSaveable { mutableStateOf(false) }
    //enables scrolling
    val scrollState = rememberScrollState()

    //layout to display the quiz
    Column(
        modifier = Modifier
            .fillMaxSize()
            //adds scrolling to the screen
            .verticalScroll(scrollState)
            .padding(16.dp)
    ){
        Text(
            text = context.getString(R.string.testing),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)

        )
        HorizontalDivider(color = colorResource(R.color.grey), thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        //checks for network connection

        LaunchedEffect(Unit) {
            hasNetwork = isNetworkAvailable(context)
        }

        //button to start the quiz
        if(hasNetwork){
            ThemedButton(
                onClick = {showQuiz = true},
                darkTheme = darkTheme,
                text = context.getString(R.string.testing),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 8.dp)
            )
        }else{
            Text(
                text = context.getString(R.string.no_internet),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 20.dp)
                    .fillMaxWidth()
            )
        }

        //shows the quiz in a container
        if(showQuiz){
            //layout and styling
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(R.color.white))
                    .shadow(4.dp)
            ){
                //displays the quiz
                Quiz(url)
            }
        }

    }
}