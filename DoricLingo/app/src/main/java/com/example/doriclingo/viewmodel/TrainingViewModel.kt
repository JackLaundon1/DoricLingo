import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doriclingo.network.NetworkObserver
import com.example.doriclingo.repositories.ProgressRepository
import com.example.doriclingo.repositories.SentenceRepository
import com.example.doriclingo.room.dao.ProgressDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TrainingViewModel(
    //accesses the progress dao, user ID, and progress repository
    private val progressDao: ProgressDao,
    private val userId: String?,
    private val repository: ProgressRepository
) : ViewModel() {

    //gets the sentence list from the repository
    private val sentenceList = SentenceRepository.sentenceList

    //sets the current index to 0
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0
    )
    //maps the current sentence to the doric text
    val currentSentence = _currentIndex
        .map {
            if (sentenceList.isNotEmpty()) {
                sentenceList[it].doric
            } else ""
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = sentenceList.getOrNull(0)?.doric ?: "" // Default to empty string if list is empty
        )

    //maps the current index to the english translation
    val currentTranslation = _currentIndex
        .map {
            if (sentenceList.isNotEmpty()) {
                sentenceList[it].translation
            } else ""
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = sentenceList.getOrNull(0)?.translation ?: "" // Default to empty string if list is empty
        )

    init {
        //fetches the last sentence index from room db if the user is logged in
        if (userId != null) {
            viewModelScope.launch {
                try {
                    progressDao.getConversation(userId).collect { index ->
                        Log.d("TrainingViewModel", "User Progress Index: $index")
                        _currentIndex.value = index?.coerceIn(sentenceList.indices) ?: 0
                    }
                } catch (e: Exception) {
                    Log.e("TrainingViewModel", "Error getting user progress", e)
                    _currentIndex.value = 0
                }
            }
        }
    }

    //syncs progress with firebase
    suspend fun pushPendingProgress(): Boolean {
        val userId = userId ?: return false
        val pendingProgress = repository.getPendingProgress(userId)
        //ensures the fetched progress is not empty
        val validProgress = pendingProgress.filter {
            it.progress.toDouble() != 0.0 && it.lastSentence != 0
        }

        if (validProgress.isEmpty()) {
            return false
        }

        var allSuccessful = true

        //loops through all pending progress entries
        for (data in validProgress) {
            try {
                val latch = kotlinx.coroutines.CompletableDeferred<Boolean>()

                //attempts to push progress to firebase
                repository.pushProgress(
                    userId = userId,
                    progress = data.progress,
                    lastSentence = data.lastSentence
                ) { success, _ ->
                    latch.complete(success)
                }

                //waits for result from the firebase push
                val success = latch.await()
                if (!success) {
                    allSuccessful = false
                    Log.e("TrainingViewModel", "Failed to push progress: $data")
                }else{
                    repository.deletePendingProgress(data.id)
                }

            } catch (e: Exception) {
                Log.e("TrainingViewModel", "Exception pushing progress to Firebase", e)
                allSuccessful = false
            }
        }
        //returns true or false depending on if the push was successful
        return allSuccessful
    }



    //moves to the next sentence and updates progress
    fun onNextClicked(status: NetworkObserver.Status) {
        if (sentenceList.isNotEmpty()) {
            //updates the sentence index
            val newIndex = (_currentIndex.value + 1).coerceAtMost(sentenceList.lastIndex)
            _currentIndex.value = newIndex
            if (userId != null) {
                viewModelScope.launch {
                    //updates progress in the dao
                    progressDao.updateProgress(
                        conversation = newIndex.toFloat(),
                        lastConversation = newIndex,
                        id = userId
                    )
                    //if the network is available, pushes to firebase
                    if (status == NetworkObserver.Status.Available) {
                        try {
                            //pushes to Firebase if there is a network connection
                            repository.pushProgress(
                                userId = userId,
                                progress = newIndex.toFloat(),
                                lastSentence = newIndex
                            ) { success, message ->
                                if (!success) {
                                    Log.e("TrainingViewModel", "Failed to push progress: $message")
                                } else {
                                    //if successful, delete the pending progress stored in room db
                                    viewModelScope.launch {
                                        //searches for data that matches the current progress
                                        val pending = repository.getPendingProgress(userId)
                                        val match = pending.find {
                                            it.progress == newIndex.toFloat() && it.lastSentence == newIndex
                                        }
                                        match?.let {
                                            Log.d("TrainingViewModel", "Deleting matched pending progress: $it")
                                            repository.deletePendingProgress(it.id)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("TrainingViewModel", "Failed to push progress to Firebase", e)
                        }
                    } else {
                        //saves the data locally to be pushed later
                        repository.storePendingProgress(userId, newIndex.toFloat(), newIndex)
                    }
                }
            }
        }
    }
}
