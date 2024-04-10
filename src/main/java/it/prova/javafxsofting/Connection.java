package it.prova.javafxsofting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Connection {
    
    static int porta = -1;
    
    static void setPorta(int porta) {
        Connection.porta = porta;
    }
    
    /**
     * Invia i dati al backend
     *
     * @param data          dati da inviare
     * @param porta         porta dove aprire la connessione
     * @param sub_directory url dove inviare i dati
     * @param <T>           tipo generico che deve essere serializzatile
     * @return la risposta del backend
     */
    static <T extends Serializable> @NotNull StringBuilder sendDataToBacked(T data, int porta,
                                                                            String sub_directory) {
        if (Connection.porta == -1) {
            Connection.porta = porta;
        }
        
        HttpURLConnection conn = getHttpURLConnection(sub_directory, methods.POST);
        
        // Aggiunge la possibilità di serializzare anche i campi statici
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        
        // Dati da inviare al backend in formato JSON
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
            System.out.println("Errore: nella lettura della request");
        }
        conn.disconnect();
        return response;
    }
    
    /**
     * Crea una connessione verso un URL con una dato metodo
     *
     * @param sub_directory URL della directory dove creare la connessione
     * @param methods       metodo di creazione della connessione
     * @return la connessione
     */
    @NotNull
    private static HttpURLConnection getHttpURLConnection(String sub_directory, methods methods) {
        StringBuilder subdirectory = sub_directory == null ? null : new StringBuilder(
                sub_directory);
        String url_path = String.format("http://localhost:%d%s", Connection.porta,
                subdirectory == null ? "" : subdirectory.insert(0, '/'));
        
        HttpURLConnection conn;
        try {
            URI uri = URI.create(url_path);
            URL url = uri.toURL(); // URL del backend Python
            conn = (HttpURLConnection) url.openConnection();
            // setto il metodo (GET, POST, DELETE, PUT)
            conn.setRequestMethod(methods.toString());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Non è stato possibile instaurare una connessione con il Backend");
        }
        return conn;
    }
    
    enum methods {
        GET, POST, DELETE, PUT
    }
    
}