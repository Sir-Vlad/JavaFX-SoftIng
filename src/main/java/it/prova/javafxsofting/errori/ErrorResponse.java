package it.prova.javafxsofting.errori;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ErrorResponse implements Serializable {
  private Map<String, List<String>> errors;
}
