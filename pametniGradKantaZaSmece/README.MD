###Optimizacija odvoza otpada
##Tehnička dokumentacija
Autori: Manuel Bradičić, Patrik Horvat, Vanja Ljubobratović
Mentor: Lovro Šverko, prof.
O projektu
Neizostavna je karakteristika svakoga razvijenoga društva uz proizvodnju dobara također i generacija otpada kao produkt konzumacije i iskorištavanja spomenutih dobara te je njegova količina usko povezana s razvijenošću toga društva, države ili zajednice.
Stoga jedna od osnovnih smjernica svakoga razvijenoga društva jest dakako racionalno raspolaganje i gospodarenje otpadom što uključuje brojne procese od sakupljanja i transportacije do njegova odlaganja na za to predviđena mjesta uz korištenje prikladnih i najoptimalnijih metoda.
Zato smo se mi, učenici Srednje škole za elektrotehniku i računalstvo u Rijeci, htjeli uključiti i svojim znanjem na području programiranja, računalnih mreža i modernih tehnologija pomoći u rješavanju jednoga od najznačajnijih problema današnjice - odvoza otpada.
Budući da je odvoz otpada toliko opširan i golem proces prisutan u svakom naseljenom području, a pogotovo u gusto naseljenim gradovima njegovom optimizacijom se mogu postići brojne pogodnosti, a smanjenje cijena odvoza, ušteda vremena i najvažnije za okoliš, smanjenje emisija CO2 plina samo su neke od njih.
Kroz ostatak ove dokumentacije potanko ćemo objasniti na koji smo način upotrebom IoT (internet of things) tehnologije odlučili optimizirati i modernizirati način sakupljanja i odvoza otpada te tako doprinijeti našoj zajednici.
Ideja
Što predstavlja najveći problem kod odvoza otpada?
Naravno odgovor je da najveći problem predstavlja to što ne znamo koliko koji spremnik za otpad koji se nalazi negdje u gradu ili naselju sadrži otpada. Tako dolazimo do toga da se neki kamioni za odvoz vraćaju poluprazni dok se drugi vraćaju prekrcani u nemogućnosti da završe svoju rutu što zahtjeva ponovno zacrtavanje ruta ili slanje dodatnih kamiona. Time činimo ogromne ali i nepotrebne vremenske, novčane, ali i ekološke gubitke u vidu povećanja emisija CO2 plina.
No što kada bismo znali koliko svaki spremnik sadrži otpada?
Onda bi bilo vrlo jednostavno automatski zacrtati rute unutar zadanih ograničenja kako bismo potpuno iskoristili svako vozilo za odvoz otpada, bez nepotrebnih dodatnih troškova.
Kako znati koliko je svaki spremnik pun?
Iako postoji više metoda, mi smo se odlučili mjeriti popunjenost spremnika pomoću ultrazvučnog senzora koji vrijeme potrebno za povratak poslanog ultrazvučnog vala pretvara u udaljenost te nam tako daje uvid u razinu otpada u spremniku. Odabrali smo baš ovu medodu budući da čini najbolji balans između preciznosti, jednostavnosti, cijene i energetskih zahtjeva. Tako smo zamislili sustav gdje bi svaki spremnik za otpad imao ugrađen jedan mikroupravljač koji ima sposobnost bežićnog spajanja na mrežu pomoću koje bi na centralnu jedinicu slao podatke sa spojenog ultrazvučnog senzora, senzora za detekciju požara te GPS modula pomoću kojega bismo mogli smjestiti svaki senzor na kartu što bi se koristilo pri zacrtavanju rute sakupljanja otpada.
Hardware
U ovome odjeljku su opisane sve fizičke komponente od kojih se sastoji projekt.
Popis komponenti:
Raspberry Pi 3
Minijaturno računalo veličine kreditne kartice razvijeno 2016. godine sa 64 - bitnim quad - core procesorom na taktu od 1.2 GHz te jednim gigabajtom LPDDR2 radne memorije. Najčešći operativni sustav koji se koristi u radu s Raspberry Pi računalom jest Raspbian, operativni sustav baziran na Debian Linux-u. U ovome projektu smo koristili navedeni operacijski sustav te nam je Raspberry Pi računalo služilo kao koncentrator za sakupljanje podataka odnosno očitanja sa senzora koje mikroupravljač (u budućnosti više njih) šalje te kao kontrolna ploča sustava na koju je moguće ostvariti udaljeni pristup pomoću SSH ili VNC protokola.
Croduino NOVA
Mikrokontrolerska pločica hrvatske proizvodnje čija je glavna karakteristika ESP8266 čip koji podržava DNS dakle ima mogućnost spajanja na WiFi mreže. U ovom projektu koristili smo Croduino NOVA mikrokontroler kako bismo pomoću njega žičano povezali nekoliko senzora te preko bežične mreže sa udaljene lokacije slali isto toliko mjerenja na jednu centralnu jedinicu.
Ultrazvučni modul HC-SR04
HC-SR04 ultrazvučni modul, poput delfina ili šišmiša, koristi ultrazvučne valove frekvencije od 40 kHz kako bi odredio udaljenost od predmeta. Domet mu je od 2 do 200 cm dok mu je preciznost unutar 3 mm. U ovome projektu on je senzor najvažnije funkcije jer nam omogućava da mjerimo popunjenost našeg spremnika.
Senzor požara (fototranzistor) s LM393 komparatorom
Na ovome modulu se nalazi fototranzistor, komponenta koja je sposobna detektirati infracrveno zračenje i pretvoriti ga u signal binarnog oblika pomoću ugrađenog LM393 komparatora. Osim Sunca u velikim količinama infracrvene valove zrači i vatra te je tako senzor požara neizostavna komponenta u ovome projektu.
GPS modul NEO - 6M
GPS modul će nam dati informacije o trenutnoj lokaciji(geografska duljina i širina). Povezivanje s mikroupravljačem se vrši preko serijske komunikacije, a koristeći prikladnu biblioteku jednostavno je pristupiti izmjerenim koordinatama.
U ovom projektu koristili smo navedeni GPS modul kako bismo mogli imati uvid u to gdje se točno nalazi svaki senzor koji bi bio postavljen negdje na udaljenoj lokaciji te tu istu prikazati na karti u sučelju.
Također smo koristili i metodu geolokacije koja ne zahtjeva prisutnost GPS modula već mikroupravljač lokaciju određuje pomoću okolnih bežićnih mreža što ćemo posebno opisati u kodu uz kod za GPS modul kao dvije paralelne metode.
Hardversko rješenje
Ultrazvučni modul HC-SR04:
crvena - napon 5V crna - uzemljenje zelena - trigger pin koji se spaja na pin broj 14 mikroupravljača te omogućava slanje signala za okidanje ultrazvučnog vala žuta - echo pin koji se spaja na pin broj 15 mikroupravljača te šalje na mikroupravljač podatak o proteklom vremenu od ispaljivanja do primitka ultrazvučnog vala
Senzor požara:
crvena - napon 5V crna - uzemljenje narančasta - D0 pin koji je spojen na pin 15 mikroupravljača putem kojega na mikroupravljač šaljemo vrijednost binarnog tipa u ovisnosti o postojanju požara
GPS modul:
crvena - napon 5V crna - uzemljenje smeđa - Tx odnosno transmit pin koji se spaja na pin 5 mikroupravljača te služi za slanje podataka o geografskoj dužini i širini na mikroupravljač roza - Rx odnosno recieve pin koji se spaja na pin 4 mikroupravljača te služi za prijenos instrukcija od strane mikroupravljača

