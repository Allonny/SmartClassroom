void RFID_read (void) {
  uint8_t uid[8];
  uint8_t uid_length;
  
  if ( nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uid_length, 25) ) {
      RFID_timout_time = timeout;
      
      if ( !card_holding ) {
        card_holding = true;
        
        String uid_str;
        for ( size_t i = 0; i < uid_length; i++ )
        {
          if ( uid[i] <= 0xF ) uid_str += F("0");
          uid_str += String(uid[i] & 0xff, HEX);
        }
        setting_update(UID_LABEL, uid_str);
        for_send(UID_LABEL, uid_str);
      }
    } else {
      card_holding = false;
      RFID_timout_time = 0;
    }
    
    digitalWrite(13, card_holding);
}

void echo_response (String param_value) {
  for_send(ECHO_LABEL, param_value);
}

void error_response (String param_value) {
  RFID_error = true;
  for_send(ERROR_LABEL, param_value);
}

void reset_request (String param_value) {
  if (param_value == KEYWORD_SETTINGS) { settings_reset(true); for_send(RESET_LABEL, KEYWORD_DONE); return; }
  if (param_value == KEYWORD_EEPROM) { EEPROM_reset(); for_send(RESET_LABEL, KEYWORD_DONE); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void uid_request (String param_value) {
  Serial.println(1);
  if (param_value == KEYWORD_GET) { for_send(UID_LABEL, setting_get(UID_LABEL)); return; }
  if (param_value == KEYWORD_RESET) { setting_update(UID_LABEL, uid_default); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void light_request (String param_value) {
  Serial.println(21);
  numeric_param_setup(LIGHT_LABEL, param_value, light_default);
  Serial.println(22);
}

void window_request (String param_value) {
  Serial.println(31);
  numeric_param_setup(WINDOW_LABEL, param_value, window_default);
  Serial.println(32);
}

void power_supply_request (String param_value) {
  Serial.println(4);
  if (param_value == KEYWORD_GET) { for_send(POWER_SUPPLY_LABEL, setting_get(POWER_SUPPLY_LABEL)); return;}
  if (param_value == KEYWORD_RESET) { setting_update(POWER_SUPPLY_LABEL, window_default); return; }
  if (param_value == KEYWORD_TRUE || param_value == KEYWORD_FALSE) setting_update(POWER_SUPPLY_LABEL, param_value);
  else error_response(KEYWORD_INVALID_INPUT);
}

void numeric_param_setup (String param, String param_value, String default_value) {
  if (param_value == KEYWORD_GET) { for_send(param, setting_get(param)); return; }
  if (param_value == KEYWORD_RESET) { setting_update(param, default_value); return; }
  bool isValid = true;
  Serial.println(5);
  for (size_t i = 0; i < param_value.length(); i++) isValid &= isDigit(param_value[i]);
  Serial.println(6);
  if (isValid) setting_update(param, param_value);
  else error_response(KEYWORD_INVALID_INPUT);
}
