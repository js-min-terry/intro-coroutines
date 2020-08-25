package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

suspend fun loadContributorsNotCancellable(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    // GlobalScope이기 때문에 return을 명시해야함
    return repos.map { repo ->
        // Dispatchers 설정을 통해서 코루틴이 실행될 쓰레드풀을 설정할 수 있다.
        GlobalScope.async(Dispatchers.Default) {
            log("starting loading for ${repo.name}")
            delay(3000)
            service.getRepoContributors(req.org, repo.name).also { logUsers(repo, it) }.bodyList()
        }
    }.awaitAll().flatten().aggregate()
}

