/*
Projekt: Pametni grad
Tim: Meteorološka stanica s mjerenjem kvalitete zraka
Autori: 
  Patrik Perčinić, 1.c
  Niko Maričić, 1.c
Srednja škola za elektrotehniku i računalstvo, Rijeka
Školska godina 2018./2019.
*/

//biblioteke za upravljanje senzorima
#include <Wire.h>
#include "Adafruit_SGP30.h"
#include <DHT.h>
#include <SPI.h>
#include <WiFi101.h>


//definiranje pinova za senzore
#define DHTPIN 6
#define Senzor_CO 0
#define Senzor_plinova 1

//definiranje modela senzora za temperaturu i vlagu
#define DHTTYPE DHT11

//instanciranje senzora kvalitete zraka
Adafruit_SGP30 sgp;

// instanciranje senzora za temperaturu i vlažnost zraka
DHT dht(DHTPIN, DHTTYPE);

//funkcija za računanje vlage zraka
uint32_t getAbsoluteHumidity(float temperature, float humidity) {
  const float absoluteHumidity = 216.7f * ((humidity / 100.0f) * 6.112f * exp((17.62f * temperature) / (243.12f + temperature)) / (273.15f + temperature)); // [g/m^3]
  const uint32_t absoluteHumidityScaled = static_cast<uint32_t>(1000.0f * absoluteHumidity); // [mg/m^3]
  return absoluteHumidityScaled;
}

/// definiranje WiFi parametara

char ssid[] = "SmartCity"; 
char pass[] = "63346836"; 
int status = WL_IDLE_STATUS;

// instanciranje WiFiClient objekta
WiFiClient client;

// ThingSpeak postavke
char server[] = "api.thingspeak.com";
String writeAPIKey = "Q4FUFDCOQO1ENQ8Y";

// definiranje timer-a (interval od 120 sekundi)
unsigned long lastConnectionTime = 0;
const unsigned long postingInterval = 120L * 1000L;

/*
******************** S E T U P ********************
*/

void setup() {

  // pokretanje serijske komunikacije
  Serial.begin(9600);
  delay(50);

  //inicijalizacija senzora vlage i temparature
  dht.begin();
  
  // spajanje na WiFi mrežu
  while ( status != WL_CONNECTED) {
    status = WiFi.begin(ssid, pass);
    Serial.print("Spajanje na "); Serial.println(ssid);

    // inicijalizacija senzora za kvalitetu zraka,
    // čekanje da vrati signal da je pokrenut
    if (!sgp.begin()) {
      while (1);
    }
  }
}


/*
******************** L O O P ********************
*/

void loop() {

  // Ako je prošlo 120 sekundi od zadnjeg spajanja spaja se i šalje podatke
  if (millis() - lastConnectionTime > postingInterval) {
    saljiPodatke();

    //Ako nije očitao vrijednosti ponavlja čitanje
    if (!sgp.IAQmeasure()) {
      return;
    }
  }

}

void saljiPodatke() {

  //Čitanje podataka senzora
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  int Sensor_CO = analogRead(Senzor_CO);
  int Sensor_plinova = analogRead(Senzor_plinova);

  // Stvara string za slanje na ThingSpeak
  String data = String("field1=" + String(Senzor_CO, DEC) + "&field2=" + String(t, DEC) + "&field3=" + String(h, DEC) + "&field4=" + String(sgp.TVOC, DEC) + "&field5=" + String(sgp.eCO2, DEC) + "&field6=" + String(Senzor_plinova, DEC));

  // Zaustavlja sve aktivne veze prije slanja
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
