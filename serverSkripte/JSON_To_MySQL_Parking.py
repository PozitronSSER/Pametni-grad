#!/usr/bin/python3
import mysql.connector, urllib.request, json, time, datetime

"""
    String za spajanje na MySQL bazu podataka.
"""
try:
    conn = mysql.connector.connect(
    host='portmap.io',
    port='61974',
    database='smartcity',
    user='test',
    password='123');
    print("Povezivanje na bazu podataka uspjesno.\n")
except EnvironmentError as e:
    print("Povezivanje neuspjelo. Provjerite podatke.\n")

    
"""
    Povlacenje JSON-a sa ThingSpeak servera.
    urllib.request.urlopen salje http request stranici, i zatim json.loads pretvara json u python objekte.
"""

cursor = conn.cursor()

with urllib.request.urlopen("https://api.thingspeak.com/channels/629316.json?api_key=Y4KQ33L0LZM0GSZX") as url:
    channelData = json.loads(url.read().decode())

jsonLastID = channelData['last_entry_id']

with urllib.request.urlopen("https://api.thingspeak.com/channels/629316/feeds.json?results=" + str(jsonLastID) + "api_key=Y4KQ33L0LZM0GSZX") as url:
    data = json.loads(url.read().decode())

"""
    Ispred metode Entry su definirane liste za inicijalni unos podataka.
    Metoda Entry izvrsava inicijalni unos podataka u bazu.
    Upisuje SVE podatke sa JSONa u bazu.
    for u pythonu funkcionira kao foreach u C# sto znaci:
    npr. Svaki entry_id koji se nalazi u dijelu json-a pod ['feeds'] se stavlja u listu listEntryID .
"""
    
listEntryID = []
listParkirnoMjesto1 = []
listParkirnoMjesto2 = []
listParkirnoMjesto3 = []
listDateCreated = []

def Entry():
    for entryID in data['feeds']:
        listEntryID.append(entryID['entry_id'])

    for ParkirnoMjesto1 in data['feeds']:
        listParkirnoMjesto1.append(ParkirnoMjesto1['field1'])

    for ParkirnoMjesto2 in data['feeds']:
        listParkirnoMjesto2.append(ParkirnoMjesto2['field2'])

    for ParkirnoMjesto3 in data['feeds']:
        listParkirnoMjesto3.append(ParkirnoMjesto3['field3'])

    for dateCreated in data['feeds']:
        listDateCreated.append(dateCreated['created_at'])

    for i in range(0, jsonLastID):
        insert = "INSERT INTO parkingMjesta(entry_id, ParkirnoMjesto1, ParkirnoMjesto2, ParkirnoMjesto3, created_at) VALUES(%s, %s, %s, %s, %s)"
        cursor.execute(insert, (listEntryID[i], listParkirnoMjesto1[i], listParkirnoMjesto2[i], listParkirnoMjesto3[i], listDateCreated[i]))

    print("Podaci upisani u bazu podataka.\n")
    conn.commit()


"""
    Metoda provjerava ako je baza podataka potpuno prazna.
    Ako je, poziva metodu Entry() koja upisuje 100 podataka (maksimum for petlje je 100) u bazu.
    Ako baza ima podatke, metoda ne radi nista i program se nastavlja.
"""

def checkIfDatabaseIsEmpty():
    cursor.execute("SELECT * FROM parkingMjesta")
    cursor.fetchall()
    brojPodatka = cursor.rowcount
    
    if (brojPodatka != 0):
        print("Baza ima podatke.\n")
    elif (brojPodatka == 0):
        print("Baza je prazna. Dodavanje podataka...")
        Entry()

checkIfDatabaseIsEmpty()

"""
    Ispred metode updateDatabase definirane su liste za azuriranje baze podataka.
    !!! updateDatabase() je pozvan tek u while loopu na kraju koda !!!
    Metoda updateDatabase() azurira bazu podataka:
    1. Posalje se novi request za json sa thingspeak-a, ali ovaj put samo za zadnjim unesenim podatkom (zato je &results=1 u URL-u)
    2. Podaci se stavljaju u updated liste i zatim na isti nacin kao i kod Entry() metode upisuju u bazu podataka
    3. Nakon toga se liste ociste i ostanu prazne tako da su spremne za novi update. To se radi naredbom npr. del updateEntryID[:]
    4. Postavlja se updatedLastEntryID na najnoviji last_entry_id. To je polje u jsonu koje nam govori koje je id od zadnjeg upisanog podatka.
"""

