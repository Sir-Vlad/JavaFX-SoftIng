# json

**`POST /autoUsate/`**

```plantuml
@startjson
{
  "utente": "integer",
  "auto": {
    "modello": "integer",
    "marca": ["NISSAN", "MAZDA", "VOLKSWAGEN", "FORD", "HONDA", "AUDI", "BMW"],
    "lunghezza": "integer",
    "larghezza": "integer",
    "peso": "integer",
    "volume_bagagliaio": "integer",
    "km_percorsi": "integer",
    "anno_immatricolazione": "1900-01-01",
    "targa": "integer"
  }
}
@endjson
```
 
**`POST /immaginiAutoUsate/<id_auto>/`**

```plantuml
@startjson
{
  "image_name": "string",
  "image_base64": "string",
  "auto": "integer"
}
@endjson
```

**`POST /utente/<id_utente>/preventivi/`**

```plantuml
@startjson
{
 "preventivo": {
    "utente": "integer",
    "concessionario": "integer",
    "modello": "integer",
    "prezzo": "integer",
    "data_emissione": "string"
  },
 "optional": [
    1, 3
  ],
 "detrazione": "integer"
}

@endjson
```

**`POST /utente/<id_utente>/preventivo/<id_preventivo>/conferma/`**

```plantuml
@startjson
{"acconto": "integer"}
@endjson
```
 
**`POST /utenti/`**

```plantuml
@startjson
{
  "email": "user@example.com",
  "password": "string",
  "nome": "string",
  "cognome": "string",
  "indirizzo": "string",
  "numero_telefono": "string",
  "numero_carta": "string",
  "data_scadenza": "2024-07-10",
  "cvc": "str"
}
@endjson

```