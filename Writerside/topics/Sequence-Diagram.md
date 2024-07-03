# Use case Diagram

```plantuml
@startuml
:Impiegato: as I

I --> (Gestione Preventivi)
I --> (Gestione Ordini)
@enduml
```

```plantuml
@startuml
:Segreteria: as S

S --> (Aggiunge Modelli Auto)
S --> (Aggiunge Optional)
S --> (Aggiunge Concessionari)
S --> (Visualizzare Ordini)
@enduml
```

```plantuml
@startuml
:Utente: as U

U --> (Login)
U --> (Registrazione)
U --> (Configurazione Auto)
U --> (Richiesta Preventivo)
U --> (Richiesta Valutazione Usato)
U --> (Conferma Preventivo)
U --> (Acquisto Usato)
@enduml
```

## Sequence Diagram

### Login

```plantuml
@startuml
title Login
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b

a ->> l: Inserimento email/password

activate l
a -> l: login()
l -> b: retrieve(utente)

alt Utente registrato
    activate b
    b -->> l: 200 HTTP - utente
    deactivate b
    l -->> a: Accesso effettuato con successo
else Utente inesistente
    activate b
    b -->> l: 404 HTTP - Not Found
    deactivate b
    l -->> a: Accesso negato
end
deactivate l
@enduml
```

### Registrazione

```plantuml
@startuml
title Registrazione
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b

a ->> l: Inserimento dati

activate l
a -> l: registrazione()
l ->> l: validazione dati
l -> b: send(user)

alt Utente registrato
    b -->> l: 201 HTTP - utente
    l -->> a: Registrazione effettuata con successo
else Utente già esistente
    b -->> l: 409 HTTP - Conflict
    l -->> a: Errore
end
deactivate
@enduml
```

### Configurazione

```plantuml
@startuml
title Configurazione
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l

a -> l: scegli modello
l -->> a: modello selezionato

opt scelta optional
    a -> l: scelta optional
    l -->> a: prezzo
end

a -> l: scelta concessionario
l -->> a: concessionario

opt detrazione
    a -> l: detrazione
    ref over l: Vendi Usato
    l --> a: conferma
end
@enduml
```

### Preventivo

```plantuml
@startuml
title Preventivo
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b

ref over a: Configurazione
a -> l: click salva configurazione
alt utente autenticato
    l -> l
else utente non autenticato
    ref over l: Login
end

l -> b: send(configurazione)
alt Preventivo registrato
    activate b
    b -->> l: HTTP 201 - Preventivo creato
    deactivate b
    l -->> a: conferma
else Preventivo formattato male
    activate b
    b -->> l: HTTP 400 - Bab Request
    deactivate b
    l -->> a: errore
else Preventivo già esistente
    activate b
    b -->> l: HTTP 409 - Conflict
    deactivate b
    l -->> a: errore
end
@enduml
```

### Vendi Usato

```plantuml
@startuml
title Vendi Auto Usata
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b

a ->> l: inserisci dati
a -> l: click salva auto usata

alt utente autenticato
    l -> l
else utente non autenticato
    ref over l: Login
end

l -> l: validazione dati
l -> b: send(autoUsata)
break Error Request
    b -->> l: HTTP 400 - Bab Request
    l -->> a: errore
end
b -->> l: HTTP 201 - Auto usata registrata
l -->> a: conferma
@enduml
```

### Conferma Preventivo

```plantuml
@startuml
title Conferma Preventivo
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b
database Database as d

a ->> l: click conferma preventivo
a ->> l: inserisci acconto
a -> l: click conferma acconto
l -> b: send(acconto)
b -> d: check preventivo
break Error Request
    d -->> b: preventivo non presente
    b -->> l: HTTP 404 - Not Found
    l -->> a: errore
end
d -->> b: preventivo presente

group transazione
    b -->> d: inserisci Ordine
    b -->> d: modifica Preventivo\n in pagato
    b -->> b: createPdf()
    b -->> a: sendEmail(ordine, pdf) 
end
note right: Le operazioni nella\n transazione devono\n avere tutte esito positivo\n altrimenti da errore.
alt transazione riuscita
    b-->> l: HTTP 201 - Ordine creato
    l-->> a: conferma
else errore nella transazione
    b -->> l: HTTP 400 - Bad Request
    l -->> a: errore
end
@enduml
```

### Acquista Auto Usata

```plantuml
@startuml
title Acquisto Auto Usata
skinparam SequenceMessageAlignment center
actor Attore as a
participant Sistema as l
participant Backend as b

a -> l: scegli auto usata
l -->> a: auto usata selezionata

a -> l: click conferma acquisto
l -> b: sendAcquisto(AutoUsata)
alt auto disponibile
    b -->> l: HTTP 201 - Acquisto effettuato
    l -->> a: conferma
else auto non disponibile
    b -->> l: HTTP 402 - Auto già acquistata 
    l -->> a: errore
end
@enduml
```

