#include <Adafruit_PN532.h>
#include <Dictionary.h>
#include <DictionaryDeclarations.h>
#include <EEPROM.h>
#include <SPI.h>
#include <Wire.h>

#define _DICT_ASCII_ONLY
#define SERIAL_SPEED 19200
#define PN532_IRQ 9
#define RESET_PIN 12
#define HOLD_LED 13
#define setting_max_length 63
#define none_address -1
#define setting_uid_address -1
#define setting_light_address 64
#define setting_window_address 128
#define setting_power_supply_address 192

#define PAIRS 8
#define LABEL_SYSTEM "system"
#define LABEL_RESET "reset"
#define LABEL_ECHO "echo"
#define LABEL_ERROR "error"
#define LABEL_UID "uid"
#define LABEL_LIGHT "light"
#define LABEL_WINDOW "window"
#define LABEL_POWER_SUPPLY "power_supply"

#define LABEL_STARTUP "startup"
#define LABEL_SAVE "save"
#define LABEL_LOAD "load"

#define KEYWORD_GET "GET"
#define KEYWORD_RESET "RESET"
#define KEYWORD_SETTINGS "SETTINGS"
#define KEYWORD_EEPROM "EEPROM"
#define KEYWORD_REBOOT "REBOOT"
#define KEYWORD_SAVE "SAVE"
#define KEYWORD_LOAD "LOAD"
#define KEYWORD_DONE "DONE"
#define KEYWORD_TRUE "TRUE"
#define KEYWORD_FALSE "FALSE"
#define KEYWORD_EMPTY "EMPTY"
#define KEYWORD_ENDLINE "ENDLINE"
#define KEYWORD_SCANNER_NOT_FOUND "SCANNER_NOT_FOUND"
#define KEYWORD_INVALID_INPUT "INVALID_INPUT"
