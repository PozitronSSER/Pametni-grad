/*
  Meterološka stanica v.1
  Autor: Patrik Perčinić 1.c
  Srednja škola za elektrotehniku i računalstvo
  Šk. god. 2018/2019
*/
//Biblioteke za upravljanje senzorima
#include <Wire.h>
#include "Adafruit_SGP30.h"
#include <DHT.h>
#include <SPI.h>
#include <WiFi101.h>


//Definiranje pinova za senzore
#define DHTPIN 6
#define Senzor_CO 0
#define Senzor_plinova 1

//Definiranje modela senzora za temperaturu i vlagu
#define DHTTYPE DHT11

//Definiranje senzora kvalitete zraka
Adafruit_SGP30 sgp;

// Šalje podatke o pin-u i vrsti senzora biblioteci
DHT dht(DHTPIN, DHTTYPE);

//Funkcija za računanje vlage zraka
uint32_t getAbsoluteHumidity(float temperature, float humidity) {
  const float absoluteHumidity = 216.7f * ((humidity / 100.0f) * 6.112f * exp((17.62f * temperature) / (243.12f + temperature)) / (273.15f + temperature)); // [g/m^3]
  const uint32_t absoluteHumidityScaled = static_cast<uint32_t>(1000.0f * absoluteHumidity); // [mg/m^3]
  return absoluteHumidityScaled;
}

//Podatci za spajanje na wifi mreže

//  SSID (ime) mreže
char ssid[] = "SSER_ucenici";
// Šifra za mrežu
char pass[] = "12345678";

//Postavlja status spajanja na mrežu
int status = WL_IDLE_STATUS;

// Poziva biblioteku za mrežno spajanje
WiFiClient client;

// Postavke ThingSpeak-a
//API
char server[] = "api.thingspeak.com";
//ID kanala
String writeAPIKey = "Q4FUFDCOQO1ENQ8Y";

//Zabilježi zadnje vrijeme spajanja
unsigned long lastConnectionTime = 0;
//Šalje paket svako 20 sekundi
const unsigned long postingInterval = 20L * 1000L;

void setup() {
  delay(1000);
  //Testiranje izbrisat
  Serial.begin(9600);

  //Poziva biblioteku za senzor vlage i temparature
  dht.begin();
  // Pokušava se spjiti na wifi mrežu
  while ( status != WL_CONNECTED) {
    //Spajanje na wpa/wpa2 mrežu
    status = WiFi.begin(ssid);

    // Čekaj 10 sekundi da se spoji na mrežu
    delay(10000);
    //Ispunit
    if (!sgp.begin()) {
      while (1);
    }
  }
}

void loop() {
  //testiranje izbrisat
  Serial.print("TVOC "); Serial.print(sgp.TVOC); Serial.print(" ppb\t");
  Serial.print("eCO2 "); Serial.print(sgp.eCO2); Serial.println(" ppm");
  delay(1000);
  // Ako je prošlo 20 sekundi od zadnjeg spajanja spaja se i šalje paket
  if (millis() - lastConnectionTime > postingInterval) {
    httpRequest();

    //Ako nije očitao vrijednosti ponavlaj čitanje
    if (!sgp.IAQmeasure()) {
      return;
    }
  }

}

void httpRequest() {

  //Čitanje podataka senzora
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  int Sensor_CO = analogRead(Senzor_CO);
  int Sensor_plinova = analogRead(Senzor_plinova);

  // Stvara paket podataka za slanje na ThingSpeak
  String data = String("field1=" + String(Senzor_CO, DEC) + "&field2=" + String(t, DEC) + "&field3=" + String(h, DEC) + "&field4=" + String(sgp.TVOC, DEC) + "&field5=" + String(sgp.eCO2, DEC) + "&field6=" + String(Senzor_plinova, DEC));

  // Zaustavlja sve aktivne konekcije prije slanja
  client.stop();

  // Šalje podatke na ThingSpeak
  if (client.connect(server, 80)) {
    client.println("POST /update HTTP/1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("User-Agent: ArduinoWiFi/1.1");
    client.println("X-THINGSPEAKAPIKEY: " + writeAPIKey);
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.print(data.length());
    client.print("\n\n");
    client.print(data);

    // Zabilježi vrijeme zadnjeg slanja
    lastConnectionTime = millis();
  }
}
