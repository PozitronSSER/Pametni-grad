
/*
Projekt: Pametni grad
Tim: Pametni parking
Autori: 
  Adrian Prodan, 1.c
  Bernard Miculinić, 1.c
Mentor: Lovro Šverko, prof.
Srednja škola za elektrotehniku i računalstvo, Rijeka
Školska godina 2018./2019.
*/

#include "ThingSpeak.h"
#include <SPI.h>
#include <WiFi101.h>
#include <HCSR04.h>

// Definiranje pinova
UltraSonicDistanceSensor senzor1(7, 6);
UltraSonicDistanceSensor senzor2(9, 8);
UltraSonicDistanceSensor senzor3(11, 10);
#define ledCrvena1 5
#define ledZelena1 4
#define ledCrvena2 3
#define ledZelena2 2
#define ledCrvena3 1
#define ledZelena3 0


// Definiranje varijabli
int stanje1;
int stanje2;
int stanje3;
int prethodnoStanje1;
int prethodnoStanje2;
int prethodnoStanje3;

// definiranje WiFi parametara
char ssid[] = "SmartCity"; 
char pass[] = "63346836"; 
int status = WL_IDLE_STATUS;

// instanciranje WiFiClient objekta
WiFiClient client;

// ThingSpeak postavke
unsigned long myChannelNumber = 629316;
const char * myWriteAPIKey = "8H2X8SLUEBK8TR0U";

// definiranje timer-a (interval od 120 sekundi)
unsigned long lastConnectionTime = 0; 
const unsigned long postingInterval = 20L * 1000L; 

/*
---------- funkcije za mjerenje udaljenosti / zauzetosti parkirnog mjesta
*/
bool izmjeriUdaljenost1() {

  // određuje se stanje parkirnog mjesta: true - zauzeto, false - slobodno
  if (senzor1.measureDistanceCm() <= 5) {
    stanje1 = 1;
    digitalWrite(ledCrvena1, HIGH);
    digitalWrite(ledZelena1, LOW);
  }
  else {
    stanje1 = 0;
    digitalWrite(ledCrvena1, LOW);
    digitalWrite(ledZelena1, HIGH);
  }

  // debug
  Serial.print("Stanje 1: ");
  Serial.print(stanje1);
  Serial.print("\t");
  Serial.println(senzor1.measureDistanceCm());
  
  //vraća stanje parkirnog mjesta
  return stanje1;
}


bool izmjeriUdaljenost2() {
  
  if (senzor2.measureDistanceCm() <= 5) {
    stanje2 = 1;
    digitalWrite(ledCrvena2, HIGH);
    digitalWrite(ledZelena2, LOW);
  }
  else {
    stanje2 = 0;
    digitalWrite(ledCrvena2, LOW);
    digitalWrite(ledZelena2, HIGH);
  }
  Serial.print("Stanje 2: ");
  Serial.print(stanje2);
  Serial.print("\t");
  Serial.println(senzor2.measureDistanceCm());
  return stanje2;
}

bool izmjeriUdaljenost3() {
  
  if (senzor3.measureDistanceCm() <= 5) {
    stanje3 = 1;
    digitalWrite(ledCrvena3, HIGH);
    digitalWrite(ledZelena3, LOW);
  }
  else {
    stanje3 = 0;
    digitalWrite(ledCrvena3, LOW);
    digitalWrite(ledZelena3, HIGH);
  }
  Serial.print("Stanje 3: ");
  Serial.print(stanje3);
  Serial.print("\t");
  Serial.println(senzor3.measureDistanceCm());

  return stanje3;
}

/*
---------- funkcije za slanje podataka na ThingSpeak 
*/

/*void posaljiSve() {
  ThingSpeak.setField(1, stanje1);
  ThingSpeak.setField(2, stanje2);
  ThingSpeak.setField(3, stanje3);

  ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);
  //client.stop();

  // POST na ThingSpeak preko porta 80 (HTTP)
  if (client.connect(server, 80)) {
    client.println("POST / update HTTP / 1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("User - Agent: ArduinoWiFi / 1.1");
    client.println("X - THINGSPEAKAPIKEY: " + writeAPIKey);
    client.println("Content - Type: application / x - www - form - urlencoded");
    client.print("Content - Length: ");
    client.print(data.length());
    client.print("\n\n");
    client.print(data);

    // spremanje vremena posljednjeg spajanja
    lastConnectionTime = millis();
  }*/


