#include <Dictionary.h>
#include <DictionaryDeclarations.h>

#include <Wire.h>
#include <SPI.h>
#include <Adafruit_PN532.h>

#define PN532_IRQ 9

Adafruit_PN532 nfc(PN532_IRQ, 100);

uint8_t uid[8];
uint8_t uid_length;
bool card_holding = false;

unsigned long RFID_polling_time = 100;
unsigned long RFID_polling_timer = 0;
unsigned long RFID_timout_time = 0;
unsigned long timeout = 2000;

unsigned long serial_polling_time = 20;
unsigned long serial_polling_timer = 0;

Dictionary *sentData = new Dictionary();

void response_setup(String value);

void setup(void) {
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  nfc.begin();
  if (!(nfc.getFirmwareVersion())) {
    Serial.println("err=SCANNER_NOT_FOUND;");
    while (true);
  }
  nfc.SAMConfig();
}

void RFID_read() {
  if (nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uid_length, 25)) {
      RFID_timout_time = timeout;
      
      if (!card_holding) {
        String uid_str;
        for (size_t i = 0; i < uid_length; i++)
        {
          if (uid[i] <= 0xF) uid_str += F("0");
          uid_str += String(uid[i] & 0xff, HEX);
        }
        sentData->insert("uid", uid_str);

        card_holding = true;
      }
    } else {
      card_holding = false;
      RFID_timout_time = 0;
    }
    
    digitalWrite(13, card_holding);
}

void parserData(String receivedData) {
  Dictionary *data = new Dictionary();
  String param;
  int separator;
  int sub_separator;
  while((separator = receivedData.indexOf(";")) > 0) {
    
    param = receivedData.substring(0, separator);
    receivedData = receivedData.substring(separator + 1);
    if((sub_separator = param.indexOf("=")) > 0) {
      data->insert(param.substring(0, sub_separator), param.substring(sub_separator + 1));
    }
  }

  if(data->search("setup") != "") response_setup(data->search("setup"));
}

void response_setup(String value) {
  sentData->insert("setup", "RESPONSE");
}

void sendData() {
  if(sentData->count() > 0) {
    String sentString = "";
    for(int i = 0; i < sentData->count(); i++) {
      sentString += sentData->key(i) + "=" + sentData->value(i) + ";";
    }
    Serial.println(sentString);
    sentData = new Dictionary();
  }
}

void getData() {
  if (Serial.available() > 0) {
    String receivedData = Serial.readString();
    receivedData.trim();
    parserData(receivedData);
  }
}

void loop(void) {
  if (millis() - RFID_polling_timer >= RFID_polling_time + RFID_timout_time) {
    RFID_polling_timer = millis();

    RFID_read();
  }

  if (millis() - serial_polling_timer >= serial_polling_time) {
    serial_polling_timer = millis();
    getData();
    sendData();
  }
}
