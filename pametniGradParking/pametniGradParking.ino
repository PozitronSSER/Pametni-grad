#include <ThingSpeak.h>
#include <SPI.h>
#include <WiFi101.h>

// Definiranje echo i trig pinova

#define echo1 6
#define echo2 8
#define echo3 10
#define trig1 7
#define trig2 9
#define trig3 11
#define ledCrvena1 5
#define ledZelena1 4
#define ledCrvena2 3
#define ledZelena2 2
#define ledCrvena3 1
#define ledZelena3 0


// Definiranje varijabli
long trajanje1;
long trajanje2;
long trajanje3;
int udaljenost1;
int udaljenost2;
int udaljenost3;
bool stanje1;
bool stanje2;
bool stanje3;
bool prethodnoStanje1;
bool prethodnoStanje2;
bool prethodnoStanje3;

char ssid[] = "SmartCity"; //  your network SSID (name)
char pass[] = "63346836"; // your network password

int status = WL_IDLE_STATUS;

// Initialize the Wifi client library
WiFiClient client;

// ThingSpeak Settings
char server[] = "api.thingspeak.com";
String writeAPIKey = "7AHJM23DSEDESOZ0";

unsigned long lastConnectionTime = 0; // track the last connection time
const unsigned long postingInterval = 20L * 1000L; // post data every 120 seconds

bool izmjeriUdaljenost1() {
  // Postavlja trig1 na NISKO
  digitalWrite(trig1, LOW);
  delayMicroseconds(2);
  // Stavlja trigPin na VISOKU razinu na 10 mikrosekundi
  digitalWrite(trig1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig1, LOW);
  // Čita echoPIN, vraća vrijeme putovanja zvučnog vala u mikrosekundama
  trajanje1 = pulseIn(echo1, HIGH);
  // Računa udaljenost
  udaljenost1 = trajanje1 * 0.034 / 2;
  // Ispisuje udaljenost na Serijskom Monitoru

  if (udaljenost1 < 5) {
    stanje1 = true;
    digitalWrite(ledCrvena1, HIGH);
    digitalWrite(ledZelena1, LOW);
  }
  else {
    stanje1 = false;
    digitalWrite(ledCrvena1, LOW);
    digitalWrite(ledZelena1, HIGH);
  }

  Serial.print("Stanje 1: ");
  Serial.println(stanje1);
  return stanje1;
}


bool izmjeriUdaljenost2() {
  // Postavlja trig2 na NISKO
  digitalWrite(trig2, LOW);
  delayMicroseconds(2);
  // Stavlja trigPin na VISOKU razinu na 10 mikrosekundi
  digitalWrite(trig2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig2, LOW);
  // Čita echoPIN, vraća vrijeme putovanja zvučnog vala u mikrosekundama
  trajanje2 = pulseIn(echo2, HIGH);
  // Računa udaljenost
  udaljenost2 = trajanje2 * 0.034 / 2;
  if (udaljenost2 < 5) {
    stanje2 = true;
    digitalWrite(ledCrvena2, HIGH);
    digitalWrite(ledZelena2, LOW);
  }
  else {
    stanje2 = false;
    digitalWrite(ledCrvena2, LOW);
    digitalWrite(ledZelena2, HIGH);
  }
  Serial.print("Stanje 2: ");
  Serial.println(stanje2);
  return stanje2;
}

bool izmjeriUdaljenost3() {
  // Postavlja trig3 na NISKO
  digitalWrite(trig3, LOW);
  delayMicroseconds(2);
  // Stavlja trigPin na VISOKU razinu na 10 mikrosekundi
  digitalWrite(trig3, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig3, LOW);
  // Čita echoPIN, vraća vrijeme putovanja zvučnog vala u mikrosekundama
  trajanje3 = pulseIn(echo3, HIGH);
  // Računa udaljenost
  udaljenost3 = trajanje3 * 0.034 / 2;

  if (udaljenost3 < 5) {
    stanje3 = true;
    digitalWrite(ledCrvena3, HIGH);
    digitalWrite(ledZelena3, LOW);
  }
  else {
    stanje3 = false;
    digitalWrite(ledCrvena3, LOW);
    digitalWrite(ledZelena3, HIGH);
  }
  Serial.print("Stanje 3: ");
  Serial.println(stanje3);

  return stanje3;
}

