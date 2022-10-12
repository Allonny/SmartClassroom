struct setting_const get_setting(String label, String defval, int address) {
  setting_const new_setting;
  new_setting.label = label;
  new_setting.defval = defval;
  new_setting.address = address;
  return new_setting;
}

void RFID_read (void) {
  uint8_t uid[8];
  uint8_t uid_length;
  
  if ( nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uid_length, 25) ) {
      RFID_timout_time = timeout;
      
      if ( !card_holding ) {
        card_holding = true;
        
        char uid_string[uid_length << 1 + 1];
        uid_string[uid_length << 1] = 0;
        for ( size_t i = 0; i < uid_length; i++ )
        {
          uint8_t double_digit = uid[i];
          uint8_t left_digit = (double_digit & 0xF0) >> 4;
          uint8_t right_digit = double_digit & 0x0F;
          
          uid_string[2 * i]     = (left_digit < 0xA ? '0' : ('A' - 0xA)) + left_digit;
          uid_string[2 * i + 1] = (right_digit < 0xA ? '0' : ('A' - 0xA)) + right_digit;
        }
        setting_update(LABEL_UID, uid_string);
        for_send(LABEL_UID, uid_string);
      }
    } else {
      card_holding = false;
      RFID_timout_time = 0;
    }
    
    digitalWrite(HOLD_LED, card_holding);
}

void system_request (String param_value) {
  if (param_value == KEYWORD_REBOOT) { digitalWrite(RESET_PIN, LOW); return; }
  if (param_value == KEYWORD_SAVE) { settings_save(); return; }
  if (param_value == KEYWORD_LOAD) { settings_load(); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void reset_request (String param_value) {
  if (param_value == KEYWORD_SETTINGS) { settings_reset(true); for_send(LABEL_RESET, KEYWORD_DONE); return; }
  if (param_value == KEYWORD_EEPROM) { EEPROM_reset(); for_send(LABEL_RESET, KEYWORD_DONE); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void echo_response (String param_value) {
  for_send(LABEL_ECHO, param_value);
}

void error_response (String param_value) {
  RFID_error = true;
  for_send(LABEL_ERROR, param_value);
}

void uid_request (String param_value) {
  if (param_value == KEYWORD_GET) { for_send(LABEL_UID, setting_get(LABEL_UID)); return; }
  if (param_value == KEYWORD_RESET) { setting_update(LABEL_UID, uid_default); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void light_request (String param_value) {
  numeric_param_setup(LABEL_LIGHT, param_value, light_default);
}

void window_request (String param_value) {
  numeric_param_setup(LABEL_WINDOW, param_value, window_default);
}

void power_supply_request (String param_value) {
  if (param_value == KEYWORD_GET) { for_send(LABEL_POWER_SUPPLY, setting_get(LABEL_POWER_SUPPLY)); return;}
  if (param_value == KEYWORD_RESET) { setting_update(LABEL_POWER_SUPPLY, window_default); return; }
  if (param_value == KEYWORD_TRUE || param_value == KEYWORD_FALSE) setting_update(LABEL_POWER_SUPPLY, param_value);
  else error_response(KEYWORD_INVALID_INPUT);
}

void numeric_param_setup (String param, String param_value, String default_value) {
  if (param_value == KEYWORD_GET) { for_send(param, setting_get(param)); return; }
  if (param_value == KEYWORD_RESET) { setting_update(param, default_value); return; }
  bool isValid = true;
  for (size_t i = 0; i < param_value.length(); i++) isValid &= isDigit(param_value[i]);
  if (isValid) setting_update(param, param_value);
  else error_response(KEYWORD_INVALID_INPUT);
}
