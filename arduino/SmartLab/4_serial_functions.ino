void for_send (String param, String param_value) {
  send_params(param, param_value);
}

void send_data (void) {
  if ( send_params.count() ) {
    String send_line = send_params.json() + KEYWORD_ENDLINE;
    Serial.println(send_line);
    send_params.destroy();
  }
}

void get_data (void) {
  if ( Serial.available() ) {
    String JSONstring = Serial.readString();
    receive_params.jload(JSONstring);
    parser_data();
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
