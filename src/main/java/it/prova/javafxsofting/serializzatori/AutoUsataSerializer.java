package it.prova.javafxsofting.serializzatori;

import com.google.gson.*;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.AutoUsata;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public class AutoUsataSerializer implements JsonSerializer<AutoUsata> {
  @Override
  public JsonElement serialize(
      @NotNull AutoUsata src, Type typeOfSrc, @NotNull JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    JsonElement utente = context.serialize(UserSession.getInstance().getUtente().getId());
    jsonObject.add("utente", utente);

    JsonObject autoUsata = new JsonObject();
    autoUsata.addProperty("modello", src.getModello());
    autoUsata.addProperty("marca", src.getMarca().name());
    autoUsata.addProperty("km_percorsi", src.getKmPercorsi());
    autoUsata.addProperty("targa", src.getTarga());

    JsonElement aaImmatricolazione = context.serialize(src.getAnnoImmatricolazione());
    autoUsata.addProperty("anno_immatricolazione", aaImmatricolazione.getAsString());
    autoUsata.addProperty("altezza", src.getAltezza());
    autoUsata.addProperty("lunghezza", src.getLunghezza());
    autoUsata.addProperty("larghezza", src.getLarghezza());
    autoUsata.addProperty("peso", src.getPeso());
    autoUsata.addProperty("volume_bagagliaio", src.getVolumeBagagliaio());
    jsonObject.add("auto", autoUsata);
    return jsonObject;
  }
}
