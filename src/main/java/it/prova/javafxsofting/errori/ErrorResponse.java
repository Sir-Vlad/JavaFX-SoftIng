package it.prova.javafxsofting.errori;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

@Data
public class ErrorResponse implements Serializable {
  @SerializedName("email")
  private String message;
}
