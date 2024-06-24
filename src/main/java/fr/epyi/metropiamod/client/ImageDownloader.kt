package fr.epyi.metropiamod.client

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

object ImageDownloader {
    fun downloadImage(imageUrl: String?): BufferedImage? {
        val httpClient: HttpClient = HttpClients.createDefault()
        val httpGet = HttpGet(imageUrl)

        try {
            // Exécution de la requête
            val response = httpClient.execute(httpGet)
            val entity = response.entity

            if (entity != null) {
                // Récupération du contenu de l'image sous forme de flux
                val inputStream = entity.content

                // Lecture de l'image à partir du flux
                val image = ImageIO.read(inputStream)

                // Fermeture du flux
                inputStream.close()

                return image
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}