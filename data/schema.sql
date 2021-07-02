CREATE TABLE NUTZER(
	[email]			VARCHAR		NOT NULL	COLLATE NOCASE,
	[vorname]			VARCHAR		NOT NULL,
	[nachname]			VARCHAR		NOT NULL,
	[geburtsdatum]		DATETIME	NOT NULL,
	[passwort]			VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([email]		<> '')
		CHECK([vorname]		<> '')
		CHECK([nachname]	<> '')
		CHECK([passwort]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([email]		NOT GLOB '*[^ -~]*')
		CHECK([vorname]		NOT GLOB '*[^ -~]*')
		CHECK([nachname]	NOT GLOB '*[^ -~]*')
		CHECK([passwort]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_Datetime_Format
        CHECK(LENGTH([geburtsdatum]) = 19)
		CHECK(datetime([geburtsdatum])	IS NOT NULL)
		CHECK(datetime([geburtsdatum])	= [geburtsdatum]),

	CONSTRAINT Gueltiges_EMail_Format
		CHECK(SUBSTR(
					[email],
					1,
					INSTR([email], '@') - 1)
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '@') + 1,
					INSTR([email], '.') - (INSTR([email],'@') + 1))
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '.') + 1)
					NOT GLOB '*[^a-zA-Z]*'),

	CONSTRAINT Gueltiges_Passwort_Format
		CHECK(LENGTH([passwort]) >= 4)
		CHECK([passwort] NOT GLOB '*[0-9][0-9]*')
		CHECK([passwort] GLOB '*[0-9]*')
		CHECK([passwort] GLOB '*[A-Z]*'),

	CONSTRAINT Primary_Key_EMail
		PRIMARY KEY([email])
);

CREATE TABLE BIBLIOTHEKAR(
	[email]			    VARCHAR		NOT NULL	COLLATE NOCASE,
	[telefonnummer]		VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([email]			<> '')
		CHECK([telefonnummer]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([email]			NOT GLOB '*[^ -~]*')
		CHECK([telefonnummer]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_EMail_Format
		CHECK(SUBSTR(
					[email], 
					1,
					INSTR([email], '@') - 1)
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '@') + 1,
					INSTR([email], '.') - (INSTR([email],'@') + 1))
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '.') + 1)
					NOT GLOB '*[^a-zA-Z]*'),

	CONSTRAINT Gueltiges_Telefonnummer_Format
		CHECK([telefonnummer] GLOB '0049*')
		CHECK([telefonnummer] NOT GLOB '*[^0-9]*'),
	
	CONSTRAINT Unique_Values
		UNIQUE([telefonnummer]),

	CONSTRAINT Primary_Key_EMail
		PRIMARY KEY([email]),

	CONSTRAINT Foreign_Key_EMail_From_NUTZER
		FOREIGN KEY([email])	REFERENCES NUTZER([email])
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE KUNDE(
	[email]			        VARCHAR		NOT NULL	COLLATE NOCASE,
	[guthaben]		        DOUBLE		NOT NULL,
	[beitragsbefreit]		BOOLEAN		NOT NULL,
	[AdresseID]			    INTEGER		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([email]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([email]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_EMail_Format
		CHECK(SUBSTR(
					[email], 
					1,
					INSTR([email], '@') - 1)
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '@') + 1,
					INSTR([email], '.') - (INSTR([email],'@') + 1))
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '.') + 1)
					NOT GLOB '*[^a-zA-Z]*'),

	CONSTRAINT Gueltiges_Guthaben 
		CHECK(typeof([guthaben]) = 'real')
		CHECK([guthaben] >= 0)
		CHECK(ROUND([guthaben],2) = [guthaben]),

	CONSTRAINT Befreiung_JB_Boolean 
		CHECK([beitragsbefreit] IN (0, 1)),

	CONSTRAINT Primary_Key_EMail
		PRIMARY KEY([email]),

	CONSTRAINT Foreign_Key_EMail_From_NUTZER
		FOREIGN KEY([email]) REFERENCES NUTZER([email])
		ON UPDATE CASCADE
		ON DELETE CASCADE,

	CONSTRAINT Foreign_Key_AdresseID_From_ADRESSE
		FOREIGN KEY([AdresseID]) REFERENCES ADRESSE([ID])
);

