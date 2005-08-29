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
    "", // Português
    "", // Français
    "", // Norsk
    "MHP-sovellukset", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "MHP-implementaatio", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "<No name available>", // English
    "<Kein Name verfügbar>", // Deutsch
    "", // Slovenski
    "<Nessun nome disponibile>", // Italiano
    "", // Nederlands
    "", // Português
    "", // Français
    "", // Norsk
    "<nimetön>", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Tuntematon kanava", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "MHP-sovellukset", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Vaaditaan kanavanvaihto:", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "Currently not available:", // English
    "Zur Zeit nicht verfügbar:", // Deutsch
    "", // Slovenski
    "Attualmente non disponibile:", // Italiano
    "", // Nederlands
    "", // Português
    "", // Français
    "", // Norsk
    "Tällä hetkellä ei saatavilla:", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "No MHP applications available", // English
    "Keine MHP-Anwendungen verfügbar", // Deutsch
    "", // Slovenski
    "Nessuna applicazione MHP disponibile", // Italiano
    "", // Nederlands
    "", // Português
    "", // Français
    "", // Norsk
    "MHP-sovelluksia ei saatavilla", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Paikalliset sovellukset:", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Sovelluksen vastaanottaminen epäonnistui!", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Ladataan tiedostoja...", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Java-ympäristön käynnistäminen epäonnistui", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Ulostulojärjestelmässä virhe: laajennos toimintakyvytön", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Java-järjestelmässä virhe: laajennos toimintakyvytön", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Lataaminen epäonnistui", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "Käynnistys epäonnistui", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "<tuntematon>", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { "This application has already been started", // English
    "Diese Anwendung wurde bereits gestartet", // Deutsch
    "", // Slovenski
    "Questa applicazione è già stata avviata", // Italiano
    "", // Nederlands
    "", // Português
    "", // Français
    "", // Norsk
    "Tämä sovellus on jo käynnistetty", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
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
    "", // Português
    "", // Français
    "", // Norsk
    "", // suomi
    "", // Polski
    "", // Español
    "", // ÅëëçíéêÜ (Greek)
    "", // Svenska
    "", // Romaneste
    "", // Magyar
    "", // Català
#if VDRVERSNUM >= 10302
    "", // ÀãááÚØÙ (Russian)
#if VDRVERSNUM >= 10307
    "", // Hrvatski (Croatian)
#endif
#endif
  },
  { NULL }
  };
