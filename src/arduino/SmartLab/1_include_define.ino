#include <Adafruit_PN532.h>
#include <Dictionary.h>
#include <DictionaryDeclarations.h>
#include <EEPROM.h>
#include <SPI.h>
#include <Wire.h>

#define SERIAL_SPEED 9600
#define PN532_IRQ 9
#define settings_EEPROM_address 0
#define settings_max_length 1024

#define PAIRS 9
#define ECHO_LABEL "echo"
#define ERROR_LABEL "error"
#define RESET_LABEL "reset"
#define SAVE_LABEL "save"
#define LOAD_LABEL "load"
#define STARTUP_LABEL "startup"
#define UID_LABEL "uid"
#define LIGHT_LABEL "light"
#define WINDOW_LABEL "window"
#define POWER_SUPPLY_LABEL "power_supply"
