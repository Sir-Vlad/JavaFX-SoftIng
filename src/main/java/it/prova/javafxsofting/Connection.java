package it.prova.javafxsofting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Connection {
    
    static int porta = -1;
    
    static void setPorta(int porta) {
        Connection.porta = porta;
    }
    
    static <T extends Serializable> StringBuilder sendDataToBacked(T data, int porta,
                                                                   String sub_directory)
    throws Error {
        if (Connection.porta == -1) {
            Connection.porta = porta;
        }
        
        HttpURLConnection conn = getHttpURLConnection(sub_directory, "POST");
        
        
        // Aggiunge la possibilità di serializzare anche i campi statici
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        
        // Dati da inviare al backend in formato JSON
        // Gson gson = new Gson();
        Gson gson = gsonBuilder.create(); // crea gson con la corrente configurazione
        String jsonInputString = gson.toJson(data);
        
        // invia i dati al backed
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException("Errore nell'invio dei dati");
        }
        
        // riceve la risposta dal backed
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Risposta: " + response); // Stampare la risposta dal backend
        } catch (Exception ignored) {
        }
        if (response.indexOf("error") != -1) {
            throw new Error(String.valueOf(response));
        }
        conn.disconnect();
        return response;
    }
    
    @NotNull
    private static HttpURLConnection getHttpURLConnection(String sub_directory, String methods) {
        StringBuilder subdirectory = new StringBuilder(sub_directory == null ? "" : sub_directory);
        String url_path = String.format("http://localhost:%d%s", Connection.porta,
                subdirectory.isEmpty() ? "" : subdirectory.insert(0, '/'));
        
        HttpURLConnection conn;
        try {
            URL url = new URL(url_path); // URL del backend Python
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(methods);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Non è stato possibile instaurare una connessione con il Backend");
        }
        return conn;
    }
    
}
