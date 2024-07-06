package it.prova.javafxsofting;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.ImmagineAuto;
import it.prova.javafxsofting.models.Ordine;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.serializzatori.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Connection {
  private static final Logger logger = Logger.getLogger(Connection.class.getName());
  public static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
          .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
          .registerTypeAdapter(Preventivo.class, new PreventivoSerializer())
          .registerTypeAdapter(Preventivo.class, new PreventivoDeserializer())
          .registerTypeAdapter(AutoUsata.class, new AutoUsataSerializer())
          .registerTypeAdapter(Ordine.class, new OrdineSerializer())
          .setPrettyPrinting()
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
  @Getter @Setter private static int porta = -1;

  @Contract(value = " -> fail", pure = true)
  private Connection() {
    throw new UnsupportedOperationException("This class is not supported");
  }

  public static boolean deleteDataToBackend(String subDirectory) throws IOException {
    if (Connection.porta == -1) {
      throw new RuntimeException("Connessione non disponibile");
    }

    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.DELETE);

    int response = conn.getResponseCode();
    if (response == HttpURLConnection.HTTP_NO_CONTENT) {
      return true;
    }
    conn.disconnect();
    return false;
  }

  public static <T extends Serializable> @Nullable List<T> getArrayDataFromBackend(
      String subDirectory, Class<T> objClass) throws Exception {
    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.GET);
    int statusCode = conn.getResponseCode();
    StringBuilder content = new StringBuilder();

    if (statusCode == 200) {
      InputStream inputStream = conn.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }
      Type type = TypeToken.getParameterized(List.class, objClass).getType();
      return gson.fromJson(content.toString(), type);
    } else if (statusCode == 404) {
      error404Connection(conn, content);
    }
    conn.disconnect();
    return null;
  }

  /**
   * @param subDirectory sottodomio dove fare la get
   * @param objClass classe della risposta della get per eseguire la deserializzazione
   * @return json object deserializzato
   * @param <T> tipo dell'oggetto deserializzato
   * @throws Exception errore nella connessione oppure nella risposta della get
   */
  public static <T extends Serializable> @Nullable T getDataFromBackend(
      String subDirectory, Class<T> objClass) throws Exception {
    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.GET);
    int statusCode = conn.getResponseCode();
    StringBuilder content = new StringBuilder();

    if (statusCode == 200) {
      InputStream inputStream = conn.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }

      conn.disconnect();

      if (content.isEmpty()) {
        return null;
      }

      return gson.fromJson(content.toString(), objClass);
    } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
      error404Connection(conn, content);
    }
    conn.disconnect();
    return null;
  }

  public static <T extends Serializable> List<T> getImageFromBackend(
      String subDirectory, Class<T> objClass) throws Exception {
    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.GET);
    int statusCode = conn.getResponseCode();
    StringBuilder content = new StringBuilder();

    if (statusCode == 200) {
      InputStream inputStream = conn.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }

      if (content.toString().equals("[]")) {
        return Collections.emptyList();
      }

      Type type = TypeToken.getParameterized(List.class, objClass).getType();
      return gson.fromJson(content.toString(), type);
    }
    conn.disconnect();
    return Collections.emptyList();
  }

  /**
   * Invia i dati al backend
   *
   * @param <T> tipo generico che deve essere serializzatile
   * @param data dati da inviare
   * @param subDirectory url dove inviare i dati
   * @throws Exception errore nella connessione
   */
  public static <T extends Serializable> void postDataToBacked(T data, String subDirectory)
      throws Exception {
    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.POST);

    // Dati da inviare al backend in formato JSON
    String jsonInputString = gson.toJson(data);

    logger.info(jsonInputString);

    // invia i dati al backed
    sendData(conn, jsonInputString);

    // riceve la risposta dal backed
    int responseCode = conn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_CREATED) {
      conn.disconnect();
      return;
    }
    if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
      conn.disconnect();
      throw new Exception("Errore del server");
    }
    StringBuilder response = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    } catch (IOException e) {

    }
    conn.disconnect();

    throw new Exception(response.toString());
  }

  /**
   * Esegue una post per inviare una lista di immagini
   *
   * @param idAutoUsata id dell'auto usata
   * @param immagini lista di immagini
   * @param s url dove inviare i dati
   * @throws Exception errore nella connessione
   */
  public static void postImmaginiAutoUsateToBacked(int idAutoUsata, List<File> immagini, String s)
      throws Exception {
    HttpURLConnection conn = getHttpURLConnection(s + idAutoUsata + "/", Methods.POST);

    List<ImmagineAuto> immaginiAuto = new ArrayList<>();
    for (File immagine : immagini) {
      immaginiAuto.add(ImmagineAuto.create(idAutoUsata, immagine));
    }

    // Dati da inviare al backend in formato JSON
    String jsonInputString = gson.toJson(immaginiAuto);

    // invia i dati al backed
    sendData(conn, jsonInputString);

    int responseCode = conn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_CREATED) {
      conn.disconnect();
      return;
    }
    StringBuilder response = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    conn.disconnect();
    throw new Exception(response.toString());
  }

  /**
   * Esegue una put all'interno del backend
   *
   * @param data dati da inviare
   * @param subDirectory url dove inviare i dati
   * @param <T> tipo generico che deve essere serializzatile
   * @throws Exception errore nella connessione
   */
  public static <T extends Serializable> void putDataToBackend(T data, String subDirectory)
      throws Exception {
    HttpURLConnection conn = getHttpURLConnection(subDirectory, Methods.PUT);

    // Dati da inviare al backend in formato JSON
    String jsonInputString = gson.toJson(data);

    // invia i dati al backed
    sendData(conn, jsonInputString);

    // riceve la risposta dal backed
    int responseCode = conn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      conn.disconnect();
      return;
    }

    StringBuilder response = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }

    conn.disconnect();
    throw new RuntimeException(response.toString());
  }

  private static void error404Connection(@NotNull HttpURLConnection conn, StringBuilder content)
      throws Exception {
    InputStream inputStream = conn.getErrorStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      content.append(line);
    }
    throw new RuntimeException(content.toString());
  }

  /**
   * Invia i dati al backend
   *
   * @param conn connessione HTTP
   * @param jsonInputString dati da inviare
   */
  private static void sendData(@NotNull HttpURLConnection conn, @NotNull String jsonInputString) {
    try (OutputStream os = conn.getOutputStream()) {
      byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    } catch (IOException e) {
      throw new RuntimeException("Errore nell'invio dei dati");
    }
  }

  /**
   * Crea una connessione verso un URL con una dato metodo
   *
   * @param subDirectory URL della directory dove creare la connessione
   * @param methods metodo di creazione della connessione
   * @return la connessione
   */
  @NotNull
  private static HttpURLConnection getHttpURLConnection(String subDirectory, Methods methods) {
    StringBuilder subdirectory = subDirectory == null ? null : new StringBuilder(subDirectory);
    //    String urlPath =
    //        String.format(
    //            "http://localhost:%d/api/%s",
    //            Connection.porta, subdirectory == null ? "" : subdirectory);

    String urlPath =
        String.format(
            "https://SirVlad33.pythonanywhere.com/api/%s",
            subdirectory == null ? "" : subdirectory);

    HttpURLConnection conn;
    try {
      URI uri = URI.create(urlPath);
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

  enum Methods {
    GET,
    POST,
    DELETE,
    PUT
  }
}
