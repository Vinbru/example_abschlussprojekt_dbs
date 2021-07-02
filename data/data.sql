--NUTZER
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('Brillenschlange@Buecherwurm.de', 'Brunhilde', 'Haumichnicht', datetime('2000-02-11'), 'Guent3rKr4ss');
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('DraufhauOlga@GibMirDeinEssensgeld.ru', 'Olga', 'vonDerWolga', datetime('1997-09-01'), 'P4ssw0rt');
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('RudiRegaleinraeumer@UniBib.de', 'Rudi', 'Raeumer', datetime('1975-04-15'), 'Gruene3chs3');
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('NineToFiveNancy@UniBib.de', 'Nancy', 'IchHabGleichFeierabend-Mueller', datetime('1987-11-22'), '3ndlichFreitag');
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('Musketier@wasdalos.net', 'Elton', 'Musk', datetime('1990-09-09'), 'L4m3');
INSERT INTO NUTZER ([email], [vorname], [nachname], [geburtsdatum], [passwort])
VALUES ('Jojo@Allstar.net', 'Jojo', 'Musk', datetime('2012-10-06'), 'IsThis4JoJoReference');

--BIBLIOTHEKAR
INSERT INTO BIBLIOTHEKAR ([email], [telefonnummer])
VALUES ('RudiRegaleinraeumer@UniBib.de', '004918054646');
INSERT INTO BIBLIOTHEKAR ([email], [telefonnummer])
VALUES ('NineToFiveNancy@UniBib.de', '00498005893437570');

--ADRESSE
INSERT INTO ADRESSE ([ID], [stadt], [plz], [hausnummer], [strasse])
VALUES (1, 'Tollestadt', 12345, 100, 'Reinemannstrasse');
INSERT INTO ADRESSE ([ID], [stadt], [plz], [hausnummer], [strasse])
VALUES (2, 'Ghetto', 24769, 420, 'Boxerweg');
INSERT INTO ADRESSE ([ID], [stadt], [plz], [hausnummer], [strasse])
VALUES (3, 'IrgendwoinnerPampa', 01210, 1, 'Nixloshierstrasse');

--KUNDE
INSERT INTO KUNDE ([email], [guthaben], [beitragsbefreit], [AdresseID])
VALUES ('Brillenschlange@Buecherwurm.de', 2645.32, 0, 1);
INSERT INTO KUNDE ([email], [guthaben], [beitragsbefreit], [AdresseID])
VALUES ('DraufhauOlga@GibMirDeinEssensgeld.ru', 0, 1, 2);
INSERT INTO KUNDE ([email], [guthaben], [beitragsbefreit], [AdresseID])
VALUES ('Musketier@wasdalos.net', 350.20, 0, 2);
INSERT INTO KUNDE ([email], [guthaben], [beitragsbefreit], [AdresseID])
VALUES ('RudiRegaleinraeumer@UniBib.de', 48.00, 1, 3);
INSERT INTO KUNDE ([email], [guthaben], [beitragsbefreit], [AdresseID])
VALUES ('Jojo@Allstar.net', 934.12, 1, 2);

--MEDIUM
INSERT INTO MEDIUM ([art])
VALUES('Hardcover');
INSERT INTO MEDIUM ([art])
VALUES('Softcover');
INSERT INTO MEDIUM ([art])
VALUES('CD');
INSERT INTO MEDIUM ([art])
VALUES('DVD');

