package it.prova.javafxsofting;

import java.io.Serializable;
import lombok.Data;

@Data
public class ErrorResponse implements Serializable {
  private String message;
}
