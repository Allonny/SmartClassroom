struct label_funcs_pairs;
struct pin_conf;
struct pin_state;
struct param_groups;

// Основные функции
void setup (void);
void loop (void);

// Функции работы с Serail
void for_send (String param, String value);
void send_data (void);
void get_data (void);
void parser_data (void);

// Функции работы с конфигурацией
void config_load (void);
void config_save (void);
void config_update (void);
void config_reset (void);
void config_pin_export (void);
void config_map_export (void);
void config_import (String config_line);

// Функции работы с группами выходов
String group_export(struct param_group param);
void group_set(struct param_group param, String value);
void group_reset(struct param_group param);


// Функции обработки запросов
void system_request (String param_value);
void echo_response (String param_value);
void error_response (String param_value);
void config_request (String param_value);
void light_request (String param_value);
void window_request (String param_value);
void power_supply_request (String param_value);

// Остальные функции
struct pin_conf init_pin_conf(uint8_t type, uint8_t id);
struct pin_state init_pin_state(uint8_t pin, uint8_t value);
void byte_to_hex(uint8_t value, char *line);
uint8_t hex_to_halfbyte(char digit);
bool is_config_line(String value);
void RFID_read (void);
void EEPROM_reset(void);

// Структуры
typedef void (*func_type)(String);

typedef struct label_funcs_pairs {
  const size_t pair_count = PAIRS;
  
  String labels[PAIRS] = {
    LABEL_SYSTEM,
    LABEL_ECHO,
    LABEL_ERROR,
    LABEL_CONFIG,
    LABEL_LIGHT,
    LABEL_WINDOW,
    LABEL_POWER_SUPPLY};
    
  func_type functions[PAIRS] = {
    &system_request,
    &echo_response,
    &error_response,
    &config_request,
    &light_request,
    &window_request,
    &power_supply_request};
} label_funcs_pairs;

typedef struct pin_conf {
  const uint8_t type;
  uint8_t id;
} pin_conf;

typedef struct pin_state {
  uint8_t pin;
  uint8_t state;
} pin_state;

typedef struct param_group {
  String label;
  pin_state pins[GROUP_MAX_COUNT];
  size_t count;  
} param_group;
