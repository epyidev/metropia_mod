package fr.epyi.metropiamod.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownloader {

    public static BufferedImage downloadImage(String imageUrl) {

        if (!isValidUrl(imageUrl)) {
            System.err.println("URL invalide : " + imageUrl);
            return null;
        }

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(imageUrl);

        try {
            // Exécution de la requête
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Récupération du contenu de l'image sous forme de flux
                InputStream inputStream = entity.getContent();

                // Lecture de l'image à partir du flux
                BufferedImage image = ImageIO.read(inputStream);

                // Fermeture du flux
                inputStream.close();

                return image;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isValidUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol().startsWith("http");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}