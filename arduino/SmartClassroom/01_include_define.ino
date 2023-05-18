#include <Adafruit_PN532.h>
#include <Dictionary.h>
#include <DictionaryDeclarations.h>
#include <EEPROM.h>
#include <SPI.h>
#include <Wire.h>
#include <Servo.h>

#define _DICT_ASCII_ONLY
#define PAIRS 7

#define SERIAL_SPEED 19200
#define SERIAL_BUFFER_SIZE 1024

#define PIN_CONF_ADDRESS 64

#define ALLOW_PIN   0b00000001
#define PWM_PIN     0b00000010
#define ADC_PIN     0b00000100
#define UART_PIN    0b00001000
#define I2C_PIN     0b00010000
#define R0_PIN      0b00100000
#define R1_PIN      0b01000000
#define R2_PIN      0b10000000

#define NONE_ID     0x00
#define LIGHT_ID    0x10
#define WINDOW_ID   0x20
#define POWER_ID    0x30
#define R0_ID       0x40
#define R1_ID       0x50
#define R2_ID       0x60
#define R3_ID       0x70
#define R4_ID       0x80
#define R5_ID       0x90
#define R6_ID       0xA0
#define R7_ID       0xB0
#define R8_ID       0xC0
#define NFC_ID      0xD0
#define RESET_ID    0xE0
#define HOLD_LED_ID 0xF0
#define ID_MASK     0xF0

#define GROUP_MAX_COUNT 16

#define LABEL_SYSTEM                "system"
#define LABEL_ECHO                  "echo"
#define LABEL_ERROR                 "error"
#define LABEL_CONFIG                "config"
#define LABEL_MAP                   "pinmap"
#define LABEL_UID                   "uid"
#define LABEL_LIGHT                 "light"
#define LABEL_WINDOW                "window"
#define LABEL_POWER_SUPPLY          "power_supply"
#define LABEL_STARTUP               "startup"
#define LABEL_SAVE                  "save"
#define LABEL_LOAD                  "load"

#define KEYWORD_GET                 "GET"
#define KEYWORD_RESET               "RESET"
#define KEYWORD_MAP                 "MAP"
#define KEYWORD_EEPROM              "EEPROM"
#define KEYWORD_REBOOT              "REBOOT"
#define KEYWORD_SAVE                "SAVE"
#define KEYWORD_LOAD                "LOAD"
#define KEYWORD_DONE                "DONE"
#define KEYWORD_TRUE                "TRUE"
#define KEYWORD_FALSE               "FALSE"
#define KEYWORD_EMPTY               "EMPTY"
#define KEYWORD_SCANNER_NOT_FOUND   "SCANNER_NOT_FOUND"
#define KEYWORD_INVALID_INPUT       "INVALID_INPUT"
#define KEYWORD_UNASSIGNED_PIN      "UNASSIGNED_PIN"

#define ENDLINE '$'
