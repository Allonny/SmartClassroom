struct label_funcs_pairs;
struct setting_const;

// Основные функции
void setup (void);
void loop (void);

// Функции работы с Serail
void for_send (String param, String value);
void send_data (void);
void get_data (void);
void parser_data (void);

// Функции работы с EEPROM
void settings_load (void);
void settings_save (void);
void setting_update(String param, String param_value);
String setting_get(String param);
void settings_reset(bool hard_reset);
void EEPROM_reset(void);

// Остальные функции
struct setting_const get_setting(String label, String defval, int address);
void RFID_read (void);
void echo_response (String param_value);
void error_response (String param_value);
void reset_request (String param_value);
void system_request (String param_value);
void uid_request (String param_value);
void light_request (String param_value);
void window_request (String param_value);
void power_supply_request (String param_value);
void numeric_param_setup (String param, String param_value, String param_default);

// Структуры
typedef void (*func_type)(String);

typedef struct label_funcs_pairs {
  const size_t pair_count = PAIRS;
  
  String labels[PAIRS] = {
    LABEL_SYSTEM,
    LABEL_RESET,
    LABEL_ECHO,
    LABEL_ERROR,
    LABEL_UID,
    LABEL_LIGHT,
    LABEL_WINDOW,
    LABEL_POWER_SUPPLY};
    
  func_type functions[PAIRS] = {
    &system_request,
    &reset_request,
    &echo_response,
    &error_response,
    &uid_request,
    &light_request,
    &window_request,
    &power_supply_request};
} label_funcs_pairs;

typedef struct setting_const {
  String label;
  String defval;
  int address;
} setting_const;
