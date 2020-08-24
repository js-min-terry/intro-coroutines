package tasks

import contributors.*
import retrofit2.Response

// ui 쓰레드에서 사용되기 때문에 ui가 freeze된다.
// AWT-EventQueue-0: Swing, AWT event dispatching thread
fun loadContributorsBlocking(service: GitHubService, req: RequestData) : List<User> {
    val repos = service
        .getOrgReposCall(req.org) // Call 객체를 리턴함
        .execute() // Call 객체를 실행. Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .bodyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributorsCall(req.org, repo.name)
            .execute() // Executes request and blocks the current thread
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

fun <T> Response<List<T>>.bodyList(): List<T> {
    return body() ?: listOf()
}