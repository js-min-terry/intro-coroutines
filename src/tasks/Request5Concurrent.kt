package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    // coroutineScope에서 동작하기 때문에 마지막이 coroutineScope으로 리턴됨
    repos.map { repo ->
        // Dispatchers 설정을 통해서 코루틴이 실행될 쓰레드풀을 설정할 수 있다.
        async(Dispatchers.Default) {
            log("starting loading for ${repo.name}")
            service.getRepoContributors(req.org, repo.name).also { logUsers(repo, it) }.bodyList()
        }
    }.awaitAll().flatten().aggregate()
}