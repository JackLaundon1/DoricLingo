package com.example.doriclingo.screens

import TrainingScreen
import TrainingViewModel
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doriclingo.R
import com.example.doriclingo.model.Sentence
import com.example.doriclingo.network.NetworkConnectivityObserver
import com.example.doriclingo.network.NetworkObserver
import com.example.doriclingo.repositories.ProgressRepository
import com.example.doriclingo.repositories.SentenceRepository
import com.example.doriclingo.room.dao.ProgressDao

//wrapper function used to separate logic from the UI
@Composable
fun TrainingScreenWrapper(
    progressDao: ProgressDao,
    userId: String?,
    onQuitClick: () -> Unit
) {
    val context = LocalContext.current
    //accesses the progres repository
    val repository = ProgressRepository(context)
    val scope = rememberCoroutineScope()
    //checks for internet connection
    lateinit var networkConnectivityObserver: NetworkObserver
    networkConnectivityObserver = NetworkConnectivityObserver(context)
    val status by networkConnectivityObserver.observe().collectAsState(
        initial = NetworkObserver.Status.Unavailable
    )

    //list of sentences
    //these are hard-coded and not in the strings utility because they must all stay in doric
    //the translations are in the strings file
    //they all have an audio representation
    val sentences = listOf(
        Sentence("Aye aye min", context.getString(R.string.hello_sir), R.raw.aye_aye_min,),
        Sentence("Foo's yer doos?", context.getString(R.string.how_are_you), R.raw.foos_yer_doos),
        Sentence("Aye pechin, and yersel'", context.getString(R.string.im_fine_how_are_you), R.raw.aye_pechin),
        Sentence("I dinna ken", context.getString(R.string.i_dont_know), R.raw.i_dinna_ken),
        Sentence("Fit like i' day?", context.getString(R.string.how_are_you_doing_today), R.raw.fit_like_i_day),
        Sentence("Nae spikkin'", context.getString(R.string.not_speaking), R.raw.nae_spikkin),
        Sentence("Loon", context.getString(R.string.a_boy), R.raw.loon),
        Sentence("Quine", context.getString(R.string.a_girl), R.raw.quine),
        Sentence("Feel", context.getString(R.string.stupid), R.raw.feel),
        Sentence("Teuchter", context.getString(R.string.someone_who_lives_in_the_countryside), R.raw.teuchter),
        Sentence(context.getString(R.string.congratulations), context.getString(R.string.you_have_completed_the_course))
    )

    //gets the sentences from sentence repository
    SentenceRepository.getSentences(sentences)

    //initiates view model with factory
    val viewModel: TrainingViewModel = viewModel(
        factory = Navigation.TrainingViewModelFactory(progressDao, userId, repository = repository)
    )

    //if the network is available, pushes any pending progress
    LaunchedEffect(status) {
        if (status == NetworkObserver.Status.Available) {
            viewModel.pushPendingProgress()
        }
    }

    //only shows the screen if the sentences have loaded
    if(SentenceRepository.sentenceList.isNotEmpty()){
        val sentence by viewModel.currentSentence.collectAsState()
        val translation by viewModel.currentTranslation.collectAsState()

        val currentIndex by viewModel.currentIndex.collectAsState()
        //checks if the user is at the end of the list
        val isLastSentence = currentIndex == SentenceRepository.sentenceList.lastIndex

        //displays the training screen with the current sentence, translation, and audio pronunciation
        TrainingScreen(
            sentence = sentence,
            translation = translation,
            //moves to next screen
            onNextClick = { viewModel.onNextClicked(status)},
            //quits
            onQuitClick = onQuitClick,
            //plays audio
            onPlayAudioClick = {
                val fullSentence = SentenceRepository.sentenceList.find { it.doric == sentence }
                fullSentence?.let{
                    playAudio(context, it)
                }
            },
            isLastSentence = isLastSentence
        )
    }
}

//uses android media player to play audio
fun playAudio(context: Context, sentence: Sentence) {
    sentence.audioId?.let {
        val mediaPlayer = MediaPlayer.create(context, it)
        //sets volume to full
        mediaPlayer.setVolume(1.0f, 1.0f)
        //ends on completion
        mediaPlayer.setOnCompletionListener { player -> player.release() }
        //plays audio
        mediaPlayer.start()
    }
}



