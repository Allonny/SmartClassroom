void setup(void) {
  config_load();
  
  digitalWrite(reset_pin, HIGH);
  delay(250);
  pinMode(reset_pin, OUTPUT);
  pinMode(hold_led_pin, OUTPUT);

  Adafruit_PN532 nfc_init(nfc_pin, 100);
  nfc_init.begin();
  if ( ! (nfc_init.getFirmwareVersion()) ) error_response(KEYWORD_SCANNER_NOT_FOUND);
  nfc_init.SAMConfig();
  nfc = &nfc_init;
  
  Serial.begin(SERIAL_SPEED);
  for_send(LABEL_STARTUP, KEYWORD_DONE);
}

void loop(void) {
  if ( (millis() - RFID_polling_timer >= RFID_polling_time + RFID_timout_time) && !RFID_error ) {
    RFID_polling_timer = millis();
    RFID_read();
  }
  
  if ( millis() - serial_polling_timer >= serial_polling_time ) {
    serial_polling_timer = millis();
    get_data();
    send_data();
  }

  if ( millis() - serial_buffer_reset_timer >= serial_polling_time ) {
    serial_buffer_reset_timer = millis();
    serial_buffer_length = 0;
  }
}
