package se.max.androidlab1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import se.max.androidlab1.data.model.Alert
import se.max.androidlab1.data.repository.GitHubRepository
import se.max.androidlab1.domain.DeceptionDetector
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

private const val OWNER = "degenerade"
private const val REPO = "smart-home-gitops"

class MonitorViewModel(
    private val gitHubRepository: GitHubRepository = GitHubRepository()
) : ViewModel() {
    private val flaggedResult = MutableStateFlow<Alert?>(null)
    val alertInfo: StateFlow<Alert?> = flaggedResult
    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg
    private val detector = DeceptionDetector()
    private val busy = MutableStateFlow(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    checkForAttacks()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _errorMsg.value = errorHandler(e)
                }
                delay(30_000L.milliseconds)
            }
        }
    }

    private suspend fun checkForAttacks() {
        val comments =
            gitHubRepository.getAllOpenComments(OWNER, REPO)

        val flaggedAlert = withContext(Dispatchers.Default) {
            comments
                .map { Alert(it.number, detector.analyze(it.comment)) }
                .firstOrNull { it.result.isAttack }
        }

        flaggedResult.value = flaggedAlert
    }

    fun forceReject() {
        val alert = flaggedResult.value ?: return
        if (!busy.compareAndSet(expect = false, update = true)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clearError()
                gitHubRepository.closePullRequest(OWNER, REPO, alert.prNumber)
                checkForAttacks()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMsg.value = errorHandler(e)
            } finally {
                busy.value = false
            }
        }
    }

    fun forceMerge() {
        val alert = flaggedResult.value ?: return
        if (!busy.compareAndSet(expect = false, update = true)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clearError()
                gitHubRepository.applyEcoConfig(OWNER, REPO)
                gitHubRepository.closePullRequest(OWNER, REPO, alert.prNumber)
                checkForAttacks()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMsg.value = errorHandler(e)
            } finally {
                busy.value = false
            }
        }
    }

    private fun errorHandler(e: Exception) : String {
        return when (e) {
            is HttpException -> when (e.code()) {
                401 -> "Bad or expired token (${e.code()})"
                403 -> "Rate limit or missing permission (${e.code()})"
                422 -> "Github rejected the request (${e.code()})"
                404 -> "Resource not found (${e.code()})"
                409 -> "The config changed on GitHub, try again (${e.code()})"
                else -> "GitHub error ${e.code()}"
            }
            is IOException -> "Network error occurred"
            else -> "Unknown error has occurred"
        }
    }

    fun clearError() { _errorMsg.value = null }
}