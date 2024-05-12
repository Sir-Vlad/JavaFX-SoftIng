# Sistema di Concessionari

## Descrizione
Il progetto è un sistema di ventita online di auto di un gruppo multi-concessionari, sviluppata come parte del corso di Ingegneria del Software 2023/24 presso l'Università di Verona. L'applicazione permette di configurare l'auto nuova da comprare ma anche poter vendere l'usato per ottenere una detrazione sull'auto nuova da comprare. Le auto usate vengono poi messe in vendita.

## Componenti del Team
- @Sir-Vlad
- @magnificorettoreertyu
- @djacoo

## Tecnologie usate
- linguaggio di programmzione: Java
- framework gui: JavaFX
- Backend: Django/Python
- Database: MySQL
- Strumenti di sviluppo: IntelliJ IDEA, PyCham

## Funzionalità Principali

1. **Configurazione dell'auto**: L'utente, dopo aver scelto il modello base, può configurazione l'auto secondo i possibili optional che sono a disposizione

2. **Vendita auto usata**: L'utente può inserire l'auto da vendere per avere una detrazione sull'acquisto di modello nuovo

3. **Gestione dei Preventivi ed degli Acqisti**: Una volta confermato la configurazion viene creato un preventivo che ha valenza di 20 giorni dal momento della conferma. Passati i 20 giorni il preventivo deve essere confermato tramite il pagamento di un acconto, in caso contrario, il preventivo verrà annulato

4. **Gestione degli utenti**: Gli utenti si possono loggare dopo che si sono registrati nel sistema. Questo è necessario per poter acquistare e vendere le auto.

## Lincenza

Il progetto è rilasciato sotto la licenza MIT. Consultare il file `LICENSE` per ulteriori informazioni.

## Note

Questo progetto è stato sviluppato come parte di un corso universitario e potrebbe essere soggetto a modifiche e miglioramenti futuri. (Forse)