# Login

## Opis Projektu

Testowa aplikacja CNApp oparta na protokole SSMMP. Interfejsem użytkownika jest klient wiersza poleceń (CLI), który komunikuje się z systemem mikrousług poprzez API Gateway. Żądania i odpowiedzi przesyłane są w formie obiektów klasy String, a cała architektura jest bezstanowa.

## Rola Komponentu

Ta mikrousługa odpowiada za weryfikację tożsamości użytkowników. Odbiera żądania typu `login_request` od API Gateway i sprawdza w bazie danych, czy użytkownik o podanym loginie i haśle istnieje.

## Konfiguracja

Ten komponent wymaga następujących zmiennych środowiskowych w pliku `.env`:

LOGIN_MICROSERVICE_PORT=

DB_HOST=

DB_PORT=

DB_NAME=

DB_USER=

DB_PASSWORD=


## Uruchomienie

Serwis można uruchomić, wykonując główną metodę `main` w klasie `Login.java`