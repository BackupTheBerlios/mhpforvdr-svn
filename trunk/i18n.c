/*
 * i18n.c: Internationalization
 *
 * See the README file for copyright information and how to reach the author.
 *
 * $Id: i18n.c 1.3 2002/06/23 13:05:59 kls Exp $
 */

#include "i18n.h"

const tI18nPhrase MhpI18nPhrases[] = {
  { "MHP", // English
    "MHP", // Deutsch
    "", // Slovenski
    "MHP", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "MHP-sovellukset", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "An MHP implementation", // English
    "Eine MHP-Implementierung", // Deutsch
    "", // Slovenski
    "Una implementazione MHP", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "MHP-implementaatio", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "<No name available>", // English
    "<Kein Name verf�gbar>", // Deutsch
    "", // Slovenski
    "<Nessun nome disponibile>", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "<nimet�n>", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Unknown channel", // English
    "Unbekannter Kanal", // Deutsch
    "", // Slovenski
    "Canale sconosciuto", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Tuntematon kanava", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "MHP Applications", // English
    "MHP-Anwendungen", // Deutsch
    "", // Slovenski
    "Applicazioni MHP", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "MHP-sovellukset", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Tuning required:", // English
    "Kanalwechsel erforderlich:", // Deutsch
    "", // Slovenski
    "Sintonizzazione richiesta:", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Vaaditaan kanavanvaihto:", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Currently not available:", // English
    "Zur Zeit nicht verf�gbar:", // Deutsch
    "", // Slovenski
    "Attualmente non disponibile:", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "T�ll� hetkell� ei saatavilla:", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "No MHP applications available", // English
    "Keine MHP-Anwendungen verf�gbar", // Deutsch
    "", // Slovenski
    "Nessuna applicazione MHP disponibile", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "MHP-sovelluksia ei saatavilla", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Local applications:", // English
    "Lokale Anwendungen:", // Deutsch
    "", // Slovenski
    "Applicazioni locali:", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Paikalliset sovellukset:", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Cannot receive application!", // English
    "Anwendung nicht empfangbar!", // Deutsch
    "", // Slovenski
    "Impossibile ricevere l'applicazione!", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Sovelluksen vastaanottaminen ep�onnistui!", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Loading files...", // English
    "Dateien werden geladen...", // Deutsch
    "", // Slovenski
    "Caricamento dati...", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Ladataan tiedostoja...", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Error while starting Java environment", // English
    "Fehler beim Starten der Java-Umgebung", // Deutsch
    "", // Slovenski
    "Errore durante l'avvio dell'ambiente Java", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Java-ymp�rist�n k�ynnist�minen ep�onnistui", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Error in output system: Plugin disabled", // English
    "Fehler im Ausgabesystem: Plugin deaktiviert", // Deutsch
    "", // Slovenski
    "Errore nel sistema di output: Plugin disabilitato", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Ulostuloj�rjestelm�ss� virhe: laajennos toimintakyvyt�n", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Error in Java system: Plugin disabled", // English
    "Fehler im Java-System: Plugin deaktiviert", // Deutsch
    "", // Slovenski
    "Errore nel sistema Java: Plugin disabilitato", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Java-j�rjestelm�ss� virhe: laajennos toimintakyvyt�n", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Loading failed", // English
    "Laden fehlgeschlagen", // Deutsch
    "", // Slovenski
    "Caricamento non riuscito", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "Lataaminen ep�onnistui", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Starting failed", // English
    "Starten fehlgeschlagen", // Deutsch
    "", // Slovenski
    "Avvio non riuscito", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "K�ynnistys ep�onnistui", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "<unknown name>", // English
    "<Unbekannter Name>", // Deutsch
    "", // Slovenski
    "<Nome sconosciuto>", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "<tuntematon>", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "This application has already been started", // English
    "Diese Anwendung wurde bereits gestartet", // Deutsch
    "", // Slovenski
    "Questa applicazione � gi� stata avviata", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "T�m� sovellus on jo k�ynnistetty", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "", // English
    "", // Deutsch
    "", // Slovenski
    "", // Italiano
    "", // Nederlands
    "", // Portugu�s
    "", // Fran�ais
    "", // Norsk
    "", // suomi
    "", // Polski
    "", // Espa�ol
    "", // �������� (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Catal�
#if VDRVERSNUM >= 10302
    "", // ������� (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { NULL }
  };
