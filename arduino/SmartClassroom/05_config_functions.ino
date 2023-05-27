void config_load (void) {
  config_reset();
  
  for( size_t i = 0; i < pin_map_size; i++ ) {
    if( pin_map[i].type & ALLOW_PIN ) pin_map[i].id = EEPROM.read( PIN_CONF_ADDRESS + i );
  }
  
  config_update();
  for_send(LABEL_LOAD, KEYWORD_DONE);
}

void config_save (void) {
  for( size_t i = 0; i < pin_map_size; i++ ) {
    EEPROM.update( PIN_CONF_ADDRESS + i , pin_map[i].id );
  }
  for_send(LABEL_SAVE, KEYWORD_DONE);
}

void config_update (void) {
  light_group.count = 0;
  window_group.count = 0;
  power_group.count = 0;
  
  for( size_t i = 0; i < pin_map_size; i++ ) {
    if( pin_map[i].type & ALLOW_PIN ) {
      pinMode(i, OUTPUT);
      switch( pin_map[i].id & ID_MASK ) {
        case NONE_ID:   break;
        case LIGHT_ID:  pin_map[i].id |= group_append(&light_group, i); break;
        case WINDOW_ID: pin_map[i].id |= group_append(&window_group, i); break;
        case POWER_ID:  pin_map[i].id |= group_append(&power_group, i); break;
        default:        break;
      }
    } else {
      switch( pin_map[i].id ) {
        case NFC_ID:      nfc_pin = i;      break;
        case RESET_ID:    reset_pin = i;    break;
        case HOLD_LED_ID: hold_led_pin = i; break;
      }
    }
  }
}

void config_reset (void) {
  for(size_t i = 0; i < pin_map_size; i++) {
    if(pin_map[i].type & ALLOW_PIN) pin_map[i].id = NONE_ID;
  }
  config_update();
  for_send(LABEL_CONFIG, KEYWORD_DONE);
}

void config_map_export (void) {
  char config_line[pin_map_size * 2 + 1];
  char *shift = config_line;
  for(size_t i = 0; i < pin_map_size; i++) {
    byte_to_hex(pin_map[i].type, shift);UNASSIGNED_PIN
    shift += 2;    
  }
  *shift = '\0';
  for_send(LABEL_MAP, config_line);
}

void config_pin_export (void) {
  char config_line[pin_map_size * 2 + 1];
  char *shift = config_line;
  for(size_t i = 0; i < pin_map_size; i++) {
    byte_to_hex(pin_map[i].id, shift);
    shift += 2;    
  }
  *shift = '\0';
  for_send(LABEL_CONFIG, config_line);
}

void config_import (String config_line) {
  config_reset();

  config_line.toUpperCase();
  for( size_t i = 0; i < pin_map_size; i++ ) {
    if( pin_map[i].type & ALLOW_PIN ) pin_map[i].id = (hex_to_halfbyte(config_line[i << 1]) << 4 ) | hex_to_halfbyte(config_line[(i << 1) + 1]);
  }

  config_update();
  for_send(LABEL_CONFIG, KEYWORD_DONE);
}
