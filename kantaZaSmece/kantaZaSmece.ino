/* ********* Program za upravljanje senzorom za kantu za smeće **********

kantaZaSmece.ino

Srednja škola za elektrotehniku i računalstvo Rijeka

Autori:
Vanja Ljubobratović (4.d - tehničar za računalstvo)
Patrik Horvat (4.d - tehničar za računalstvo)

Mentor:
Lovro Šverko, prof.

Školska godina: 2017/2018
*/

/************************* Biblioteke *********************************/

#include <ESP8266WiFi.h>  // Biblioteka za spajanje na WiFi mrežu
#include "Adafruit_MQTT.h" //Arduino biblioteka za MQTT podršku
#include "Adafruit_MQTT_Client.h" //Biblioteka za definiranje servera za slanje podatataka
#include <SoftwareSerial.h> //Biblioteka za serijsku komunikaciju
#include <TinyGPS.h> //Biblioteka za GPS NMEA prevođenje (dobivanje koordinata od GPS modula)

/************************* Podatci o WiFi mreži *********************************/

#define WLAN_SSID       "3D print"
#define WLAN_PASS       "forzafiume1987"

/************************* MQTT broker (Server IP:port) *********************************/

#define MQTT_SERVER      "192.168.30.104" // IP adresa raspberry pi [trenutna]
#define MQTT_SERVERPORT  1883

/***************************** Uređaji **************************************/

// definiranje input pinova na mikrokontroleru

#define SenzorZaPoklopac 2
#define SenzorZaDubinuTrig 14
#define SenzorZaDubinuEcho 12
#define SenzorPozara 16
#define GPSrx 4
#define GPStx 5

/******************************** Varijable  *****************************/

int detekcijaPozara;
int trajanjePinga;
int udaljenost;

bool newdata = false;

long lat, lon;
float flat, flon;
unsigned long age, date, time, chars;
int year;
byte month, day, hour, minute, second, hundredths;
unsigned short sentences, failed;

/************ Kreiranje objekata iz klasa WiFiClient i Adafruit_MQTT_Client ******************/

// objekt koji se spaja na mqtt (rezervirano ime), u njega ulaze podaci o WiFi mreži (WLAN_SSID i WLAM_PASS)
WiFiClient client;

// kreiranje objekta mqtt pomoću konstruktora (proslijeđuju se podaci iz objekta client (&client))

Adafruit_MQTT_Client mqtt(&client, MQTT_SERVER, MQTT_SERVERPORT);

/****************************** Kanali ***************************************/

// postavljanje kanala koje će mqtt server slušati
Adafruit_MQTT_Publish topicPoklopac = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/poklopac");
Adafruit_MQTT_Publish topicDubina = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/dubina");
Adafruit_MQTT_Publish topicPozar = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/pozar");
Adafruit_MQTT_Publish topicKorisnik = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/idKorisnika");
Adafruit_MQTT_Publish topicSirina = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/sirina");
Adafruit_MQTT_Publish topicDuljina = Adafruit_MQTT_Publish(&mqtt, "pametniGrad/otpad/node01/duljina");

// postavljanje kanala koje će senzor slušati
Adafruit_MQTT_Subscribe naredba1 = Adafruit_MQTT_Subscribe(&mqtt, "pametniGrad/otpad/node01/naredba1");

// funkcija za instanciranje
void MQTT_connect();
void gpsdump(TinyGPS &gps);
void printFloat(double f, int digits = 2);

/*************************** SETUP() ************************************/

void setup() {

	Serial.begin(115200);

	// Definiranje načina rada pinova na mikrokontroleru
	pinMode(SenzorPozara, INPUT);
	pinMode(SenzorZaDubinuEcho, INPUT);
	pinMode(SenzorZaDubinuTrig, OUTPUT);
	pinMode(SenzorZaPoklopac, INPUT);

	digitalWrite(SenzorZaDubinuEcho, LOW);
	digitalWrite(SenzorZaDubinuTrig, LOW);
	digitalWrite(SenzorPozara, LOW);

	delay(10);

	/*************************** Spajanje na WiFi ************************************/

	// debug
	Serial.println(); Serial.println();
	Serial.print("Spajanje na: ");
	Serial.println(WLAN_SSID);

	// spajanje na WiFi
	WiFi.begin(WLAN_SSID, WLAN_PASS);

	// debug
	while (WiFi.status() != WL_CONNECTED) {
		delay(500);
		Serial.print(".");
	}
	Serial.println();

	Serial.println("Spojen na WiFi");
	Serial.println("IP adresa: "); Serial.println(WiFi.localIP());

	// spajanje na kanale za slušanje naredbi
	mqtt.subscribe(&naredba1);
}




