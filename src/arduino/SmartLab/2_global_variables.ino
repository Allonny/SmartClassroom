Adafruit_PN532 nfc(PN532_IRQ, 100);

bool RFID_error = false;
bool card_holding = false;
unsigned long RFID_polling_time = 100;
unsigned long RFID_polling_timer = 0;
unsigned long RFID_timout_time = 0;
unsigned long timeout = 2000;

unsigned long serial_polling_time = 20;
unsigned long serial_polling_timer = 0;

Dictionary &receive_params = *(new Dictionary());
Dictionary &send_params = *(new Dictionary());

const String avaible_receive_params[] = {"echo", "test", "test1"};
const size_t avaible_params_count = sizeof(avaible_receive_params) / sizeof(avaible_receive_params[0]);