updateEntryID = []
updateParkirnoMjesto1 = []
updateParkirnoMjesto2 = []
updateParkirnoMjesto3 = []
updateDateCreated = []

def updateDatabase():
    with urllib.request.urlopen("https://api.thingspeak.com/channels/629316/feeds.json?api_key=Y4KQ33L0LZM0GSZX&results=1") as url:
        updateData = json.loads(url.read().decode())

    for newEntryID in updateData['feeds']:
        updateEntryID.append(newEntryID['entry_id'])

    for newParkirnoMjesto1 in updateData['feeds']:
        updateParkirnoMjesto1.append(newParkirnoMjesto1['field1'])

    for newParkirnoMjesto2 in updateData['feeds']:
        updateParkirnoMjesto2.append(newParkirnoMjesto2['field2'])

    for newParkirnoMjesto3 in updateData['feeds']:
        updateParkirnoMjesto3.append(newParkirnoMjesto3['field3'])

    for newDateCreated in data['feeds']:
        updateDateCreated.append(newDateCreated['created_at'])

    for i in range(0,1):
        insert = "INSERT INTO parkingMjesta(entry_id, ParkirnoMjesto1, ParkirnoMjesto2, ParkirnoMjesto3, created_at) VALUES(%s, %s, %s, %s, %s)"
        cursor.execute(insert, (updateEntryID[i], updateParkirnoMjesto1[i], updateParkirnoMjesto2[i], updateParkirnoMjesto3[i], updateDateCreated[i]))
    conn.commit()
    
    del updateEntryID[:]
    del updateParkirnoMjesto1[:]
    del updateParkirnoMjesto2[:]
    del updateParkirnoMjesto3[:]
    del updateDateCreated[:]

    with urllib.request.urlopen("https://api.thingspeak.com/channels/629316.json?api_key=Y4KQ33L0LZM0GSZX") as url:
        channelDataUpdate2 = json.loads(url.read().decode())
    
    del updateLastEntryID[:]
    updateLastEntryID.append(channelDataUpdate2['last_entry_id'])


"""
    while True loop preko metoda checkForUpdate() stalno provjerava je li se promjenio 'last_entry_id' tako da:
    1. Stalno se provjerava 'last_entry_id' naredbom:
            listLastEntryID.append(channelData['last_entry_id'])
    2. U if petlji se usporeduje sa updatedLastEntryID koji je IZVAN while petlje i metode postavljen na isto:
            updateLastEntryID.append(channelData['last_entry_id'])

    Ako su obije varijable iste, baza podataka se ne mijenja i ispisuje se poruka da je azurirana.
    U suprotnom, baza se azurira i zatim se poziva metoda updateDatabase() koja na svom kraju
    postavlja novi updatedLastEntryID. Tako LastEntryID i updateLastEntryID opet postaju isti dok se ne pojavi
    novi last_entry_id podatak na jsonu.

    time.sleep naredba vrti while loop svakih 5 sekundi.
"""

delay = 5

listLastEntryID = []

updateLastEntryID = []
updateLastEntryID.append(channelData['last_entry_id'])

def checkForUpdate():
    with urllib.request.urlopen("https://api.thingspeak.com/channels/629316.json?api_key=Y4KQ33L0LZM0GSZX") as url:
        channelDataUpdate = json.loads(url.read().decode())
    
    listLastEntryID.append(channelDataUpdate['last_entry_id'])
    
    dt = datetime.datetime.now()
    
    if(listLastEntryID == updateLastEntryID):
         print("Baza je azurirana.")
         print(dt)
         print("\n")
         del listLastEntryID[:]
    elif(listLastEntryID != updateLastEntryID):
        print("Azuriranje baze podataka...")
        updateDatabase()
        print("Baza podataka azurirana.")
        print(dt)
        print("\n")
        del listLastEntryID[:]

while True:
    print("updatedLastEntryID = " + str(updateLastEntryID))

    checkForUpdate();
    
    time.sleep(delay)
    
conn.close()