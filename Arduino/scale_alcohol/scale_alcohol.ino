#include "HX711.h"

HX711 scale;

void setup() {
  Serial.begin(38400);
  
  scale.begin(7, 6);
  
  scale.set_scale(2280.f/6.505);                      // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();				        // reset the scale to 0

  analogReadResolution(12);
}

double scaleReading = 0;

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
    
    scaleReading = scale.get_units(3);
    Serial.println(String(scaleReading)+":"+String(ratio));
}