--ARTIKEL
INSERT INTO ARTIKEL ([isbn], [bezeichnung], [beschreibung], [erscheinungsdatum], [art])
VALUES('978-3936457704', 'Scharfe Gewalt: Messerkampf, Messerabwehr und das Ueberleben auf der Strasse', 'Messerkampf ist eines der umstrittensten Themen in den Kampfkuensten. Ueberall gibt es Techniken zur Messerabwehr und zum Ueberleben im Messerkampf, doch funktionieren diese Techniken auch? Die Gefahr und die Brutalitaet eines Messerangriffs werden leider oft voellig unterschaetzt und entsprechende Techniken und Trainings zu wenig kritisch hinterfragt. Dieses Buch zeigt deshalb sehr deutlich die Gefahren des Themas auf und vermittelt ausserdem verschiedene Ansaetze zu einem realistischen Training mit dem Messer und gegen das Messer.', '2020-09-01 00:00:00', 'Hardcover');
INSERT INTO ARTIKEL ([isbn], [bezeichnung], [beschreibung], [erscheinungsdatum], [art])
VALUES('978-3837070071', 'Hilfe ich werde gemobbt!: Was kann ich tun?', 'Noch ein Buch ueber Mobbing? Ja, aber endlich eins, was wirklich weiter hilft. In der Tat es gibt viele Buecher zum Thema Mobbing, aber keines was das Mobbingopfer in den Mittelpunkt stellt und dieses mit Rat und Informationen hilft. Dieses Buch wurde einzig und alleine geschrieben um Mobbingopfern zu helfen und ihnen Mut zu machen sich zu wehren. Obwohl Mobbing durch Leistungsdruck, modernes Sklaventum und Zerfall der Gesellschaft immer haeufiger wird, muss niemand tatenlos und ohnmaechtig dem Mobbing gegenueber stehen und sich alles gefallen lassen. Jeder kann ein Mobbingopfer werden, auch ohne es bewusst zu provozieren. Genau deshalb werden viele zu Mitlaeufern und Passivtaetern, durch unterlassener Hilfeleistung. Nutzen Sie dieses Buch um nicht laenger ein Mobbingopfer zu sein. Auch ich habe Informationen zu Mobbing und Hilfe gesucht, dabei musste ich feststellen, dass es kein Buch gibt, was ungeschminkt und ehrlich die Wahrheit ueber Mobbing und dessen Auswirkung / Folgen preisgibt. In diesem Buch zaehle ich Gruende fuer Mobbing auf, was man tun sollte und wie sich der Gesetzgeber dazu verhaelt.', '2008-10-14 00:00:00', 'Softcover');
INSERT INTO ARTIKEL ([isbn], [bezeichnung], [beschreibung], [erscheinungsdatum], [art])
VALUES('978-1-4215-7879-8', 'Jojos Bizarre Adventure: Part 1--Phantom Blood', 'Young Jonathan Joestars life is forever changed when he meets his new adopted brother, Dio. For some reason, Dio has a smoldering grudge against him and derives pleasure from seeing him suffer. But every man has his limits, as Dio finds out. This is the beginning of a long and hateful relationship!', '2015-02-24 00:00:00', 'Softcover');
INSERT INTO ARTIKEL ([isbn], [bezeichnung], [beschreibung], [erscheinungsdatum], [art])
VALUES('Soon-To-Be-Deleted-Artikel-ISBN', 'Soon-To-Be-Deleted-Artikel-Bezeichnung', 'Soon-To-Be-Deleted-Artikel-Beschreibung', datetime('2021-01-01'), 'Softcover');

--AUTOR
INSERT INTO AUTOR ([ID], [vorname], [nachname])
VALUES (1, 'Sven', 'Ackermann');
INSERT INTO AUTOR ([ID], [vorname], [nachname])
VALUES (2, 'Joachim', 'Peters');
INSERT INTO AUTOR ([ID], [vorname], [nachname])
VALUES (3, 'Hirohiko', 'Araki');
INSERT INTO AUTOR ([ID], [vorname], [nachname])
VALUES (4, 'SoonToBeDeletedAutorVorname', 'SoonToBeDeletedAutorNachname');

--AUTOR_VERFASST_ARTIKEL
INSERT INTO AUTOR_VERFASST_ARTIKEL([isbn], [AutorID])
VALUES ('978-3936457704',1);
INSERT INTO AUTOR_VERFASST_ARTIKEL([isbn], [AutorID])
VALUES ('978-3837070071',2);
INSERT INTO AUTOR_VERFASST_ARTIKEL([isbn], [AutorID])
VALUES ('978-1-4215-7879-8',3);
INSERT INTO AUTOR_VERFASST_ARTIKEL([isbn], [AutorID])
VALUES ('Soon-To-Be-Deleted-Artikel-ISBN',4);

