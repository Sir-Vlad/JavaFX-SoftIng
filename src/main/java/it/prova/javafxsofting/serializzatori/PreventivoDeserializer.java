package it.prova.javafxsofting.serializzatori;

import com.google.gson.*;
import it.prova.javafxsofting.models.Preventivo;
import java.lang.reflect.Type;

public class PreventivoDeserializer implements JsonDeserializer<Preventivo> {
  @Override
  public Preventivo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();
    Preventivo preventivo;

    int id = jsonObject.get("id").getAsInt();
    int utenteId = jsonObject.get("utente").getAsInt();
    int modelloId = jsonObject.get("modello").getAsInt();
    int concessionarioId = jsonObject.get("concessionario").getAsInt();
    int prezzo = jsonObject.get("prezzo").getAsInt();
    if (jsonObject.get("data_emissione").isJsonNull()) {
      preventivo = new Preventivo(utenteId, modelloId, concessionarioId, prezzo);
    } else {
      String dataEmissione = jsonObject.get("data_emissione").getAsString();
      preventivo = new Preventivo(utenteId, modelloId, concessionarioId, prezzo, dataEmissione);
    }
    preventivo.setId(id);
    preventivo.setStato(jsonObject.get("stato").getAsString());

    JsonArray optionals = jsonObject.get("config").getAsJsonArray();
    int[] idOpt = new int[optionals.size()];
    for (int i = 0; i < optionals.size(); i++) {
      if (optionals.get(i).isJsonNull()) {
        continue;
      }
      int optionalId = optionals.get(i).getAsInt();
      idOpt[i] = optionalId;
    }
    preventivo.setIdRefConfig(idOpt);

    return preventivo;
  }
}
