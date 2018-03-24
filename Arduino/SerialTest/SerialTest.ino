void setup() {
  Serial.begin(38400);

}

int i = 0;
void loop() { 
  Serial.println(i++ + 0.1);
  delay(1000);
}
