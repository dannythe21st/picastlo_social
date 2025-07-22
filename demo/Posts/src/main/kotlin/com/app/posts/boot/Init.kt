package com.app.posts.boot


import com.app.posts.data.PostDAO
import com.app.posts.data.PostRepository
import com.app.posts.data.Visibility
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths
import java.time.Instant

@Component
class Init(val posts: PostRepository): CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {

        val mockposts = listOf(
            PostDAO(0, "rcosta", 1, 1, "my post 1 description", -1, Visibility.FRIEND_ONLY),
            PostDAO(0, "rcosta", 3, 3, "my post 2 description", 1, Visibility.PUBLIC),
            PostDAO(0, "rcosta", 3, 1, "my post 3 description", -1, Visibility.FRIEND_ONLY),
            PostDAO(0, "deugenio", 4, 5, "my post 4 description", -1, Visibility.GROUP),
            PostDAO(0, "deugenio", 5, 6, "my post 5 description", -1, Visibility.PUBLIC),
            PostDAO(0, "deugenio", 6, 7, "my post 6 description", 1, Visibility.FRIEND_ONLY),
            PostDAO(0, "deugenio", 7, 8, "my post 7 description", 1, Visibility.GROUP),
            PostDAO(0, "deugenio", 8, 9, "my post 8 description", -1, Visibility.PUBLIC),
            PostDAO(0, "deugenio", 9, 10, "my post 9 description", 1, Visibility.FRIEND_ONLY),
            PostDAO(0, "deugenio", 10, 11, "my post 10 description", -1, Visibility.GROUP),
            PostDAO(0, "deugenio", 1, 12, "my post 11 description", 1, Visibility.PUBLIC),
            PostDAO(0, "deugenio", 2, 13, "my post 12 description", 1, Visibility.FRIEND_ONLY),
            PostDAO(0, "deugenio", 3, 14, "my post 13 description", -1, Visibility.GROUP),
            PostDAO(0, "deugenio", 4, 15, "my post 14 description", 1, Visibility.PUBLIC),
            PostDAO(0, "rcosta", 5, 16, "my post 15 description", 1, Visibility.FRIEND_ONLY),
            PostDAO(0, "rcosta", 6, 17, "my post 16 description", 2, Visibility.GROUP),
            PostDAO(0, "rcosta", 7, 18, "my post 17 description", 1, Visibility.PUBLIC),
            PostDAO(0, "rcosta", 8, 19, "my post 18 description", -1, Visibility.FRIEND_ONLY),
            PostDAO(0, "rcosta", 9, 11, "my post 19 description", 1, Visibility.GROUP),
            PostDAO(0, "deugenio", 20, 5, "my post 20 description", 3, Visibility.PUBLIC)
        )

        posts.saveAll(mockposts)
    }
}