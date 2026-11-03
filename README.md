# Aplikacija za spremljanje stroškov

Spletna aplikacija za spremljanje osebnih stroškov. Zaledje je napisano v Spring Bootu, uporabniški vmesnik v Reactu, podatki pa se shranjujejo v bazo PostgreSQL.

## Kazalo

1. [O projektu](#o-projektu)
2. [Uporabljene tehnologije](#uporabljene-tehnologije)
3. [Struktura projekta](#struktura-projekta)
4. [Navodila za nameščanje in zagon](#navodila-za-nameščanje-in-zagon)
5. [Navodila za razvijalce](#navodila-za-razvijalce)
6. [Standardi kodiranja](#standardi-kodiranja)

---

## O projektu

Aplikacija uporabniku omogoča, da si zabeleži vsak strošek (naziv, znesek, kategorija, datum, opomba) in dobi pregled nad svojo porabo.

**Cilji projekta:**
- preprosto beleženje stroškov,
- razvrščanje stroškov v kategorije,
- pregled in iskanje po zapisih,
- pregled porabe po kategorijah.

**Funkcionalnosti:**
- **CRUD stroškov:** dodajanje, pregled vseh in posameznega stroška, urejanje ter brisanje,
- **CRUD kategorij** prek REST API-ja,
- **iskanje** po nazivu in opombi,
- **filtriranje** po kategoriji in obdobju (od–do),
- **statistika** porabe po kategorijah,
- **dokumentacija API-ja** v Swaggerju.

---

## Uporabljene tehnologije

### Zaledje (`backend`)

| Tehnologija | Različica |
|---|---|
| Java | 17 (deluje tudi na 21) |
| Spring Boot | 3.5.6 |
| Spring Web, Spring Data JPA, Validation | iz Spring Boot 3.5.6 |
| PostgreSQL gonilnik | iz Spring Boot 3.5.6 |
| springdoc-openapi (Swagger UI) | 2.8.9 |
| Maven | 3.9 ali novejši (ali priloženi wrapper) |

### Uporabniški vmesnik (`frontend`)

| Tehnologija | Različica |
|---|---|
| Node.js | [vpiši, izpiše `node -v`] |
| React | [vpiši, glej `frontend/package.json`] |
| Vite | [vpiši, glej `frontend/package.json`] |

### Baza in orodja

| Tehnologija | Različica |
|---|---|
| PostgreSQL | 16 (v Dockerju) |
| Docker | [vpiši, izpiše `docker -v`] |
| Git | [vpiši, izpiše `git --version`] |

---

## Struktura projekta

```
.
├── backend/                        Spring Boot zaledje
│   ├── pom.xml                     odvisnosti in nastavitve gradnje (Maven)
│   └── src/
│       ├── main/
│       │   ├── java/si/feri/stroski/
│       │   │   ├── StroskiApplication.java   vstopna točka
│       │   │   ├── controller/               REST kontrolerji (HTTP endpointi)
│       │   │   ├── model/                    entitete in objekti podatkov
│       │   │   └── repository/               dostop do baze (Spring Data JPA)
│       │   └── resources/
│       │       └── application.properties    nastavitve aplikacije in baze
│       └── test/                             testi
├── frontend/                       React uporabniški vmesnik
│   ├── package.json                odvisnosti in skripte
│   ├── index.html                  vstopna stran
│   └── src/
│       ├── main.jsx                zagon React aplikacije
│       └── App.jsx                 glavna komponenta (seznam, obrazec, iskanje, statistika)
├── .gitignore
└── README.md
```

| Mapa | Vsebina |
|---|---|
| `backend/.../controller` | sprejme HTTP zahtevke in vrne odgovore |
| `backend/.../model` | opis tabel (`Strosek`, `Kategorija`) in objekt za statistiko |
| `backend/.../repository` | poizvedbe v bazo |
| `backend/.../resources` | konfiguracija (povezava z bazo, vrata) |
| `frontend/src` | koda uporabniškega vmesnika |

**Delovanje v kratkem:** brskalnik (React, `:5173`) pošilja zahtevke HTTP na zaledje (Spring Boot, `:8080`), ki bere in piše v PostgreSQL (`:5432`).

---

## Navodila za nameščanje in zagon

### Predpogoji

Namesti naslednja orodja:

- **Java JDK 17** ali novejši (preveri z `java -version`),
- **Maven** (ali uporabi priloženi wrapper `mvnw`, če obstaja),
- **Node.js** (LTS) z `npm` (preveri z `node -v` in `npm -v`),
- **Docker Desktop** (za bazo PostgreSQL),
- **Git**.

### 1. Prenos projekta

```bash
git clone <URL-REPOZITORIJA>
cd <IME-REPOZITORIJA>
```

### 2. Zagon baze (PostgreSQL v Dockerju)

Prvi zagon (ustvari kontejner):

```bash
docker run --name stroski-db \
  -e POSTGRES_USER=stroski \
  -e POSTGRES_PASSWORD=stroski \
  -e POSTGRES_DB=stroskidb \
  -p 5432:5432 \
  -v stroski-data:/var/lib/postgresql/data \
  -d postgres:16
```

Kasnejši zagoni:

```bash
docker start stroski-db
```

Če so vrata 5432 že zasedena, zamenjaj `-p 5432:5432` z `-p 5433:5432` in v `application.properties` uporabi vrata 5433.

### 3. Nastavitve zaledja

Povezava z bazo je v `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stroskidb
spring.datasource.username=stroski
spring.datasource.password=stroski
spring.jpa.hibernate.ddl-auto=update
```

| Nastavitev | Pomen |
|---|---|
| `spring.datasource.url` | naslov in ime baze |
| `spring.datasource.username` / `password` | uporabnik in geslo, enaka kot pri zagonu kontejnerja |
| `spring.jpa.hibernate.ddl-auto=update` | tabele se ustvarijo samodejno ob zagonu |

Če v kontejnerju uporabljaš druge podatke za prijavo, jih spremeni tudi tukaj. Za produkcijo gesla ne hrani v repozitoriju, ampak ga nastavi z okoljsko spremenljivko (`SPRING_DATASOURCE_PASSWORD`).

### 4. Zagon zaledja

```bash
cd backend
mvn spring-boot:run
```

Zaledje teče na `http://localhost:8080`. Dokumentacija API-ja (Swagger UI) je na `http://localhost:8080/swagger-ui.html`.

### 5. Zagon uporabniškega vmesnika

V novem terminalu:

```bash
cd frontend
npm install        # samo prvič
npm run dev
```

Aplikacija je na `http://localhost:5173`.

### Povzetek naslovov

| Del | Naslov |
|---|---|
| Uporabniški vmesnik | http://localhost:5173 |
| Zaledje (API) | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

### REST API

| Metoda | Pot | Opis |
|---|---|---|
| GET | `/api/stroski` | vsi stroški |
| GET | `/api/stroski/{id}` | en strošek |
| POST | `/api/stroski` | nov strošek |
| PUT | `/api/stroski/{id}` | posodobitev stroška |
| DELETE | `/api/stroski/{id}` | brisanje stroška |
| GET | `/api/stroski/iskanje` | iskanje in filtriranje (`q`, `kategorijaId`, `datumOd`, `datumDo`) |
| GET | `/api/stroski/statistika` | poraba po kategorijah |
| GET, POST, PUT, DELETE | `/api/kategorije` | upravljanje kategorij |

### Pogoste težave

| Težava | Rešitev |
|---|---|
| Zaledje se ne zažene (povezava z bazo) | preveri, da kontejner teče (`docker ps`) |
| Frontend ne prikaže podatkov | preveri, da zaledje teče na vratih 8080 |
| Napaka `Port already in use` | ustavi proces na teh vratih ali zamenjaj vrata |
| `npm run dev` ne najde skripte | poženi ga v mapi `frontend` |

---

## Navodila za razvijalce

### Delo z repozitorijem

- Veja `main` vsebuje stabilno, delujočo različico. Neposredno vanjo ne pišemo večjih sprememb.
- Za vsako novo funkcionalnost ali popravek ustvari novo vejo iz `main`:

```bash
git checkout main
git pull
git checkout -b feature/ime-funkcionalnosti
```

### Poimenovanje vej

| Predpona | Uporaba | Primer |
|---|---|---|
| `feature/` | nova funkcionalnost | `feature/iskanje-stroskov` |
| `fix/` | popravek napake | `fix/pot-do-kategorij` |
| `docs/` | dokumentacija | `docs/readme` |

### Sporočila commitov

Kratko in v ukazni obliki, po vzorcu `vrsta: opis`:

```
feat: dodaj iskanje stroškov po nazivu
fix: popravi pot do kategorij v frontendu
docs: posodobi README
refactor: premakni kontrolerje v svoj paket
```

### Postopek prispevanja

1. Ustvari vejo (glej zgoraj).
2. Naredi spremembe in jih večkrat commitaj v manjših korakih.
3. Preveri, da zaledje in uporabniški vmesnik delujeta.
4. Potisni vejo na GitHub: `git push -u origin feature/ime-funkcionalnosti`.
5. Odpri **Pull Request** v `main` z opisom sprememb.
6. Po pregledu ga združi (merge) in izbriši vejo.

### Česa ne commitamo

Mape `target/`, `node_modules/`, `.idea/` in datoteke z gesli ali ključi. Za to skrbi datoteka `.gitignore`.

---

## Standardi kodiranja

### Java (zaledje)

- Razredi: `PascalCase` (`StroskiController`), metode in spremenljivke: `camelCase`, konstante: `UPPER_SNAKE_CASE`.
- Paketi so v malih črkah (`si.feri.stroski.controller`). Razredi so razporejeni po vlogi: `controller`, `model`, `repository`.
- Kontrolerji so tanki: vsebujejo samo obravnavo HTTP zahtevkov, dostop do baze gre prek repozitorija.
- Vhodni podatki se preverjajo z anotacijami (`@Valid`, `@NotBlank`, `@NotNull`).
- Vsak endpoint je dokumentiran z `@Operation` (Swagger).
- Uporabljamo ustrezne HTTP kode: 200, 201, 204, 400, 404.
- Zamik: 4 presledki.

### REST API

- Poti so v množini in z malimi črkami (`/api/stroski`).
- Metode HTTP imajo pomen: GET bere, POST ustvari, PUT posodobi, DELETE briše.
- Odgovori so v obliki JSON.

### JavaScript / React (uporabniški vmesnik)

- Komponente: `PascalCase`, spremenljivke in funkcije: `camelCase`.
- Uporabljamo funkcijske komponente in hooke (`useState`, `useEffect`).
- Stanje se posodablja nespremenljivo (brez neposrednega spreminjanja objektov).
- Pri izrisu seznamov je vsak element označen s `key`.
- Naslovi API-ja so zapisani na enem mestu (konstanta `API`).
- Zamik: 2 presledka.

### Splošno

- Imena v kodi so jasna in opisna. Komentarji pojasnijo zakaj, ne kaj.
- Koda se pred commitom preveri: zaledje se mora zagnati, frontend pa prikazati podatke.
- Nepotrebna ali zakomentirana koda se ne commita.