--ARTIKEL_EMPFIEHLT_ARTIKEL
INSERT INTO ARTIKEL_EMPFIEHLT_ARTIKEL([EmpfehlendISBN], [EmpfohlenISBN])
VALUES ('978-3936457704','978-3837070071');
INSERT INTO ARTIKEL_EMPFIEHLT_ARTIKEL([EmpfehlendISBN], [EmpfohlenISBN])
VALUES ('978-3837070071','978-3936457704');

--STANDORT
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (1, '-1', '5');
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (2, '0', '2');
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (3, '-1', '6');
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (4, '3', '1');
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (5, '3', '3');
INSERT INTO STANDORT([ID], [etage], [regal])
VALUES (6, '2', '5');

--EXEMPLAR
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3936457704', 1, 20.00, 4);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3936457704', 2, 20.00, 4);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3936457704', 3, 20.00, 5);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3936457704', 4, 20.00, 5);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3936457704', 5, 20.00, 5);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 1, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 2, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 3, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 4, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 5, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 6, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-3837070071', 7, 5.95, 1);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('978-1-4215-7879-8', 1, 14.59, 4);
INSERT INTO EXEMPLAR([isbn], [Nummer], [preis], [StandortID])
VALUES('Soon-To-Be-Deleted-Artikel-ISBN', 1, 99.99, 6);

--GENRE
INSERT INTO GENRE([bezeichnung])
VALUES ('Fighting & Self-Defense');
INSERT INTO GENRE([bezeichnung])
VALUES ('General Sports');
INSERT INTO GENRE([bezeichnung])
VALUES ('General Martial Arts & Self-Defense');
INSERT INTO GENRE([bezeichnung])
VALUES ('Lifestyle');
INSERT INTO GENRE([bezeichnung])
VALUES ('Kriminalroman');

--ARTIKEL_GEHOERT_ZU_GENRE
INSERT INTO ARTIKEL_GEHOERT_ZU_GENRE([isbn], [Genre Name])
VALUES ('978-3936457704','Fighting & Self-Defense');
INSERT INTO ARTIKEL_GEHOERT_ZU_GENRE([isbn], [Genre Name])
VALUES ('978-3936457704','General Sports');
INSERT INTO ARTIKEL_GEHOERT_ZU_GENRE([isbn], [Genre Name])
VALUES ('978-3936457704','General Martial Arts & Self-Defense');
INSERT INTO ARTIKEL_GEHOERT_ZU_GENRE([isbn], [Genre Name])
VALUES ('978-3837070071','Lifestyle');
INSERT INTO ARTIKEL_GEHOERT_ZU_GENRE([isbn], [Genre Name])
VALUES ('978-1-4215-7879-8','Kriminalroman');

--AUSLEIHE
INSERT INTO AUSLEIHE([beginn], [ende], [zurueckgegeben], [isbn], [Nummer], [email])
VALUES ('2021-02-08 15:23:44', '2021-02-15 00:00:00', 1, '978-3936457704', 2, 'DraufhauOlga@GibMirDeinEssensgeld.ru');
INSERT INTO AUSLEIHE([beginn], [ende], [zurueckgegeben], [isbn], [Nummer], [email])
VALUES ('2021-02-15 12:34:08', '2021-02-22 00:00:00', 1, '978-3936457704', 1, 'DraufhauOlga@GibMirDeinEssensgeld.ru');
INSERT INTO AUSLEIHE([beginn], [ende], [zurueckgegeben], [isbn], [Nummer], [email])
VALUES ('2021-02-22 16:51:55', '2021-03-01 00:00:00', 0, '978-3936457704', 1, 'DraufhauOlga@GibMirDeinEssensgeld.ru');
INSERT INTO AUSLEIHE([beginn], [ende], [zurueckgegeben], [isbn], [Nummer], [email])
VALUES ('2021-03-15 00:00:00', '2021-03-22 00:00:00', 0, '978-3837070071', 3, 'Brillenschlange@Buecherwurm.de');
INSERT INTO AUSLEIHE([beginn], [ende], [zurueckgegeben], [isbn], [Nummer], [email])
VALUES ('2021-02-08 16:51:55', '2021-02-15 00:00:00', 1, '978-1-4215-7879-8', 1, 'Jojo@Allstar.net');


