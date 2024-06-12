# Endpoints API documentation

## Utente

- `GET api/utenti`: restituisce lista di tutti gli utenti
- `POST api/utenti`: creare uno o più utenti

    ```json
    [
     {
      "email": "...",
      "password": "...",
      "nome": "...",
      "cognome": "...",
      "indirizzo": "...",
      "numero_telefono": "...",
      "numero_carta": "...",
      "data_scadenza": "...",
      "cvc": "..."
     }
   ]
  ```
    - campi facoltativi:
        - indirizzo
        - numero_telefono
- `GET api/utente/<str:email>/`: dettagli utente
- `PUT api/utente/<str:email>/`: modifica utente
- `DELETE api/utente/<str:email>/`: elimina utente

## Modelli Auto

- `GET api/modelli/`: restituisce tutti i modelli di auto in catalogo

## Optionals

- `GET api/optionals/`

## Auto Usate

- `GET api/autoUsate/`
- `POST api/autoUsate/`
    ```json
    {
      "utente": "...",
      "auto":{
          "modello": "...",
          "marca": "...",
          "altezza": "..",
          "lunghezza": "...",
          "larghezza": "...",
          "peso": "...",
          "volume_bagagliaio": "...",
          "km_percorsi": "...",
          "anno_immatricolazione": "...",
          "targa": "..."
      }
    } 
    ```

## Immagini Modelli Auto

- `GET api/immaginiAutoNuove/<int:id>/`

## Immagini Auto Usate

- `GET api/immaginiAutoUsate/<int:id>/`
- `POST api/immaginiAutoUsate/<int:id>/`
    - NOT IMPLEMENTED

  [//]: # (TODO: not implemented)

## Preventivo

- `GET api/preventivi/`
- `GET api/utente/<int:id>/preventivi/`
- `POST api/utente/<int:id>/preventivi/`
    ```json
    {
      "preventivo": {
          "utente": 1,
          "concessionario": 1,
          "modello": 1,
          "prezzo": 20000,
          "data_emissione": "2023-11-20"
      },
      "optional": [1,5,10]
    }
    ```

    - **preventivo**: campo che contiene i campi del preventivo
    - **optional**: campo che contiene gli id degli optional

### Return HTTP Code

- `201 - CREATED`: quando la post è andata a buon fine
- `400 - BAD REQUEST`: errore generico
- `409 - CONFLICT`: quando le chiavi esportate non esistono o ci sono conflitti di unique

## Ordini

- `GET api/utente/<int:id>/ordini/`
- `POST api/utente/<int:id>/ordini/`
    - NOT IMPLEMENTED

## Concessionari

- `GET api/concessionari/`