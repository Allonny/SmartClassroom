void system_request (String param_value) {
  if (param_value == KEYWORD_REBOOT)  { digitalWrite(reset_pin, LOW); return; }
  if (param_value == KEYWORD_RESET)   { EEPROM_reset(); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void echo_response (String param_value) {
  for_send(LABEL_ECHO, param_value);
}

void error_response (String param_value) {
  RFID_error = true;
  for_send(LABEL_ERROR, param_value);
}

void config_request (String param_value) {
  if (param_value == KEYWORD_GET)   { config_pin_export(); return; }
  if (param_value == KEYWORD_MAP)   { config_map_export(); return; }
  if (param_value == KEYWORD_RESET) { config_reset (); return; }
  if (param_value == KEYWORD_SAVE)  { config_save(); return; }
  if (param_value == KEYWORD_LOAD)  { config_load(); return; }
  if (is_config_line(param_value))  { config_import (param_value); return; };
  error_response(KEYWORD_INVALID_INPUT);
}

void light_request (String param_value) {
  if (param_value == KEYWORD_GET)   { group_export(&light_group); return; }
  if (param_value == KEYWORD_RESET) { group_reset(&light_group); return; }
  if (is_param_line(param_value))   { group_set(&light_group, param_value); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void window_request (String param_value) {
  if (param_value == KEYWORD_GET)   { group_export(&window_group); return; }
  if (param_value == KEYWORD_RESET) { group_reset(&window_group); return; }
  if (is_param_line(param_value))   { group_set(&window_group, param_value); return; }
  error_response(KEYWORD_INVALID_INPUT);
}

void power_supply_request (String param_value) {
  if (param_value == KEYWORD_GET)   { group_export(&power_group); return; }
  if (param_value == KEYWORD_RESET) { group_reset(&power_group); return; }
  if (is_param_line(param_value))   { group_set(&power_group, param_value); return; }
  error_response(KEYWORD_INVALID_INPUT);
}
