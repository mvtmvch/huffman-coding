kompresja i dekompresja Huffmana (bezstratna)

Projekt realizuje bezstratną kompresję i dekompresję pojedynczych plików metodą Huffmana.
Program obsługuje zarówno pliki tekstowe, jak i binarne.

W skrócie:
- w trybie kompresji plik jest dzielony na bloki o długości L bajtów (parametr -l),
  a następnie kodowany Huffmanem,
- w trybie dekompresji plik jest odtwarzany dokładnie do postaci wejściowej (round-trip).

Kompilacja (Maven)
Przejdź do katalogu z projektem i wykonaj:
```bash
cd HufmannCoding
mvn clean package
```

Po poprawnej kompilacji powstanie:
```
HufmannCoding/target/AiSD2025ZEx5.jar
```

Czyszczenie projektu
Aby usunąć skompilowane pliki .class i katalog target/ (w tym plik .jar):
```bash
cd HufmannCoding
mvn clean
```

Uruchomienie — działające przykłady do wklejenia
Poniższe komendy tworzą plik testowy, kompresują go, a następnie dekompresują i pozwalają sprawdzić zgodność.

1) Linux/macOS (bash)
```bash
cd HufmannCoding
mkdir -p tmp
echo "Przykladowy tekst do kompresji Huffmanem." > tmp/input.txt
java -jar target/AiSD2025ZEx5.jar -m comp -s tmp/input.txt -d tmp/input.txt.comp -l 2
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/input.txt.comp -d tmp/output.txt
diff -q tmp/input.txt tmp/output.txt
```

2) Windows (PowerShell)
```powershell
cd HufmannCoding
New-Item -ItemType Directory -Force -Path tmp | Out-Null
"Przykladowy tekst do kompresji Huffmanem." | Set-Content -Encoding ASCII tmp\input.txt
java -jar target\AiSD2025ZEx5.jar -m comp -s tmp\input.txt -d tmp\input.txt.comp -l 2
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\input.txt.comp -d tmp\output.txt
fc tmp\input.txt tmp\output.txt
```

Przykład na większym pliku (bible.txt)
1) Linux/macOS (bash)
```bash
cd HufmannCoding
mkdir -p tmp
java -jar target/AiSD2025ZEx5.jar -m comp -s data/bible.txt -d tmp/bible.comp -l 2
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/bible.comp -d tmp/bible_out.txt
diff -q data/bible.txt tmp/bible_out.txt
```

2) Windows (PowerShell)
```powershell
cd HufmannCoding
New-Item -ItemType Directory -Force -Path tmp | Out-Null
java -jar target\AiSD2025ZEx5.jar -m comp -s data\bible.txt -d tmp\bible.comp -l 2
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\bible.comp -d tmp\bible_out.txt
fc data\bible.txt tmp\bible_out.txt
```


Uruchomienie — standardowe użycie
Najpierw przejdź do katalogu projektu:
```bash
cd HufmannCoding
```

Kompresja:
```bash
java -jar target/AiSD2025ZEx5.jar -m comp -s <plik_wejściowy> -d <plik_wyjściowy> -l <L>
```

Dekompresja:
```bash
java -jar target/AiSD2025ZEx5.jar -m decomp -s <plik_skompresowany> -d <plik_wyjściowy>
```

Argumenty programu
- -m — tryb pracy:
  - comp — kompresja
  - decomp — dekompresja
- -s — ścieżka do pliku wejściowego (źródłowego)
- -d — ścieżka do pliku wyjściowego (docelowego)
- -l — (opcjonalnie, tylko dla kompresji) długość sekwencji bajtów traktowanej jako pojedynczy symbol:
  - domyślnie: 1
  - zakres: 1..255
  - przykład: -l 2 oznacza kodowanie par bajtów jako „symboli”

Testy
Aby uruchomić testy jednostkowe:
```bash
cd HufmannCoding
mvn test
```

Komentarz o implementacji
- Program nie wymaga dodatkowej interakcji z użytkownikiem w trakcie działania (brak wejścia z klawiatury).
- Obsługa błędów odbywa się poprzez jawnie zdefiniowany wyjątek HuffmanException (np. niepoprawne argumenty, uszkodzony plik).
- Plik skompresowany zawiera nagłówek oraz zapis drzewa Huffmana i danych:
  * nagłówek (3 bajty): L, bitPadding, bytePadding
  * drzewo Huffmana (preorder): 0 = węzeł wewn., 1 = liść + L bajtów symbolu
  * dane zakodowane bitowo (dopełniane do pełnego bajtu bitPadding)
- W przypadku gdy rozmiar pliku nie jest podzielny przez L, ostatni blok jest dopełniany zerami,
  a liczba dopisanych bajtów jest zapisana jako bytePadding, co pozwala poprawnie odtworzyć oryginalne dane.


gotowe komendy na laboratoria:

