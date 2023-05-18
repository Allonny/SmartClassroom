String group_export(struct param_group *group) {
  char param_line[group->count * 2 + 1];
  char *shift = param_line;
  for(size_t i = 0; i < group->count; i++) {
    byte_to_hex(group->pins[i].state, shift);
    shift += 2;
  }
  *shift = '\0';
  for_send(group->label, param_line);
}

void group_set(struct param_group *group, String value) {
  uint8_t index = (hex_to_halfbyte(value[0]) << 4) | hex_to_halfbyte(value[1]);
  uint8_t state = (hex_to_halfbyte(value[2]) << 4) | hex_to_halfbyte(value[3]);

  if(index >= group->count) { error_response(KEYWORD_UNASSIGNED_PIN); return; }
  group->pins[index].state = state;

  if(group->label == LABEL_LIGHT) light_control();
  if(group->label == LABEL_WINDOW) window_control();
  if(group->label == LABEL_POWER_SUPPLY) power_control();

  for_send(group->label, KEYWORD_DONE);
}

void group_reset(struct param_group *group) {
  for(size_t i = 0; i < group->count; i++) group->pins[i].state = 0;
  for_send(group->label, KEYWORD_DONE);
}

uint8_t group_append(struct param_group *group, uint8_t pin) {
  if( group->count >= GROUP_MAX_COUNT ) return 0;
  group->pins[group->count] = init_pin_state(pin, 0);
  return group->count++;
}

void light_control() {
  for(size_t i = 0; i < light_group.count; i++) {
    uint8_t pin = light_group.pins[i].pin;
    uint8_t state = light_group.pins[i].state;
    if( pin_map[pin].type & PWM_PIN ) analogWrite(pin, crt8_square(state));
    else digitalWrite(pin, state ? HIGH : LOW);
  }
}

void window_control() {
  Servo servo;
  for(size_t i = 0; i < window_group.count; i++) {
    uint8_t pin = window_group.pins[i].pin;
    uint8_t state = window_group.pins[i].state;
    uint8_t angle = map(state, 0, 255, 1, 179);
    servo.attach(pin);
    servo.write(angle);
    delay(1000);
    servo.detach();
  }
}

void power_control() {
  for(size_t i = 0; i < power_group.count; i++) {
    uint8_t pin = power_group.pins[i].pin;
    uint8_t state = power_group.pins[i].state;
    digitalWrite(pin, state ? HIGH : LOW);
  }
}