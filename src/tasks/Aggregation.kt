package tasks

import contributors.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/
val log: Logger = LoggerFactory.getLogger("aggregate")


fun List<User>.aggregate(): List<User> {
    log.info("aggreate users: $this")
    return this.groupBy { it.login }
        .map { User(login = it.key, contributions = it.value.sumBy { user: User -> user.contributions }) }
        .sortedByDescending { it.contributions }
}