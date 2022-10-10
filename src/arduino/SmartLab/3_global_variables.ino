typedef void (*func_type)(String);

typedef struct {
  const size_t pair_count = PAIRS;
  
  String labels[PAIRS] = {
    ECHO_LABEL,
    ERROR_LABEL,
    RESET_LABEL,
    SAVE_LABEL,
    LOAD_LABEL,
    UID_LABEL,
    LIGHT_LABEL,
    WINDOW_LABEL,
    POWER_SUPPLY_LABEL};
    
  func_type functions[PAIRS] = {
    &echo_response,
    &error_response,
    &reset_request,
    &settings_save,
    &settings_load,
    &uid_request,
    &light_request,
    &window_request,
    &power_supply_request};
} label_funcs_pairs;

Adafruit_PN532 nfc(PN532_IRQ, 100);

bool RFID_error = false;
bool card_holding = false;
unsigned long RFID_polling_time = 100;
unsigned long RFID_polling_timer = 0;
unsigned long RFID_timout_time = 0;
unsigned long timeout = 2000;

unsigned long serial_polling_time = 100;
unsigned long serial_polling_timer = 0;

Dictionary &receive_params = *(new Dictionary());
Dictionary &send_params = *(new Dictionary());
Dictionary &settings = *(new Dictionary());

label_funcs_pairs parser_pairs;

const String KEYWORD_GET = "GET";
const String KEYWORD_RESET = "RESET";
const String KEYWORD_SETTINGS = "SETTINGS";
const String KEYWORD_EEPROM = "EEPROM";
const String KEYWORD_DONE = "DONE";
const String KEYWORD_TRUE = "TRUE";
const String KEYWORD_FALSE = "FALSE";
const String KEYWORD_EMPTY = "EMPTY";
const String KEYWORD_SCANNER_NOT_FOUND = "SCANNER_NOT_FOUND";
const String KEYWORD_INVALID_INPUT = "INVALID_INPUT";

String uid_default = KEYWORD_EMPTY;
String light_default = "0";
String window_default = "0";
String power_supply_default = KEYWORD_FALSE;
const String settings_params[][2] = {
  {UID_LABEL, uid_default},
  {LIGHT_LABEL, light_default},
  {WINDOW_LABEL, window_default},
  {POWER_SUPPLY_LABEL, power_supply_default}};
const size_t settings_params_count = sizeof(settings_params) / sizeof(settings_params[0]);
