package it.prova.javafxsofting.serializzatori;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.prova.javafxsofting.models.Ordine;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class OrdineSerializer implements JsonSerializer<Ordine> {

  @Override
  public JsonElement serialize(
      @NotNull Ordine src, Type typeOfSrc, @NotNull JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    JsonElement acconto = context.serialize(src.getAcconto());
    jsonObject.add("acconto", acconto);

    return jsonObject;
  }
}
