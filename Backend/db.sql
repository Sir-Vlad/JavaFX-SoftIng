BEGIN;
--
-- Create model Impiegato
--
CREATE TABLE "Backend_IngSoft_impiegato"
(
    "id"       integer      NOT NULL PRIMARY KEY AUTOINCREMENT,
    "email"    varchar(100) NOT NULL UNIQUE,
    "password" varchar(20)  NOT NULL,
    "nome"     varchar(20)  NOT NULL,
    "cognome"  varchar(20)  NOT NULL
);
--
-- Create model ModelloAuto
--
CREATE TABLE "Backend_IngSoft_modelloauto"
(
    "id"                integer     NOT NULL PRIMARY KEY AUTOINCREMENT,
    "nome"              varchar(20) NOT NULL UNIQUE,
    "marca"             varchar(20) NOT NULL,
    "prezzo_base"       integer     NOT NULL,
    "altezza"           integer     NOT NULL,
    "lunghezza"         integer     NOT NULL,
    "larghezza"         integer     NOT NULL,
    "peso"              integer     NOT NULL,
    "volume_bagagliaio" integer     NOT NULL
);
--
-- Create model Optional
--
CREATE TABLE "Backend_IngSoft_optional"
(
    "id"          integer     NOT NULL PRIMARY KEY AUTOINCREMENT,
    "nome"        varchar(20) NOT NULL,
    "descrizione" varchar(30) NOT NULL,
    "prezzo"      integer     NOT NULL
);
--
-- Create model Sede
--
CREATE TABLE "Backend_IngSoft_sede"
(
    "id"     integer     NOT NULL PRIMARY KEY AUTOINCREMENT,
    "nome"   varchar(20) NOT NULL UNIQUE,
    "via"    varchar(20) NOT NULL,
    "civico" varchar(5)  NOT NULL,
    "citta"  varchar(20) NOT NULL,
    "cap"    varchar(20) NOT NULL
);
--
-- Create model Utente
--
CREATE TABLE "Backend_IngSoft_utente"
(
    "id"            integer      NOT NULL PRIMARY KEY AUTOINCREMENT,
    "email"         varchar(100) NOT NULL UNIQUE,
    "password"      varchar(20)  NOT NULL,
    "nome"          varchar(20)  NOT NULL,
    "cognome"       varchar(20)  NOT NULL,
    "numero_carta"  varchar(20)  NOT NULL,
    "data_scadenza" date         NOT NULL,
    "cvc"           varchar(3)   NOT NULL
);
--
-- Create model Configurazione
--
CREATE TABLE "Backend_IngSoft_configurazione"
(
    "id"                integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "modello_id"        bigint  NOT NULL REFERENCES "Backend_IngSoft_modelloauto" ("id") DEFERRABLE INITIALLY DEFERRED,
    "cambio_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "colore_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "dim_cerchi_id"     bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "fanali_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "finestrini_id"     bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "infotainment_id"   bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "motorizzazione_id" bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "scarico_id"        bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "sedili_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "sospensioni_id"    bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED,
    "stereo_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Periodo
--
CREATE TABLE "Backend_IngSoft_periodo"
(
    "id"   integer    NOT NULL PRIMARY KEY AUTOINCREMENT,
    "mese" varchar(9) NOT NULL,
    "anno" integer    NOT NULL
);
--
-- Create model Preventivo
--
CREATE TABLE "Backend_IngSoft_preventivo"
(
    "id"                integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "data_emissione"    date    NOT NULL,
    "prezzo"            integer NOT NULL,
    "configurazione_id" bigint  NOT NULL REFERENCES "Backend_IngSoft_configurazione" ("id") DEFERRABLE INITIALLY DEFERRED,
    "sede_id"           bigint  NOT NULL REFERENCES "Backend_IngSoft_sede" ("id") DEFERRABLE INITIALLY DEFERRED,
    "utente_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_utente" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Sconto
--
CREATE TABLE "Backend_IngSoft_sconto"
(
    "id"                 integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "percentuale_sconto" integer NOT NULL,
    "modello_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_modelloauto" ("id") DEFERRABLE INITIALLY DEFERRED,
    "periodo_id"         bigint  NOT NULL REFERENCES "Backend_IngSoft_periodo" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model AutoUsata
--
CREATE TABLE "Backend_IngSoft_autousata"
(
    "id"                    integer     NOT NULL PRIMARY KEY AUTOINCREMENT,
    "modello"               varchar(20) NOT NULL,
    "marca"                 varchar(20) NOT NULL,
    "prezzo"                integer     NOT NULL,
    "km_percorsi"           integer     NOT NULL,
    "anno_immatricolazione" date        NOT NULL,
    "altezza"               integer     NOT NULL,
    "lunghezza"             integer     NOT NULL,
    "larghezza"             integer     NOT NULL,
    "peso"                  integer     NOT NULL,
    "volume_bagagliaio"     integer     NOT NULL,
    "utente_id"             bigint      NOT NULL REFERENCES "Backend_IngSoft_utente" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Acquisto
--
CREATE TABLE "Backend_IngSoft_acquisto"
(
    "id"             integer     NOT NULL PRIMARY KEY AUTOINCREMENT,
    "numero_fattura" varchar(10) NOT NULL UNIQUE,
    "acconto"        integer     NOT NULL,
    "data_ritiro"    date        NOT NULL,
    "preventivo_id"  bigint      NOT NULL REFERENCES "Backend_IngSoft_preventivo" ("id") DEFERRABLE INITIALLY DEFERRED,
    "utente_id"      bigint      NOT NULL REFERENCES "Backend_IngSoft_utente" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model ImmaginiAutoUsate
--
CREATE TABLE "Backend_IngSoft_immaginiautousate"
(
    "id"      integer      NOT NULL PRIMARY KEY AUTOINCREMENT,
    "path"    varchar(255) NOT NULL,
    "auto_id" bigint       NOT NULL REFERENCES "Backend_IngSoft_autousata" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Gestione
--
CREATE TABLE "Backend_IngSoft_gestione"
(
    "id"            integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "auto_usata_id" bigint  NOT NULL REFERENCES "Backend_IngSoft_autousata" ("id") DEFERRABLE INITIALLY DEFERRED,
    "impiegato_id"  bigint  NOT NULL REFERENCES "Backend_IngSoft_impiegato" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model ImmaginiAutoNuove
--
CREATE TABLE "Backend_IngSoft_immaginiautonuove"
(
    "id"      integer      NOT NULL PRIMARY KEY AUTOINCREMENT,
    "path"    varchar(255) NOT NULL,
    "auto_id" bigint       NOT NULL REFERENCES "Backend_IngSoft_modelloauto" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Possiede
--
CREATE TABLE "Backend_IngSoft_possiede"
(
    "id"          integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "modello_id"  bigint  NOT NULL REFERENCES "Backend_IngSoft_modelloauto" ("id") DEFERRABLE INITIALLY DEFERRED,
    "optional_id" bigint  NOT NULL REFERENCES "Backend_IngSoft_optional" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Detrazione
--
CREATE TABLE "Backend_IngSoft_detrazione"
(
    "id"            integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "auto_usata_id" bigint  NOT NULL UNIQUE REFERENCES "Backend_IngSoft_autousata" ("id") DEFERRABLE INITIALLY DEFERRED,
    "preventivo_id" bigint  NOT NULL UNIQUE REFERENCES "Backend_IngSoft_preventivo" ("id") DEFERRABLE INITIALLY DEFERRED
);
--
-- Create model Ritiro
--
CREATE TABLE "Backend_IngSoft_ritiro"
(
    "id"            integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "preventivo_id" bigint  NOT NULL REFERENCES "Backend_IngSoft_preventivo" ("id") DEFERRABLE INITIALLY DEFERRED,
    "sede_id"       bigint  NOT NULL REFERENCES "Backend_IngSoft_sede" ("id") DEFERRABLE INITIALLY DEFERRED
);
CREATE INDEX "Backend_IngSoft_configurazione_modello_id_2071287f" ON "Backend_IngSoft_configurazione" ("modello_id");
CREATE INDEX "Backend_IngSoft_configurazione_cambio_id_95991f28" ON "Backend_IngSoft_configurazione" ("cambio_id");
CREATE INDEX "Backend_IngSoft_configurazione_colore_id_93710fae" ON "Backend_IngSoft_configurazione" ("colore_id");
CREATE INDEX "Backend_IngSoft_configurazione_dim_cerchi_id_d1102b27" ON "Backend_IngSoft_configurazione" ("dim_cerchi_id");
CREATE INDEX "Backend_IngSoft_configurazione_fanali_id_00c2818e" ON "Backend_IngSoft_configurazione" ("fanali_id");
CREATE INDEX "Backend_IngSoft_configurazione_finestrini_id_eb60a3c3" ON "Backend_IngSoft_configurazione" ("finestrini_id");
CREATE INDEX "Backend_IngSoft_configurazione_infotainment_id_43ae9951" ON "Backend_IngSoft_configurazione" ("infotainment_id");
CREATE INDEX "Backend_IngSoft_configurazione_motorizzazione_id_d9d2e243" ON "Backend_IngSoft_configurazione" ("motorizzazione_id");
CREATE INDEX "Backend_IngSoft_configurazione_scarico_id_1c971de8" ON "Backend_IngSoft_configurazione" ("scarico_id");
CREATE INDEX "Backend_IngSoft_configurazione_sedili_id_7ed2e863" ON "Backend_IngSoft_configurazione" ("sedili_id");
CREATE INDEX "Backend_IngSoft_configurazione_sospensioni_id_dc139baf" ON "Backend_IngSoft_configurazione" ("sospensioni_id");
CREATE INDEX "Backend_IngSoft_configurazione_stereo_id_8f9f1397" ON "Backend_IngSoft_configurazione" ("stereo_id");
CREATE UNIQUE INDEX "Backend_IngSoft_periodo_mese_anno_4207ab8a_uniq" ON "Backend_IngSoft_periodo" ("mese", "anno");
CREATE INDEX "Backend_IngSoft_preventivo_configurazione_id_b282ba9c" ON "Backend_IngSoft_preventivo" ("configurazione_id");
CREATE INDEX "Backend_IngSoft_preventivo_sede_id_c3034b8d" ON "Backend_IngSoft_preventivo" ("sede_id");
CREATE INDEX "Backend_IngSoft_preventivo_utente_id_463906c0" ON "Backend_IngSoft_preventivo" ("utente_id");
CREATE INDEX "Backend_IngSoft_sconto_modello_id_fab18208" ON "Backend_IngSoft_sconto" ("modello_id");
CREATE INDEX "Backend_IngSoft_sconto_periodo_id_3fb2edb2" ON "Backend_IngSoft_sconto" ("periodo_id");
CREATE INDEX "Backend_IngSoft_autousata_utente_id_ce9b25a2" ON "Backend_IngSoft_autousata" ("utente_id");
CREATE INDEX "Backend_IngSoft_acquisto_preventivo_id_7424cf5f" ON "Backend_IngSoft_acquisto" ("preventivo_id");
CREATE INDEX "Backend_IngSoft_acquisto_utente_id_4c9b9698" ON "Backend_IngSoft_acquisto" ("utente_id");
CREATE UNIQUE INDEX "Backend_IngSoft_immaginiautousate_path_auto_id_56427288_uniq" ON "Backend_IngSoft_immaginiautousate" ("path", "auto_id");
CREATE INDEX "Backend_IngSoft_immaginiautousate_auto_id_4a45386e" ON "Backend_IngSoft_immaginiautousate" ("auto_id");
CREATE UNIQUE INDEX "Backend_IngSoft_gestione_impiegato_id_auto_usata_id_8ae4c1e3_uniq" ON "Backend_IngSoft_gestione" ("impiegato_id", "auto_usata_id");
CREATE INDEX "Backend_IngSoft_gestione_auto_usata_id_6d9c7885" ON "Backend_IngSoft_gestione" ("auto_usata_id");
CREATE INDEX "Backend_IngSoft_gestione_impiegato_id_7a6a9f0c" ON "Backend_IngSoft_gestione" ("impiegato_id");
CREATE UNIQUE INDEX "Backend_IngSoft_immaginiautonuove_path_auto_id_a434eb87_uniq" ON "Backend_IngSoft_immaginiautonuove" ("path", "auto_id");
CREATE INDEX "Backend_IngSoft_immaginiautonuove_auto_id_fc80358d" ON "Backend_IngSoft_immaginiautonuove" ("auto_id");
CREATE UNIQUE INDEX "Backend_IngSoft_possiede_modello_id_optional_id_78621643_uniq" ON "Backend_IngSoft_possiede" ("modello_id", "optional_id");
CREATE INDEX "Backend_IngSoft_possiede_modello_id_125b63b7" ON "Backend_IngSoft_possiede" ("modello_id");
CREATE INDEX "Backend_IngSoft_possiede_optional_id_132ab543" ON "Backend_IngSoft_possiede" ("optional_id");
CREATE UNIQUE INDEX "Backend_IngSoft_detrazione_preventivo_id_auto_usata_id_aef1a250_uniq" ON "Backend_IngSoft_detrazione" ("preventivo_id", "auto_usata_id");
CREATE UNIQUE INDEX "Backend_IngSoft_ritiro_preventivo_id_sede_id_b4c7ea74_uniq" ON "Backend_IngSoft_ritiro" ("preventivo_id", "sede_id");
CREATE INDEX "Backend_IngSoft_ritiro_preventivo_id_8318eb51" ON "Backend_IngSoft_ritiro" ("preventivo_id");
CREATE INDEX "Backend_IngSoft_ritiro_sede_id_b9adf97c" ON "Backend_IngSoft_ritiro" ("sede_id");
COMMIT;
