// Основные функции
void setup (void);
void loop (void);

// Функции работы с Serail
void for_send (String param, String value);
void send_data (void);
void get_data (void);
void parser_data (void);

// Функции работы с EEPROM
void settings_load (String v);
void settings_save (String v);
void setting_update(String param, String param_value);
String setting_get(String param);
void settings_reset(bool hard_reset);
void EEPROM_reset(void);

// Остальные функции
void RFID_read (void);
void echo_response (String value);
void error_response (String value);
void reset_request (String value);
void uid_request (String param_value);
void light_request (String value);
void window_request (String value);
void power_supply_request (String value);
void numeric_param_setup (String param, String param_value, String param_default);