void posaljiSve() {

  // create data string to send to ThingSpeak
  String data = String("field1 = " + String(stanje1, DEC) + "&field2 = " + String(stanje2, DEC) + "&field3 = " + String(stanje3, DEC));

  // close any connection before sending a new request
  client.stop();

  // POST data to ThingSpeak
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

    // note the last connection time
    lastConnectionTime = millis();
  }
}

void posaljiPrvi() {

  // create data string to send to ThingSpeak
  String data = String("field1 = " + String(stanje1, DEC));

  // close any connection before sending a new request
  client.stop();

  // POST data to ThingSpeak
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

    // note the last connection time
    lastConnectionTime = millis();
  }
}

void posaljiDrugi() {

  // create data string to send to ThingSpeak
  String data = String("field2 = " + String(stanje2, DEC));

  // close any connection before sending a new request
  client.stop();

  // POST data to ThingSpeak
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

    // note the last connection time
    lastConnectionTime = millis();
  }
}

void posaljiTreci() {

  // create data string to send to ThingSpeak
  String data = String("field3 = " + String(stanje3, DEC));

  // close any connection before sending a new request
  client.stop();

  // POST data to ThingSpeak
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

    // note the last connection time
    lastConnectionTime = millis();
  }
}

void setup() {
  pinMode(echo1, INPUT);
  pinMode(echo2, INPUT);
  pinMode(echo3, INPUT);
  pinMode(trig1, OUTPUT);
  pinMode(trig2, OUTPUT);
  pinMode(trig3, OUTPUT);
  pinMode (ledCrvena1, OUTPUT);
  pinMode (ledZelena1, OUTPUT);
  pinMode (ledCrvena2, OUTPUT);
  pinMode (ledZelena2, OUTPUT);
  pinMode (ledCrvena3, OUTPUT);
  pinMode (ledZelena3, OUTPUT);

  //Isključivanje echo i trig senzora i ledica
  digitalWrite(echo1, LOW);
  digitalWrite(echo2, LOW);
  digitalWrite(echo3, LOW);
  digitalWrite(trig1, LOW);
  digitalWrite(trig2, LOW);
  digitalWrite(trig3, LOW);
  digitalWrite (ledCrvena1, LOW);
  digitalWrite (ledZelena1, LOW);
  digitalWrite (ledCrvena2, LOW);
  digitalWrite (ledZelena2, LOW);
  digitalWrite (ledCrvena3, LOW);
  digitalWrite (ledZelena3, LOW);

  Serial.begin(9600);
  delay(50);

  // attempt to connect to Wifi network
  while ( status != WL_CONNECTED) {
    // Connect to WPA/WPA2 Wi-Fi network
    status = WiFi.begin(ssid, pass);
    Serial.print("Spajanje na "); Serial.println(ssid);
  }

  stanje1 = izmjeriUdaljenost1();
  prethodnoStanje1 = stanje1;
  delay(50);
  stanje2 = izmjeriUdaljenost2();
  prethodnoStanje2 = stanje2;
  delay(50);
  stanje3 = izmjeriUdaljenost3();
  prethodnoStanje3 = stanje3;
  delay(50);
  posaljiSve();
}

void loop() {

  stanje1 = izmjeriUdaljenost1();
  if (prethodnoStanje1 != stanje1) {
    prethodnoStanje1 = stanje1;
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


  // if interval time has passed since the last connection,
  // then connect again and send data
  if (millis() - lastConnectionTime > postingInterval) {
    posaljiSve();
  }


}
