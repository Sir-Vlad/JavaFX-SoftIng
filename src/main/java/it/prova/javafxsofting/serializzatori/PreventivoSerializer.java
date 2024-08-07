package it.prova.javafxsofting.serializzatori;

import com.google.gson.*;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.scegli_conf_auto.ConfiguratorController;
import it.prova.javafxsofting.models.Preventivo;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public final class PreventivoSerializer implements JsonSerializer<Preventivo> {
  @Override
  public @NotNull JsonElement serialize(
      @NotNull Preventivo src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    JsonObject preventivo = new JsonObject();
    preventivo.addProperty("utente", src.getUtente().getId());
    preventivo.addProperty("modello", src.getModello().getId());
    preventivo.addProperty("concessionario", src.getConcessionario().getId());
    preventivo.addProperty("prezzo", src.getPrezzo());
    if (src.getDataEmissione() != null) {
      preventivo.addProperty("data_emissione", src.getDataEmissione().toString());
    }
    jsonObject.add("preventivo", preventivo);

    JsonArray optionals = new JsonArray();
    for (int i = 0; i < src.getOptionals().size(); i++) {
      optionals.add(src.getOptionals().get(i).getId());
    }
    jsonObject.add("optional", optionals);

    if (ConfiguratorController.isDetrazione()) {
      JsonElement detrazione =
          context.serialize(
              UserSession.getInstance().getPreventiviUsati().getLast().getIdAutoUsata());
      jsonObject.add("detrazione", detrazione);
    }

    return jsonObject;
  }
}
