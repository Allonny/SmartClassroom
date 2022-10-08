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
        
        for_send("uid", uid_str);
      }
    } else {
      card_holding = false;
      RFID_timout_time = 0;
    }
    
    digitalWrite(13, card_holding);
}

void echo_response (String value) {
  for_send("echo", value);
}

void error_response (String value) {
  RFID_error = true;
  for_send("error", value);
}
