package it.prova.javafxsofting.serializzatori;

import com.google.gson.*;
import it.prova.javafxsofting.errori.ErrorResponse;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public final class ErrorResponseDeserializer implements JsonDeserializer<ErrorResponse> {
  @Override
  public @NotNull ErrorResponse deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    ErrorResponse errorResponse = new ErrorResponse();
    JsonObject jsonObject = json.getAsJsonObject();

    try {
      String message = jsonObject.get("message").getAsString();
      errorResponse.setMessage(message);
    } catch (JsonParseException e) {
      throw new RuntimeException(e);
    }
    return errorResponse;
  }
}
