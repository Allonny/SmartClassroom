label_funcs_pairs parser_pairs;

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

String uid_default = KEYWORD_EMPTY;
String light_default = "0";
String window_default = "0";
String power_supply_default = KEYWORD_FALSE;
setting_const settings_params[] = {
  get_setting(LABEL_UID, uid_default, setting_uid_address),
  get_setting(LABEL_LIGHT, light_default, setting_light_address),
  get_setting(LABEL_WINDOW, window_default, setting_window_address),
  get_setting(LABEL_POWER_SUPPLY, power_supply_default, setting_power_supply_address)};
const size_t settings_params_count = sizeof(settings_params) / sizeof(settings_params[0]);