```plantuml
@startuml
title Valutazione Preventivo
skinparam SequenceMessageAlignment center
actor Impiegato as a
participant Sistema as l
database Database as b
actor Utente as u

a ->> l: Accesso area riservata
a -> l: Seleziona preventivo
l -->> a: Preventivo selezionato
a ->> l: inserisci valutazione
a -> l: salva Preventivo
l -> b: query di modifica
b -->> l: modifica effettuata
l ->> u: sendEmail()
l -->> a: preventivo salvato con successo
@enduml
```

```plantuml
@startuml
title Modica dati database
skinparam SequenceMessageAlignment center
actor Segreteria as a
participant Sistema as l
database Database as b

a ->> l: Accesso area riservata
a -> l: Seleziona elemento\n da modificare
l -->> a: Elemento selezionato
a -> l: Effettua modifica
l -> b: query di modifica
break Errore nei constraint
    b -->> l: Errore nei constraint
    l -->> a: errore nella modifica
end
    b -->> l: elemento modificato
    l -->> a: elemento modificato
@enduml
```

```plantuml
@startuml
title Inserimento dati database
skinparam SequenceMessageAlignment center
actor Segreteria as a
participant Sistema as l
database Database as b

a ->> l: Accesso area riservata
a -> l: Effettua inserimento
l -> b: query di inserimento
break Errore nei constraint
    b -->> l: Errore nei constraint
    l -->> a: errore nella modifica
end
    b -->> l: elemento inserito
    l -->> a: elemento inserito
@enduml
```

## Diagramma di Attività

```plantuml
@startuml
title Login
start
repeat
:Inserimento\n Credenziali;
backward :Errore;
repeat while (Valide?) is (no);
-> si;
stop
@enduml
```

```plantuml
@startuml
title Utente
skinparam conditionStyle diamond
start
: Visualizzazione\n dell'interfaccia di base;
switch ()
case ()
    :Visualizzazione\n Auto usate;
    if () then
        :Acquisto;
    end if
case ()
    :Visualizzazione\n Area Personale;
    switch ()
    case ()
        :Visualizzazione\n Dati Personali;
        if () then
            :Modifica\n Dati Personali;
        end if
    case ()
        :Visualizzazione\n Preventivi;
        if () then
            :Conferma Preventivo;
        end if
    case ()
        :Visualizzazione\n Ordini;
    endswitch
case ()
    :Visualizzazione\n Auto Nuove;
    if () then
        :Configurazione\n Modello Auto;
        if () then
            :Richiesta\n Valutazione Usato;
        end if
    :Richiesta Preventivo;
    end if
case ()
    :Visualizzazione\n Concessionari;
endswitch
stop
@enduml
```

```plantuml
@startuml
title Impiegato
skinparam conditionStyle diamond
start
:Visualizzazione\n interfaccia di base;
if () then
    :Valutazione usato;
else
    :Visualizza ordini;
endif
stop
@enduml
```

```plantuml
@startuml
title Segreteria
skinparam conditionStyle diamond
start
:Visualizzazione\n interfaccia di base;
switch ()
case ()
    :Visualizza Modelli Auto;
    switch ()
    case ()
        :Modifica\n Modelli Auto;
    case ()
        :Aggiungere\n Modelli Auto;
    case ()
        :Elimina\n Modelli Auto;
    endswitch
case ()
    :Visualizza Optional;
    switch ()
    case ()
        :Modifica\n Optional;
    case ()
        :Aggiungere\n Optional;
    case ()
        :Elimina\n Optional;
    endswitch
case ()
    :Visualizza Concessionari;
    switch ()
    case ()
        :Modifica\n Concessionari;
    case ()
        :Aggiungere\n Concessionari;
    case ()
        :Elimina\n Concessionari;
    endswitch
case ()
    :Gestione Ordini;
endswitch
stop
@enduml
```

## Processo di sviluppo

```plantuml
@startuml

start
:Elicitazione ed analisi\n dei requisiti;
note right:Diagramma UML: use-cases ed\n activity diagram
:Progettazione architetturale;
repeat
    fork
        :Integrazione della\n documentazione;
        note left: Documentazione\n complessiva;
    fork again
        repeat
            :Progettazione\n interna;
            note right: UML: class diagram
            :implementazione;
            note right: Versione
        repeat while ()
        :Test e validazione;
        note right: Descrizione dei test
    end fork
repeat while ()
:Deploy e test;
end
@enduml
```