void for_send (String param, String param_value) {
  send_params(param, param_value);
}

void send_data (void) {
  if ( send_params.count() ) {
    String send_line = send_params.json() + ENDLINE;
    Serial.println(send_line);
    send_params.destroy();
  }
}

void get_data (void) {
  if (serial_buffer_length) {
    int index = String(serial_buffer).substring(0, serial_buffer_length).indexOf(ENDLINE);
    if (index != -1) {
      String JSONstring = String(serial_buffer).substring(0, index);
      serial_buffer_length = 0;     
      receive_params.jload(JSONstring);
      parser_data();
    }
  }
}

void parser_data (void) {
  for ( size_t i = 0; i < parser_pairs.pair_count; i++ ) {
    if ( receive_params(parser_pairs.labels[i]) ) {
      (parser_pairs.functions)[i](receive_params[parser_pairs.labels[i]]);
    }
  }
  receive_params.destroy();
}

void serialEvent() {
  serial_buffer_reset_timer = millis();
  serial_buffer_length += Serial.readBytes(serial_buffer + serial_buffer_length, SERIAL_BUFFER_SIZE - serial_buffer_length);
}