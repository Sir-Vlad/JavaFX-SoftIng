package it.prova.javafxsofting.errori;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;
import it.prova.javafxsofting.serializzatori.ErrorResponseDeserializer;

@Data
@JsonAdapter(ErrorResponseDeserializer.class)
public class ErrorResponse implements Serializable {
  @SerializedName("email")
  private String message;
}
