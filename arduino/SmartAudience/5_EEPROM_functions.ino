void settings_load (void) {
  settings.destroy();

  for (size_t i = 0; i < settings_params_count; i++) {
    if(settings_params[i].address == none_address) continue;
    
    char param_value[setting_max_length + 1];
    param_value[setting_max_length] = 0;
    for (size_t j = 0; j < setting_max_length; j++) {
      param_value[j] = EEPROM.read(settings_params[i].address + j);
    }
    settings(settings_params[i].label, String(param_value));
  }

  settings_reset(false);
  for_send(LABEL_LOAD, KEYWORD_DONE);
}

void settings_save (void) {
  for (size_t i = 0; i < settings_params_count; i++) {
    if(settings_params[i].address == none_address) continue;
    
    char param_value[setting_max_length];
    settings[settings_params[i].label].toCharArray(param_value, setting_max_length);
    for (size_t j = 0; j < setting_max_length; j++) {
      EEPROM.update(settings_params[i].address + j, param_value[j]);
    }
  }
  for_send(LABEL_SAVE, KEYWORD_DONE);
}

void setting_update (String param, String param_value) {
  settings(param, param_value);
}

String setting_get (String param) {
  return settings[param];
}

void settings_reset (bool hard_reset) {
  for (size_t i = 0; i < settings_params_count; i++) {
      String param = settings_params[i].label;
      String default_value = settings_params[i].defval;
      if (setting_get(param) == "" || hard_reset) setting_update(param, default_value);
  }
}

void EEPROM_reset (void) {
  for(size_t i = 0; i < 1024; i++) EEPROM.write(i, 0);
}