void loop() {

	// provjera je li senzor povezan sa mqtt serverom
	// prvi puta se spaja, nakon toga provjerava je li veza u redu
	// ako nije, spaja se ponovo na mqtt server
	MQTT_connect();
	
	//slušanje kanala
	slusajKanal();	

	// Svake 2 sekunde šalje se ažuriranje podataka, ne koristi se milis() 
	// funkcija zbog korištenja navažećih argumenata na pozive 
        // sustava, što uzrokuje ponovo pokretanje modula
	dzipies();

	// slanje podatka o dubini
	saljiDubinu();
	
	// slanje podatka o požaru
	saljiPozar();

	

	
	
}


// metoda za spajanje na mqtt server
// prilikom svakog pokretanja metode loop() provjerava se postoji li veza

void MQTT_connect() {

	// varijabla za provjeru stanja veze sa mqtt serverom
	int8_t provjeraVeze;

	// ako veza postoji ne radi ništa
	if (mqtt.connected()) {
		return;
	}

	/* ********** spajanje na mqtt server ********* */

	//debug
	Serial.print("Spajam se na MQTT... ");

	uint8_t pokusaj = 3;
	// kada se spoji na mqtt server, mqtt.connect() vraća vrijednost 0
	while ((provjeraVeze = mqtt.connect()) != 0) { 
		 // debug
		Serial.println(mqtt.connectErrorString(provjeraVeze));
		Serial.println("Ponovni pokušaj spajanja na MQTT za 5 sekundi...");
		// prekid veze sa mqtt serverom
		mqtt.disconnect();
		delay(5000);  // čekanje 5 sekundi
		pokusaj--;
		// 3 puta se pokušava uspostaviti konekcija sa mqtt serverom, ukoliko ne uspije sustav se ponovno pokreće
		if (pokusaj == 0) {
			ESP.reset();
		}
	}

	// debug
	Serial.println("MQTT Connected!");
}

void slusajKanal(){
	Adafruit_MQTT_Subscribe *subscription;
	while ((subscription = mqtt.readSubscription(5000))) {
		if (subscription == &naredba1)
		{

			// debug
			Serial.print(F("Got: "));
			Serial.println((char *)naredba1.lastread);
		}

	}
}

/* ********************************************* Dubina *********************************************** */

int izmjeriDubinu() {
	// priprema za ping
	digitalWrite(SenzorZaDubinuTrig, LOW);
	digitalWrite(SenzorZaDubinuEcho, LOW);
	delayMicroseconds(10);

	// ultrazvučni ping (50 ms)
	digitalWrite(SenzorZaDubinuTrig, HIGH);
	delayMicroseconds(50);
	digitalWrite(SenzorZaDubinuTrig, LOW);

	// odčitanje sa echo pina se pretvara u vrijeme u mikrosekundama
	trajanjePinga = pulseIn(SenzorZaDubinuEcho, HIGH);

	// udaljenost računamo prema formuli Udaljenost = (Vrijeme / 2) * 0.034, gdje se dijeljenje sa 2 izvršava zbog zvučnog vala
	// koji se šalje prema objektu te se odbija natrag, što udvostručuje put vala
	// množenje sa 0.034 se izvršava jer je brzina zvuka 340m/s, te kako bi dobili iznos u centimetrima vrijenost se preračunava
	// u prije spomenuti broj
	udaljenost = (trajanjePinga / 2) * 0.034;
	// ispis mjerenja
	Serial.print("Udaljenost cm: ");
	Serial.println(udaljenost);
	Serial.print("Trajanje pinga: ");
	Serial.println(trajanjePinga);

	return udaljenost;
}


