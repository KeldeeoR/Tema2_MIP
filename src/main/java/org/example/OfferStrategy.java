package org.example;

public interface OfferStrategy {
    // Returnează valoarea reducerii (număr pozitiv) sau 0 dacă nu se aplică
    // De asemenea, primește un nume pentru a-l afișa pe bon
    double calculeazaReducere(Comanda comanda);
    String getNumeOferta();
}