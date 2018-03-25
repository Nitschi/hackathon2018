//#define heaterSelPin 15

void setup() {
    Serial.begin(9600);
    //pinMode(heaterSelPin,OUTPUT);   // set the heaterSelPin as digital output.
    //digitalWrite(heaterSelPin,LOW); // Start to heat the sensor
    analogReadResolution(12);
}

void loop() {
    float vcc = 3.3;
    float sensor_volt;
    float RS_air; //  Get the value of RS via in a clear air
    float sensorValue;

/*--- Get a average data by testing 100 times ---*/
    for(int x = 0 ; x < 100 ; x++)
    {
        sensorValue = sensorValue + analogRead(A0);
    }
    sensorValue = sensorValue/100.0;
/*-----------------------------------------------*/

    sensor_volt = sensorValue/4096*vcc;
    RS_air = sensor_volt/(vcc-sensor_volt); // omit *R16
    Serial.print("sensor_volt = ");
    Serial.print(sensor_volt);
    Serial.println("V");
    Serial.print("RS_air = ");
    Serial.println(RS_air);
    delay(1000);
}
