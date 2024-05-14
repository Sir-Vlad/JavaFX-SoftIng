package it.prova.javafxsofting;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import it.prova.javafxsofting.errori.ErrorResponse;
import it.prova.javafxsofting.serializzatori.ErrorResponseDeserializer;
import it.prova.javafxsofting.serializzatori.LocalDateDeserializer;
import it.prova.javafxsofting.serializzatori.LocalDateSerializer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Connection {

  public static int porta = -1;

  static void setPorta(int porta) {
    Connection.porta = porta;
  }

  /**
   * Invia i dati al backend
   *
   * @param data dati da inviare
   * @param porta porta dove aprire la connessione
   * @param sub_directory url dove inviare i dati
   * @param <T> tipo generico che deve essere serializzatile
   * @return la risposta del backend
   */
  public static <T extends Serializable> void sendDataToBacked(
      T data, int porta, String sub_directory) throws Exception {
    if (Connection.porta == -1) {
      Connection.porta = porta;
    }

    HttpURLConnection conn = getHttpURLConnection(sub_directory, methods.POST);

    // Dati da inviare al backend in formato JSON
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(ErrorResponse.class, new ErrorResponseDeserializer())
            .setDateFormat(DateFormat.LONG)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setExclusionStrategies(
                new ExclusionStrategy() {
                  @Override
                  public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(Expose.class) != null
                        && !f.getAnnotation(Expose.class).deserialize();
                  }

                  @Override
                  public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                  }
                })
            .create(); // crea gson con la corrente configurazione
    String jsonInputString = gson.toJson(data);

    // invia i dati al backed
    try (OutputStream os = conn.getOutputStream()) {
      byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    } catch (IOException e) {
      throw new RuntimeException("Errore nell'invio dei dati");
    }

    // riceve la risposta dal backed
    int responseCode = conn.getResponseCode();
    System.out.println(responseCode);
    if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
      StringBuilder response = new StringBuilder();
      try (BufferedReader br =
          new BufferedReader(
              new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        System.out.println("Risposta: " + response); // Stampare la risposta dal backend

        // fixme: non riesce a serializzare l'errore
        ErrorResponse errorResponse = gson.fromJson(response.toString(), ErrorResponse.class);
        throw new Exception(errorResponse.getMessage());
      }
    }

    conn.disconnect();
  }

  /**
   * Crea una connessione verso un URL con una dato metodo
   *
   * @param sub_directory URL della directory dove creare la connessione
   * @param methods metodo di creazione della connessione
   * @return la connessione
   */
  @NotNull
  private static HttpURLConnection getHttpURLConnection(String sub_directory, methods methods) {
    StringBuilder subdirectory = sub_directory == null ? null : new StringBuilder(sub_directory);
    String url_path =
        String.format(
            "http://localhost:%d/api/%s",
            Connection.porta, subdirectory == null ? "" : subdirectory);

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
      throw new RuntimeException("Non è stato possibile instaurare una connessione con il Backend");
    }
    return conn;
  }

  public static <T extends Serializable> @Nullable T getData(String path, Class<T> objClass)
      throws Exception {
    HttpURLConnection conn = getHttpURLConnection(path, methods.GET);
    int statusCode = conn.getResponseCode();
    StringBuilder content = new StringBuilder();

    System.out.println(statusCode);

    if (statusCode == 200) {
      InputStream inputStream = conn.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }

      Gson gson =
          new GsonBuilder()
              .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
              .setDateFormat(DateFormat.LONG)
              .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
              .setExclusionStrategies(
                  new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                      return f.getAnnotation(Expose.class) != null
                          && !f.getAnnotation(Expose.class).deserialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                      return false;
                    }
                  })
              .create();

      return gson.fromJson(content.toString(), objClass);
    } else if (statusCode == 404) {
      InputStream inputStream = conn.getErrorStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }
      throw new Exception(content.toString());
    }
    conn.disconnect();
    return null;
  }

  enum methods {
    GET,
    POST,
    DELETE,
    PUT
  }
}
