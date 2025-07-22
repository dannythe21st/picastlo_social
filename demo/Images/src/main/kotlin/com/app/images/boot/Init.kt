package com.app.images.boot

import com.app.images.data.ImageDAO
import com.app.images.data.ImageRepository
import com.app.images.data.Visibility
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class Init(val images: ImageRepository): CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {

        val path = Paths.get("./mockImages").toAbsolutePath().toString()

        val mockimages = listOf(
            ImageDAO(0, File(path + File.separatorChar + "image.png").readBytes(), "rcosta", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image2.png").readBytes(), "deugenio", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image3.png").readBytes(), "rcosta", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image4.png").readBytes(), "deugenio", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image5.png").readBytes(), "rcosta", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image6.png").readBytes(), "deugenio", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image7.png").readBytes(), "rcosta", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image8.png").readBytes(), "deugenio", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image9.png").readBytes(), "rcosta", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image10.png").readBytes(), "deugenio", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image.png").readBytes(), "rcosta", Visibility.PUBLIC, 2),
            ImageDAO(0, File(path + File.separatorChar + "image2.png").readBytes(), "deugenio", Visibility.PUBLIC, -1),
            ImageDAO(0, File(path + File.separatorChar + "image3.png").readBytes(), "rcosta", Visibility.PUBLIC, -1),
            ImageDAO(0, File(path + File.separatorChar + "image4.png").readBytes(), "deugenio", Visibility.PUBLIC, -1),
            ImageDAO(0, File(path + File.separatorChar + "image5.png").readBytes(), "rcosta", Visibility.PUBLIC, -1),
            ImageDAO(0, File(path + File.separatorChar + "image6.png").readBytes(), "deugenio", Visibility.PUBLIC, -1),
            ImageDAO(0, File(path + File.separatorChar + "image7.png").readBytes(), "rcosta", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image8.png").readBytes(), "deugenio", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image9.png").readBytes(), "rcosta", Visibility.PUBLIC, 1),
            ImageDAO(0, File(path + File.separatorChar + "image11.png").readBytes(), "deugenio", Visibility.PUBLIC, 1)
        )
        images.saveAll(mockimages)
    }
}