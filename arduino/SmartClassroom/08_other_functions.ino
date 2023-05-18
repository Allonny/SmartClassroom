struct pin_conf init_pin_conf(uint8_t type, uint8_t id) {
  pin_conf new_pin_conf = { .type = type, .id = id };
  return new_pin_conf;
}

struct pin_state init_pin_state(uint8_t pin, uint8_t state) {
  pin_state new_pin_state = { .pin = pin, .state = state };
  return new_pin_state;
}

void byte_to_hex(uint8_t value, char *line) {
  uint8_t left_digit = (value & 0xF0) >> 4;
  uint8_t right_digit = value & 0x0F;
  
  line[0] = (left_digit < 0xA ? '0' : ('A' - 0xA)) + left_digit;
  line[1] = (right_digit < 0xA ? '0' : ('A' - 0xA)) + right_digit;
}

uint8_t hex_to_halfbyte(char digit) {
  if( digit >= '0' && digit <= '9' ) digit -= '0';
  else if( digit >= 'A' && digit <= 'F' ) digit -= 'A' - 0xA;
  else if( digit >= 'a' && digit <= 'f' ) digit -= 'a' - 0xA;
  else digit = 0;
  return digit;
}

bool isNumber (String value) {
  bool valid = true;
  for(size_t i = 0; i < value.length(); i++) {
    valid &= isDigit(value[i]);
  }
  return value;
}

bool is_hex(String value) {
  value.toUpperCase();
  for(size_t i = 0; i < value.length(); i++) {
    if( !((value[i] >= '0' && value[i] <= '9') || (value[i] >= 'A' && value[i] <= 'F'))) return false;
  }
  return true;
}

bool is_config_line(String value) {
  if(value.length() != pin_map_size * 2) return false;
  return is_hex(value);
}

bool is_param_line(String value) {
  if(value.length() != sizeof(pin_state) * 2) return false;
  return is_hex(value);
}

uint8_t crt8_square(uint8_t value) {
  return ((uint16_t)value * value + 0xFF) >> 8;  
}

uint8_t crt8_cubic(uint8_t value) {
  return ((uint32_t)value * value + value + 0x1FD01) >> 16;  
}

void EEPROM_reset (void) {
  for( size_t i = 0; i < EEPROM.length(); i++ ) EEPROM.update(i, 0);
  for_send(LABEL_SYSTEM, KEYWORD_DONE);
}

void RFID_read (void) {
  uint8_t uid[8];
  uint8_t uid_length;
  
  if ( (*nfc).readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uid_length, 25) ) {
      RFID_timout_time = timeout;
      
      if ( !card_holding ) {
        card_holding = true;
        
        char uid_string[uid_length * 2 + 1];
        char *shift = uid_string;
        for ( size_t i = 0; i < uid_length; i++ )
        {
          byte_to_hex(uid[i], shift);
          shift += 2;
        }
        *shift = '\0';
        for_send(LABEL_UID, uid_string);
      }
    } else {
      card_holding = false;
      RFID_timout_time = 0;
    }
    
  digitalWrite(hold_led_pin, card_holding);
}

