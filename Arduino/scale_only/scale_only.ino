#include "HX711.h"

HX711 scale;

void setup() {
  Serial.begin(38400);
  
  scale.begin(7, 6);
  
  scale.set_scale(2280.f/6.505);  // this value is obtained by calibrating the scale with known weights; see the README for details
  scale.tare();				            // reset the scale to 0
}

double scaleReading = 0;

void loop() {

    scaleReading = scale.get_units(3);
    Serial.println(String(scaleReading*1000));
}
