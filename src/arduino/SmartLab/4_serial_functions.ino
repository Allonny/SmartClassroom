void for_send (String param, String param_value) {
  send_params(param, param_value);
}

void send_data (void) {
  if ( send_params.count() ) {
    Serial.println(send_params.json());
//    send_params.destroy();
    while (send_params.count()) send_params.remove(send_params(0));
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
  while (receive_params.count()) receive_params.remove(receive_params(0));
  //receive_params.destroy();
}