CREATE TABLE ADRESSE(
	[ID]				INTEGER		NOT NULL,
	[stadt]				VARCHAR		NOT NULL COLLATE NOCASE,
	[plz]				VARCHAR		NOT NULL COLLATE NOCASE,
	[hausnummer]			VARCHAR		NOT NULL COLLATE NOCASE,
	[strasse]			VARCHAR		NOT NULL COLLATE NOCASE,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([stadt]	<> '')
		CHECK([plz]		<> '')
		CHECK([hausnummer]	<> '')
		CHECK([strasse]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([stadt]	NOT GLOB '*[^ -~]*')
		CHECK([plz]		NOT GLOB '*[^ -~]*')
		CHECK([hausnummer]	NOT GLOB '*[^ -~]*')
		CHECK([strasse]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Unique_Values
		UNIQUE([stadt], [plz], [hausnummer], [strasse]) ,

	CONSTRAINT Primary_Key_ID
		PRIMARY KEY([ID])
);

CREATE TABLE AUSLEIHE(
	[ID]				INTEGER		NOT NULL,
	[beginn]			DATETIME	NOT NULL,
	[ende]				DATETIME	NOT NULL,
	[zurueckgegeben]	BOOLEAN		NOT NULL,
	[isbn]				VARCHAR		NOT NULL,
	[Nummer]			INTEGER		NOT NULL,
	[email]			    VARCHAR		NOT NULL	COLLATE NOCASE,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]	<> '')
		CHECK([email]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn]	NOT GLOB '*[^ -~]*')
		CHECK([email]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_Datetime_Format
	    CHECK(LENGTH([beginn]) = 19)
        CHECK(LENGTH([ende]) = 19)
		CHECK(datetime([beginn]) IS NOT NULL)
		CHECK(datetime([ende]) IS NOT NULL)
		CHECK(datetime([beginn]) = [beginn])
		CHECK(datetime([ende]) = [ende]),

	CONSTRAINT Gueltiges_EMail_Format
		CHECK(SUBSTR(
					[email], 
					1,
					INSTR([email], '@') - 1)
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '@') + 1,
					INSTR([email], '.') - (INSTR([email],'@') + 1))
					NOT GLOB '*[^a-zA-Z0-9]*'),
		CHECK(SUBSTR(
					[email],
					INSTR([email], '.') + 1)
					NOT GLOB '*[^a-zA-Z]*'),

    CONSTRAINT zurueckgegeben_Boolean
        CHECK([zurueckgegeben] IN (0, 1)),

	CONSTRAINT Bis_Datum_Nicht_Vor_Von_Datum CHECK([beginn] <= [ende]),

	CONSTRAINT Unique_Values
		UNIQUE([beginn], [ende], [isbn], [Nummer]),

	CONSTRAINT Primary_Key_ID
		PRIMARY KEY([ID]),

	CONSTRAINT Foreign_Key_EMail_From_NUTZER
		FOREIGN KEY([email]) REFERENCES NUTZER([email])
		ON UPDATE CASCADE
		ON DELETE CASCADE,

	CONSTRAINT Foreign_Key_ISBN_Nummer_From_EXEMPLAR
		FOREIGN KEY([isbn], [Nummer]) REFERENCES EXEMPLAR([isbn], [Nummer])
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE EXEMPLAR(
	[isbn]				VARCHAR		NOT NULL	COLLATE NOCASE,
	[Nummer]			INTEGER		NOT NULL,
	[preis]	            DOUBLE		NOT NULL,
	[StandortID]		INTEGER		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn] 	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiger_Anschaffungspreis
		CHECK(typeof([preis]) = 'real'),
		CHECK([preis] >= 0),
		CHECK(ROUND([preis],2) = [preis]),

	CONSTRAINT Primary_Key_ISBN_Nummer
		PRIMARY KEY([isbn],[Nummer]),

	CONSTRAINT Foreign_Key_ISBN_From_ARTIKEL
		FOREIGN KEY([isbn]) REFERENCES ARTIKEL([isbn])
        ON DELETE CASCADE ,

	CONSTRAINT Foreign_Key_StandortID_From_STANDORT
		FOREIGN KEY([StandortID]) REFERENCES STANDORT([ID])
);