void saljiDubinu(){
	udaljenost = izmjeriDubinu();

	// debug
	Serial.print(F("\nDubina "));

	// slanje i debug
	if (!topicDubina.publish(udaljenost)) {
		Serial.println(F("\nDubina nije poslano!"));
	}
	else {
		Serial.println(F("\nDubina poslano!"));
	}
}

/* ************************************    Požar    ********************************************* */
void saljiPozar(){
	// senzor za vatru
	detekcijaPozara = digitalRead(SenzorPozara);

	// debug
	Serial.print("Ima li požara? ");
	Serial.println(detekcijaPozara);

	if (detekcijaPozara==1)
	{
		// debug
		Serial.print(F("\nPožar HIGH "));

		// slanje i debug
		if (!topicPozar.publish(detekcijaPozara)) {
			Serial.println(F("\nPožar HIGH nije poslano!"));
		}
		else {
			Serial.println(F("\nPožar HIGH poslano!"));
		}
	}
	else
	{
		// debug
		Serial.print(F("\nPožar LOW "));

		// slanje i debug
		if (!topicPozar.publish(detekcijaPozara)) {
			Serial.println(F("\nPožar LOW nije poslano!"));
		}
		else {
			Serial.println(F("\nPožar LOW poslano!"));
		}

	}
}
/* ************************************ GPS podatci ********************************************* */
void dzipies(){
	delay(2000);
	newdata = true;
	char c = mySerial.read();


	if (newdata)
	{
		Serial.println("Dobiveni podatci");
		Serial.println("-------------");
		gpsdump(gps);
		Serial.println("-------------");
		Serial.println();
	}
}
 
 void gpsdump(TinyGPS &gps)
 {
 	
 
 	gps.get_position(&lat, &lon, &age);
 	Serial.print("Lat/Long(10^-5 deg): "); Serial.print(lat); Serial.print(", "); Serial.print(lon);
 
 	int sirina = (int)lat;
 	int duljina = (int)lon;
 
 	if (!topicSirina.publish(sirina)) {
 		Serial.println(F("\nSirina nije poslano!"));
 	}
 	else {
 		Serial.println(F("\nSirina poslano!"));
 	}
 	if (!topicDuljina.publish(duljina)) {
 		Serial.println(F("\nSirina nije poslano!"));
 	}
 	else {
 		Serial.println(F("\nSirina poslano!"));
 	}
 
 	gps.f_get_position(&flat, &flon, &age);
 	Serial.print("Lat/Long(float): ");
 	printFloat(flat, 5);
 	Serial.print(", ");
 	printFloat(flon, 5);
 
 	if (!topicSirina.publish(sirina)) {
 		Serial.println(F("\nSirina nije poslano!"));
 	}
 	else {
 		Serial.println(F("\nSirina poslano!"));
 	}
 	if (!topicDuljina.publish(duljina)) {
 		Serial.println(F("\nSirina nije poslano!"));
 	}
 	else {
 		Serial.println(F("\nSirina poslano!"));
 	}
 }
 
 
 void printFloat(double number, int digits)
 {
 	// za negativne brojeve
 	if (number < 0.0)
 	{
 		Serial.print('-');
 		number = -number;
 	}
 
 	// Ispravno zaokruživanje
 	double rounding = 0.5;
 	for (uint8_t i = 0; i < digits; ++i)
 		rounding /= 10.0;
 
 	number += rounding;
 
 	// izvlačenje dijela integera i ispis
 	unsigned long int_part = (unsigned long)number;
 	double remainder = number - (double)int_part;
 	Serial.print(int_part);
 
 	// Ispis decimalne točke, ali samo ako postoje znamenke nakon nje
 	if (digits > 0)
 		Serial.print(".");
 
 	// Izvlačenje znamenaka iz ostatka
 	while (digits-- > 0)
 	{
 		remainder *= 10.0;
 		int toPrint = int(remainder);
 		Serial.print(toPrint);
 		remainder -= toPrint;
 	}
 } 
