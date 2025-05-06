import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.doriclingo.R
import com.example.doriclingo.screens.playAudio

@Composable
fun TrainingScreen(
    //sentence to display
    sentence: String,
    //translation of sentence
    translation: String,
    //callback to move to next sentence
    onNextClick: () -> Unit,
    //callback to quit the training
    onQuitClick: () -> Unit,
    //callback to play audio
    onPlayAudioClick: () -> Unit,
    //flag to test if the current sentence is the last
    isLastSentence: Boolean
) {
    val context = LocalContext.current
    //layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //displays the sentence
        Text(
            text = sentence,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //displays the translation
        Text(
            text = translation,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp),
        )
        //if not the last sentence, show all buttons
        if (!isLastSentence) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                //button to move to next sentence
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(context.getString(R.string.next))
                }

                //button to quit
                Button(
                    onClick = onQuitClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(context.getString(R.string.quit))
                }
            }

            //button to play audio
            Button(
                onClick = onPlayAudioClick,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(context.getString(R.string.play))
            }
        }
        //if last sentence
        else{
            Row {
                //show only the quit button
                Button(
                    onClick = onQuitClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                ) {
                    Text(context.getString(R.string.quit))
                }
            }

        }


    }
}
