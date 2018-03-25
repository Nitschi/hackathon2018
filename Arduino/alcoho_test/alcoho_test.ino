//#define heaterSelPin 15

void setup() {
    Serial.begin(9600);
    //pinMode(heaterSelPin,OUTPUT);   // set the heaterSelPin as digital output.
    //digitalWrite(heaterSelPin,LOW); // Start to heat the sensor
    analogReadResolution(12);

}

void loop() {
    float vcc = 3.3;
    float RS_air = 2.42;
    float sensor_volt;
    float RS_gas; // Get value of RS in a GAS
    float ratio; // Get ratio RS_GAS/RS_air
    int sensorValue = analogRead(A0);
    sensor_volt=(float)sensorValue/4096*vcc;
    RS_gas = sensor_volt/(vcc-sensor_volt); // omit *R16

  /*-Replace the name "R0" with the value of R0 in the demo of First Test -*/
    ratio = RS_gas/RS_air;  // ratio = RS/R0
  /*-----------------------------------------------------------------------*/

    Serial.print("sensor_volt = ");
    Serial.println(sensor_volt);
    Serial.print("RS_ratio = ");
    Serial.println(RS_gas);
    Serial.print("Rs/R0 = ");
    Serial.println(ratio);

    Serial.print("\n\n");
    delay(1000);
}
