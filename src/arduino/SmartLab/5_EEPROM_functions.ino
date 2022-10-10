void settings_load (String v = "") {
  while (settings.count()) settings.remove(settings(0));
  
  char saved_settings[settings_max_length];
  char readed_char = EEPROM.read(settings_EEPROM_address);
  if (readed_char == '{') {
    int index = 0;
    do {
      saved_settings[index++] = readed_char;
      readed_char = EEPROM.read(settings_EEPROM_address + index);
    } while(readed_char != '}' && index < settings_max_length);
    saved_settings[index] = '}';
    
    settings.jload(String(saved_settings));
    
    settings_reset(false);
  } else {
    settings_reset(true);  
  }
}

void settings_save (String v = "") {
  String currnet_settings = settings.json();

  int index = 0;
  do { EEPROM.write(index, currnet_settings[index]);
  } while (currnet_settings[index] != '}' && index++ < settings_max_length);

  for_send(SAVE_LABEL, KEYWORD_DONE);
}

void setting_update (String param, String param_value) {
  settings(param, param_value);
}

String setting_get (String param) {
  return settings[param];
}

void settings_reset (bool hard_reset) {
  for (size_t i = 0; i < settings_params_count; i++) {
      String param = settings_params[i][0];
      String default_value = settings_params[i][1];
      if (setting_get(param) == "" || hard_reset) setting_update(param, default_value);
  }
}

void EEPROM_reset (void) {
  for(size_t i = 0; i < 1024; i++) EEPROM.write(i, 0);
}
