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

with urllib.request.urlopen("https://api.thingspeak.com/channels/196696.json?api_key=E5V4C2KMUN5S4BWM") as url:
    channelData = json.loads(url.read().decode())

jsonLastID = channelData['last_entry_id']

with urllib.request.urlopen("https://api.thingspeak.com/channels/196696/feeds.json?results=" + str(jsonLastID) + "api_key=E5V4C2KMUN5S4BWM") as url:
    data = json.loads(url.read().decode())

"""
    Ispred metode Entry su definirane liste za inicijalni unos podataka.
    Metoda Entry izvrsava inicijalni unos podataka u bazu.
    Upisuje SVE podatke sa JSONa u bazu.
    for u pythonu funkcionira kao foreach u C# sto znaci:
    npr. Svaki entry_id koji se nalazi u dijelu json-a pod ['feeds'] se stavlja u listu listEntryID .
"""
    
listEntryID = []
listID = []
listTemperaturaZraka = []
listVlagaZraka = []
listKvalitetaZraka = []
listRazinaCO2 = []
listRazinaCO = []
listDetekcijaOpasnihPlinova = []
listCreatedAt = []

def Entry():
    for entryID in data['feeds']:
        listEntryID.append(entryID['entry_id'])

    for ID in data['feeds']:
        listID.append(ID['field1'])

    for TemperaturaZraka in data['feeds']:
        listTemperaturaZraka.append(TemperaturaZraka['field2'])

    for VlagaZraka in data['feeds']:
        listVlagaZraka.append(VlagaZraka['field3'])

    for KvalitetaZraka in data['feeds']:
        listKvalitetaZraka.append(KvalitetaZraka['field4'])

    for RazinaCO2 in data['feeds']:
        listRazinaCO2.append(RazinaCO2['field5'])

    for RazinaCO in data['feeds']:
        listRazinaCO.append(RazinaCO['field6'])

    for DetekcijaOpasnihPlinova in data['feeds']:
        listDetekcijaOpasnihPlinova.append(DetekcijaOpasnihPlinova['field7'])

    for CreatedAt in data['feeds']:
        listCreatedAt.append(CreatedAt['created_at'])

    for i in range(0, jsonLastID):
        insert = "INSERT INTO meteoroloskaStanica VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s)"
        cursor.execute(insert, (listEntryID[i], listID[i], listTemperaturaZraka[i], listVlagaZraka[i], listKvalitetaZraka[i], listRazinaCO2[i], listRazinaCO[i], listDetekcijaOpasnihPlinova[i], listCreatedAt[i]))

    print("Podaci upisani u bazu podataka.\n")
    conn.commit()


"""
    Metoda provjerava ako je baza podataka potpuno prazna.
    Ako je, poziva metodu Entry() koja upisuje 100 podataka (maksimum for petlje je 100) u bazu.
    Ako baza ima podatke, metoda ne radi nista i program se nastavlja.
"""

def checkIfDatabaseIsEmpty():
    cursor.execute("SELECT * FROM meteoroloskaStanica")
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
updateID = []
updateTemperaturaZraka = []
updateVlagaZraka = []
updateKvalitetaZraka = []
updateRazinaCO2 = []
updateRazinaCO = []
updateDetekcijaOpasnihPlinova = []
updateCreatedAt = []

def updateDatabase():
    with urllib.request.urlopen("https://api.thingspeak.com/channels/196696/feeds.json?api_key=E5V4C2KMUN5S4BWM&results=1") as url:
        updateData = json.loads(url.read().decode())
        
    for newEntryID in updateData['feeds']:
        updateEntryID.append(newEntryID['entry_id'])
        
    for newID in updateData['feeds']:
        updateID.append(newID['field1'])

    for newTemperaturaZraka in updateData['feeds']:
        updateTemperaturaZraka.append(newTemperaturaZraka['field2'])

    for newVlagaZraka in updateData['feeds']:
        updateVlagaZraka.append(newVlagaZraka['field3'])

    for newKvalitetaZraka in updateData['feeds']:
        updateKvalitetaZraka.append(newKvalitetaZraka['field4'])

    for newRazinaCO2 in updateData['feeds']:
        updateRazinaCO2.append(newRazinaCO2['field5'])

    for newRazinaCO in updateData['feeds']:
        updateRazinaCO.append(newRazinaCO['field6'])

    for newDetekcijaOpasnihPlinova in updateData['feeds']:
        updateDetekcijaOpasnihPlinova.append(newDetekcijaOpasnihPlinova['field7'])

    for newCreatedAt in updateData['feeds']:
        updateCreatedAt.append(newCreatedAt['created_at'])

    for i in range(0, 1):
        insert = "INSERT INTO meteoroloskaStanica VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s)"
        cursor.execute(insert, (updateEntryID[i], updateID[i], updateTemperaturaZraka[i], updateVlagaZraka[i], updateKvalitetaZraka[i], updateRazinaCO2[i], updateRazinaCO[i], updateDetekcijaOpasnihPlinova[i], updateCreatedAt[i]))

    print("Podaci upisani u bazu podataka.")
    conn.commit()
    
    del updateEntryID[:]
    del updateID[:]
    del updateTemperaturaZraka[:]
    del updateVlagaZraka[:]
    del updateKvalitetaZraka[:]
    del updateRazinaCO2[:]
    del updateRazinaCO[:]
    del updateDetekcijaOpasnihPlinova[:]
    del updateCreatedAt[:]
    
    with urllib.request.urlopen("https://api.thingspeak.com/channels/196696.json?api_key=E5V4C2KMUN5S4BWM") as url:
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
    with urllib.request.urlopen("https://api.thingspeak.com/channels/196696.json?api_key=E5V4C2KMUN5S4BWM") as url:
        channelDataUpdate = json.loads(url.read().decode())
    
    listLastEntryID.append(channelDataUpdate['last_entry_id'])
    
    dt = datetime.datetime.now()
    
    if(listLastEntryID == updateLastEntryID):
         print("Baza je azurirana.")
         print(dt)
         print("\n")
         del listLastEntryID[:]
    elif(listLastEntryID != updateLastEntryID):
        print("\nAzuriranje baze podataka...")
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