package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {

    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        val allUsers = Collections.synchronizedList(mutableListOf<User>())
        val numberOfProcessed = AtomicInteger()
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users
                if (numberOfProcessed.incrementAndGet() == repos.size) {
                    updateResults(allUsers.aggregate())
                }
            }
        }
    }
}

/**
 * The crossinline marker is used to mark lambdas that mustn’t allow non-local returns,
 * especially when such lambda is passed to another execution context
 * such as a higher order function that is not inlined,
 * a local object or a nested function. In other words,
 * you won’t be able to do a return in such lambdas.
 */
inline fun <T> Call<T>.onResponse(crossinline callback: (Response<T>) -> Unit) {
    // enqueue를 호출한다.
    enqueue(object : Callback<T> {
        // CallBack object의 2가지 함수를 정의해서 세팅한다.
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}