CREATE TABLE STANDORT(
	[ID]				INTEGER		NOT NULL,
	[etage]				INTEGER		NOT NULL,
	[regal]			    INTEGER 	NOT NULL,

    CONSTRAINT Etage_Int
        CHECK(typeof([etage]) = 'integer'),

    CONSTRAINT Regal_Int
        CHECK(typeof([regal]) = 'integer'),

	CONSTRAINT Unique_Values
		UNIQUE([etage], [regal]),

	CONSTRAINT Primary_Key_ID
		PRIMARY KEY([ID])
);

CREATE TABLE MEDIUM(
    [art]   TEXT    NOT NULL COLLATE NOCASE,

    CONSTRAINT Keine_Leeren_Zeichen
        CHECK([art]			<> ''),

    CONSTRAINT Gueltiger_ASCII_Bereich
        CHECK([art] 			NOT GLOB '*[^ -~]*'),

    CONSTRAINT Korrekter_Medium_Typ
        CHECK([art] IN ('Hardcover', 'Softcover', 'CD', 'DVD')),

    CONSTRAINT Primary_Key_ISBN
        PRIMARY KEY([art])
);

CREATE TABLE ARTIKEL(
	[isbn]				VARCHAR		NOT NULL,
	[bezeichnung]		VARCHAR		NOT NULL,
	[beschreibung]		TEXT		NOT NULL,
	[erscheinungsdatum]	DATETIME	NOT NULL,
	[art]				VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]			<> '')
		CHECK([bezeichnung]		<> '')
		CHECK([beschreibung]	<> '')
		CHECK([art]				<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn] 			NOT GLOB '*[^ -~]*')
		CHECK([bezeichnung]		NOT GLOB '*[^ -~]*')
		CHECK([beschreibung]	NOT GLOB '*[^ -~]*')
		CHECK([art]				NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_Datetime_Format
        CHECK(LENGTH([erscheinungsdatum]) = 19)
		CHECK(datetime([erscheinungsdatum])	IS NOT NULL)
		CHECK(datetime([erscheinungsdatum])	= [erscheinungsdatum]),

	CONSTRAINT Korrekter_Medium_Typ 
		CHECK([art] IN ('Hardcover', 'Softcover', 'CD', 'DVD')),

	CONSTRAINT Primary_Key_ISBN
		PRIMARY KEY([isbn]),

    CONSTRAINT Foreign_Key_art_From_MEDIUM
        FOREIGN KEY([art]) REFERENCES MEDIUM([art])
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE COVERBILD(
	[coverbild]				BLOB		NOT NULL,
	[isbn]				    VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Gueltiges_PNG_Format
		CHECK(hex([coverbild]) GLOB '89504E470D0A1A0A*'),

	CONSTRAINT Primary_Key_ISBN
		PRIMARY KEY([isbn]),

	CONSTRAINT Foreign_Key_ISBN_From_ARTIKEL
		FOREIGN KEY([isbn]) REFERENCES ARTIKEL([isbn])
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE AUTOR(
	[ID]				INTEGER		NOT NULL,
	[vorname]			VARCHAR		NOT NULL,
	[nachname]			VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([vorname]		<> '')
		CHECK([nachname]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([vorname]		NOT GLOB '*[^ -~]*')
		CHECK([nachname]	NOT GLOB '*[^ -~]*'),
	
	CONSTRAINT Primary_Key_ID
		PRIMARY KEY([ID])
);

CREATE TABLE GENRE(
	[bezeichnung]				VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([bezeichnung]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([bezeichnung]	NOT GLOB '*[^ -~]*'),
	
	CONSTRAINT Primary_Key_Name
		PRIMARY KEY([bezeichnung])
);

CREATE TABLE ARTIKEL_EMPFIEHLT_ARTIKEL(
	[EmpfehlendISBN]	VARCHAR		NOT NULL,
	[EmpfohlenISBN]		VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([EmpfehlendISBN]	<> '')
		CHECK([EmpfohlenISBN]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([EmpfehlendISBN]	NOT GLOB '*[^ -~]*')
		CHECK([EmpfohlenISBN]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT	 Primary_Key_EmpfehelndISBN_EmpfohlenISBN
		PRIMARY KEY([EmpfehlendISBN], [EmpfohlenISBN]),

	CONSTRAINT	Foreign_Key_EmpfehlendISBN_From_ARTIKEL
		FOREIGN KEY([EmpfehlendISBN]) REFERENCES ARTIKEL([isbn])
        ON DELETE CASCADE ,

	CONSTRAINT Foreign_Key_EmpfohlenISBN_From_ARTIKEL
		FOREIGN KEY([EmpfohlenISBN]) REFERENCES ARTIKEL([isbn])
        ON DELETE CASCADE
);

CREATE TABLE AUTOR_VERFASST_ARTIKEL(
	[isbn]				VARCHAR		NOT NULL,
	[AutorID]			INTEGER		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Primary_Key_ISBN_AutorID
		PRIMARY KEY([isbn], [AutorID]),
	
	CONSTRAINT Foreign_Key_ISBN_From_ARTIKEL
		FOREIGN KEY([isbn]) REFERENCES ARTIKEL([isbn])
        ON DELETE CASCADE ,

	CONSTRAINT Foreign_Key_AutorID_From_AUTOR
		FOREIGN KEY(AutorID) REFERENCES AUTOR([ID])
        ON DELETE  CASCADE
);

CREATE TABLE ARTIKEL_GEHOERT_ZU_GENRE(
	[isbn]				VARCHAR		NOT NULL,
	[Genre Name]		VARCHAR		NOT NULL,

	CONSTRAINT Keine_Leeren_Zeichen
		CHECK([isbn]		<> ''),
		CHECK([Genre Name]	<> ''),

	CONSTRAINT Gueltiger_ASCII_Bereich
		CHECK([isbn]		NOT GLOB '*[^ -~]*')
		CHECK([Genre Name]	NOT GLOB '*[^ -~]*'),

	CONSTRAINT Primary_Key_ISBN_GenreName
		PRIMARY KEY([isbn], [Genre Name]),
	
	CONSTRAINT Foreign_Key_ISBN_From_ARTIKEL
		FOREIGN KEY([isbn]) REFERENCES ARTIKEL([isbn])
        ON DELETE CASCADE ,

	CONSTRAINT Foreign_Key_GenreName_From_GENRE
		FOREIGN KEY([Genre Name]) REFERENCES GENRE(bezeichnung)
);

CREATE TRIGGER MAX_AUSLEIHE
	BEFORE INSERT ON AUSLEIHE
	BEGIN
		SELECT
			CASE
				WHEN (
					SELECT COUNT(*) FROM AUSLEIHE 
					WHERE	AUSLEIHE.[email] = NEW.[email]
					AND		AUSLEIHE.[zurueckgegeben] = 0
					) >= 5
				THEN RAISE(ABORT, 'Auf diesem Account sind bereits 5 oder mehr Ausleihen noch nicht zurueckgegeben')
			END;
	END;

CREATE TRIGGER BEREITS_AUSGELIEHEN
	BEFORE INSERT ON AUSLEIHE
	BEGIN
		SELECT 
			CASE 
				WHEN (
					SELECT COUNT(*) FROM AUSLEIHE 
					WHERE 	AUSLEIHE.[isbn] = NEW.[isbn]
					AND		AUSLEIHE.[Nummer] = NEW.[Nummer]
					AND		AUSLEIHE.[ende] BETWEEN NEW.[beginn] AND NEW.[ende]
					AND		AUSLEIHE.[zurueckgegeben] = 0
					) > 0
				THEN RAISE(ABORT, 'Dieses Exemplar ist in dem gewuenschten Zeitraum bereits verliehen.')
				WHEN (
					SELECT COUNT(*) FROM AUSLEIHE 
					WHERE 	AUSLEIHE.[isbn] = NEW.[isbn]
					AND		AUSLEIHE.[Nummer] = NEW.[Nummer]
					AND		AUSLEIHE.[beginn] BETWEEN NEW.[beginn] AND NEW.[ende]
					AND		AUSLEIHE.[zurueckgegeben] = 0
					) > 0
				THEN RAISE(ABORT, 'Dieses Exemplar ist in dem gewuenschten Zeitraum bereits verliehen.')
			END;
	END;

CREATE TRIGGER ADRESSE_MEHRFACH_ZUGEORDNET
	BEFORE UPDATE ON ADRESSE
	BEGIN
		SELECT 
			CASE 
				WHEN (
					SELECT COUNT(*) FROM KUNDE 
					WHERE 	KUNDE.[AdresseID] = OLD.[ID]
					) > 1
				THEN RAISE(ABORT, 'Diese Adresse ist mehr als einem Kunden zugewiesen und kann nicht geaendert werden.')
			END;
	END;