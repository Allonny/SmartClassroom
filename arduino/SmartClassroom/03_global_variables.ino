label_funcs_pairs parser_pairs;

Adafruit_PN532 *nfc;
uint8_t nfc_pin;
uint8_t reset_pin;
uint8_t hold_led_pin;

bool RFID_error = false;
bool card_holding = false;
unsigned long RFID_polling_time = 100;
unsigned long RFID_polling_timer = 0;
unsigned long RFID_timout_time = 0;
unsigned long timeout = 2000;

unsigned long serial_polling_time = 10;
unsigned long serial_polling_timer = 0;

unsigned long serial_buffer_reset_time = 1000;
unsigned long serial_buffer_reset_timer = 0;
char serial_buffer[SERIAL_BUFFER_SIZE];
size_t serial_buffer_length = 0;

Dictionary &receive_params = *(new Dictionary());
Dictionary &send_params = *(new Dictionary());

pin_conf pin_map[] = {
  init_pin_conf(            UART_PIN , NONE_ID | 0 ),       // pin 0 - RX
  init_pin_conf(            UART_PIN , NONE_ID | 1 ),       // pin 1 - TX
  init_pin_conf( ALLOW_PIN           , NONE_ID     ),       // pin 2
  init_pin_conf( ALLOW_PIN | PWM_PIN , NONE_ID     ),       // pin 3
  init_pin_conf( ALLOW_PIN           , NONE_ID     ),       // pin 4
  init_pin_conf( ALLOW_PIN | PWM_PIN , NONE_ID     ),       // pin 5
  init_pin_conf( ALLOW_PIN | PWM_PIN , NONE_ID     ),       // pin 6
  init_pin_conf( ALLOW_PIN           , NONE_ID     ),       // pin 7
  init_pin_conf( ALLOW_PIN           , NONE_ID     ),       // pin 8
  init_pin_conf( ALLOW_PIN | PWM_PIN , NONE_ID     ),       // pin 9
  init_pin_conf( ALLOW_PIN | PWM_PIN , NONE_ID     ),       // pin 10
  init_pin_conf(             PWM_PIN , NFC_ID      ),       // pin 11
  init_pin_conf( 0                   , RESET_ID    ),       // pin 12
  init_pin_conf( 0                   , HOLD_LED_ID ),       // pin 13
  init_pin_conf( ALLOW_PIN | ADC_PIN , NONE_ID     ),       // pin 14
  init_pin_conf( ALLOW_PIN | ADC_PIN , NONE_ID     ),       // pin 15
  init_pin_conf( ALLOW_PIN | ADC_PIN , NONE_ID     ),       // pin 16
  init_pin_conf( ALLOW_PIN | ADC_PIN , NONE_ID     ),       // pin 17
  init_pin_conf(             I2C_PIN , NONE_ID | 0 ),       // pin 18 - SDA
  init_pin_conf(             I2C_PIN , NONE_ID | 1 ),       // pin 19 - SCL
};

const size_t pin_map_size = sizeof(pin_map) / sizeof(*pin_map);

param_group light_group = { .label = LABEL_LIGHT };
param_group window_group = { .label = LABEL_WINDOW };
param_group power_group = { .label = LABEL_POWER_SUPPLY };
