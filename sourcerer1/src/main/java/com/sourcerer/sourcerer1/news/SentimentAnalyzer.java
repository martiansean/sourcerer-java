package com.sourcerer.sourcerer1.news;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SentimentAnalyzer {
    private String prompt = """
        Perform a sentiment analysis on the following news article and
        and output as tags where:
        
        <Name>organization or business or entity name</Name>
        <Sentiment>Positive, negative or neutral</Sentiment>

        Do not output anything but the tags. 

        :
        """;

    public Analysis output(Article news) {
        try {
            // Create URL object
            URI ExternalURL = new URI("http://localhost:4000/generate");
            URL url = ExternalURL.toURL();

            // Create connection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            connection.setRequestMethod("POST");

            // Enable input and output streams
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/json");

            String promptString = "\"" + prompt + news + "\"";
            // Create request body
            String requestBody = "{\"content\": " + promptString + "}"; // Replace with your actual request body

            // Write request body to output stream
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();

            // Get response code
            int responseCode = connection.getResponseCode();

            // Read response body
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Close connection
            connection.disconnect();

            return new Analysis(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in sentiment analysis");
        }
    }
}