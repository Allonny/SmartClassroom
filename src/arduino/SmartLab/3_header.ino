// Основные функции
void setup (void);
void loop (void);

// Функции работы с Serail
void for_send (String param, String value);
void send_data (void);
void get_data (void);
void parser_data (void);

// Остальные функции
void RFID_read (void);
void echo_response (String value);
void error_response (String value);