/*void posaljiPrvi() {

  String data = String("field1 = " + String(stanje1, DEC));
  client.stop();
  if (client.connect(server, 80)) {
    client.println("POST / update HTTP / 1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("User - Agent: ArduinoWiFi / 1.1");
    client.println("X - THINGSPEAKAPIKEY: " + writeAPIKey);
    client.println("Content - Type: application / x - www - form - urlencoded");
    client.print("Content - Length: ");
    client.print(data.length());
    client.print("\n\n");
    client.print(data);
    lastConnectionTime = millis();
  }
}

void posaljiDrugi() {

  String data = String("field2 = " + String(stanje2, DEC));
  client.stop();
  if (client.connect(server, 80)) {
    client.println("POST / update HTTP / 1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("User - Agent: ArduinoWiFi / 1.1");
    client.println("X - THINGSPEAKAPIKEY: " + writeAPIKey);
    client.println("Content - Type: application / x - www - form - urlencoded");
    client.print("Content - Length: ");
    client.print(data.length());
    client.print("\n\n");
    client.print(data);
    lastConnectionTime = millis();
  }
}

void posaljiTreci() {

  String data = String("field3 = " + String(stanje3, DEC));
  client.stop();
  if (client.connect(server, 80)) {
    client.println("POST / update HTTP / 1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("User - Agent: ArduinoWiFi / 1.1");
    client.println("X - THINGSPEAKAPIKEY: " + writeAPIKey);
    client.println("Content - Type: application / x - www - form - urlencoded");
    client.print("Content - Length: ");
    client.print(data.length());
    client.print("\n\n");
    client.print(data);
    lastConnectionTime = millis();
  }
}

/*
******************** S E T U P ********************
*/

void setup() {
  // definiranje U/I pinova
  pinMode (ledCrvena1, OUTPUT);
  pinMode (ledZelena1, OUTPUT);
  pinMode (ledCrvena2, OUTPUT);
  pinMode (ledZelena2, OUTPUT);
  pinMode (ledCrvena3, OUTPUT);
  pinMode (ledZelena3, OUTPUT);

  digitalWrite (ledCrvena1, LOW);
  digitalWrite (ledZelena1, LOW);
  digitalWrite (ledCrvena2, LOW);
  digitalWrite (ledZelena2, LOW);
  digitalWrite (ledCrvena3, LOW);
  digitalWrite (ledZelena3, LOW);

  // pokretanje serijske komunikacije
  Serial.begin(9600);
  delay(50);
  
  // spajanje na WiFi mrežu
  while ( status != WL_CONNECTED) {
    status = WiFi.begin(ssid, pass);
    Serial.print("Spajanje na "); Serial.println(ssid);
  }

  ThingSpeak.begin(client);
  /*
  početno čitanje sa senzora zbog 
  postavljanja vrijednosti u varijable prethodnoStanje,
  slanje svih podataka na ThingSpeak kanal
  */  
  stanje1 = izmjeriUdaljenost1();
  prethodnoStanje1 = stanje1;
  delay(50);
  stanje2 = izmjeriUdaljenost2();
  prethodnoStanje2 = stanje2;
  delay(50);
  stanje3 = izmjeriUdaljenost3();
  prethodnoStanje3 = stanje3;
  delay(50);
//  posaljiSve();
}
/*
******************** L O O P ********************
*/
void loop() {
/*
  // čitanje senzora
  stanje1 = izmjeriUdaljenost1();
  // slanje podataka na ThingSpeak, ako se stanje promijenilo (zbog uštede podatkovnog prometa)
  if (prethodnoStanje1 != stanje1) {
    prethodnoStanje1 = stanje1;
    // debug
    Serial.println("Šaljem Stanje 1");
    posaljiPrvi();
  }
  delay(50);

  stanje2 = izmjeriUdaljenost2();
  if (prethodnoStanje2 != stanje2) {
    prethodnoStanje2 = stanje2;
    Serial.println("Šaljem Stanje 2");
    posaljiDrugi();
  }
  delay(50);

  stanje3 = izmjeriUdaljenost3();
  if (prethodnoStanje3 != stanje3) {
    prethodnoStanje3 = stanje3;
    Serial.println("Šaljem Stanje 3");
    posaljiTreci();
  }
  delay(50);


  // slanje svih podataka svake dvije minute
  if (millis() - lastConnectionTime > postingInterval) {
    posaljiSve();
  }
*/

  stanje1 = izmjeriUdaljenost1();
  stanje2 = izmjeriUdaljenost2();
  stanje3 = izmjeriUdaljenost3();
  
  ThingSpeak.setField(1, stanje1);
  ThingSpeak.setField(2, stanje2);
  ThingSpeak.setField(3, stanje3);

  ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);
  delay(16000);


}