**macOS/Linux:**
```bash
cd HufmannCoding

# 1) Kompiluje projekt i robi JAR (szybko, bez testów)
mvn -q clean package -DskipTests

# TEKST: powtarzalny (L=3)
java -jar target/AiSD2025ZEx5.jar -m comp -s data/powtorzenia.txt -d tmp/powtorzenia.comp -l 3
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/powtorzenia.comp -d tmp/powtorzenia_out.txt
diff -q data/powtorzenia.txt tmp/powtorzenia_out.txt
ls -lh data/powtorzenia.txt tmp/powtorzenia.comp

# TEKST: bible (L=2) 
java -jar target/AiSD2025ZEx5.jar -m comp -s data/bible.txt -d tmp/bible.comp -l 2
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/bible.comp -d tmp/bible_out.txt
diff -q data/bible.txt tmp/bible_out.txt
ls -lh data/bible.txt tmp/bible.comp

# TEKST: niemanie (L=1)
java -jar target/AiSD2025ZEx5.jar -m comp -s data/niemanie.txt -d tmp/niemanie_l1.comp -l 1
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/niemanie_l1.comp -d tmp/niemanie_l1_out.txt
diff -q data/niemanie.txt tmp/niemanie_l1_out.txt
ls -lh data/niemanie.txt tmp/niemanie_l1.comp tmp/niemanie_l1_out.txt

# OBRAZEK: BMP (L=2) powtarzalny
java -jar target/AiSD2025ZEx5.jar -m comp -s data/obrazek1.bmp -d tmp/obrazek1.comp -l 2
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/obrazek1.comp -d tmp/obrazek1_out.bmp
cmp -s data/obrazek1.bmp tmp/obrazek1_out.bmp && echo "OK: BMP identyczny"
ls -lh data/obrazek1.bmp tmp/obrazek1.comp

# OBRAZEK: JPG
java -jar target/AiSD2025ZEx5.jar -m comp -s data/dogs.jpg -d tmp/dogs.comp -l 2
java -jar target/AiSD2025ZEx5.jar -m decomp -s tmp/dogs.comp -d tmp/dogsout.jpg
cmp -s data/dogs.jpg tmp/dogsout.jpg && echo "OK: JPG identyczny"
ls -lh data/dogs.jpg tmp/dogs.comp
```

**Windows (PowerShell):**
```powershell
cd HufmannCoding

# 1) Kompiluje projekt i robi JAR (szybko, bez testów)
mvn -q clean package -DskipTests

# TEKST: powtarzalny (L=3)
java -jar target\AiSD2025ZEx5.jar -m comp -s data\powtorzenia.txt -d tmp\powtorzenia.comp -l 3
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\powtorzenia.comp -d tmp\powtorzenia_out.txt
fc data\powtorzenia.txt tmp\powtorzenia_out.txt >nul && echo OK: TEKST identyczny || echo BLAD: TEKST rozny
dir data\powtorzenia.txt tmp\powtorzenia.comp

# TEKST: bible (L=2)
java -jar target\AiSD2025ZEx5.jar -m comp -s data\bible.txt -d tmp\bible.comp -l 2
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\bible.comp -d tmp\bible_out.txt
fc data\bible.txt tmp\bible_out.txt >nul && echo OK: TEKST identyczny || echo BLAD: TEKST rozny
dir data\bible.txt tmp\bible.comp

# TEKST: niemanie (L=1)
java -jar target\AiSD2025ZEx5.jar -m comp -s data\niemanie.txt -d tmp\niemanie_l1.comp -l 1
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\niemanie_l1.comp -d tmp\niemanie_l1_out.txt
fc data\niemanie.txt tmp\niemanie_l1_out.txt >nul && echo OK: TEKST identyczny || echo BLAD: TEKST rozny
dir data\niemanie.txt tmp\niemanie_l1.comp tmp\niemanie_l1_out.txt

# OBRAZEK: BMP (L=2) powtarzalny
java -jar target\AiSD2025ZEx5.jar -m comp -s data\obrazek1.bmp -d tmp\obrazek1.comp -l 2
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\obrazek1.comp -d tmp\obrazek1_out.bmp
fc /b data\obrazek1.bmp tmp\obrazek1_out.bmp >nul && echo OK: BMP identyczny || echo BLAD: BMP rozny
dir data\obrazek1.bmp tmp\obrazek1.comp

# OBRAZEK: JPG
java -jar target\AiSD2025ZEx5.jar -m comp -s data\dogs.jpg -d tmp\dogs.comp -l 2
java -jar target\AiSD2025ZEx5.jar -m decomp -s tmp\dogs.comp -d tmp\dogsout.jpg
fc /b data\dogs.jpg tmp\dogsout.jpg >nul && echo OK: JPG identyczny || echo BLAD: JPG rozny
dir data\dogs.jpg tmp\dogs.comp
```
