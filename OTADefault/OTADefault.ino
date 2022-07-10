#include <OTALabDevice.h>

OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup();
}

void loop(){
    device->handle();
}