package it.prova.javafxsofting.serializzatori;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class LocalDateSerializer implements JsonSerializer<LocalDate> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Contract("_, _, _ -> new")
  @Override
  public @NotNull JsonElement serialize(
      LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(formatter.format(src));
  }
}
