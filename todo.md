# Fattura

Abbiamo bisogno di due template di fatture

- uno per il nuovo
- uno per l'usato

I template c'è li creiamo in `html` e poi con `jinja2` e `pdfkit` creiamo il pdf da dare all'utente.

> NB. Guardare anche `ironpdf`

# Aggiungere elementi custom a Scene Builder

- prendere il file .jar in target
- copiarlo in /home/maty/.scenebuilder/Library/

# Query

Utilizzare i dataframe di Apache Spark. Questa libreria è disponibile sia per Python che per Java.

# REST API

generare un token in python che mi passo tramite le richieste.

###### Risorse

- https://www.baeldung.com/java-httpclient-basic-auth
- https://forum.jmix.io/t/how-to-authenticate-in-rest-from-java-code/1525/2
- https://dev.to/noelopez/http-client-api-in-java-part-2-75e

# Aggiungere i font nel progetto

# Response Json multi object

```java
Gson gson = new Gson();
String json = "..."; // your JSON string
Type type = new TypeToken<List<User>>() {}.getType();
List<User> users = gson.fromJson(json, type);
```

# Aggiungere gli hash alle immagini