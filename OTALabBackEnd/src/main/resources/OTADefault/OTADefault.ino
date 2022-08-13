#include <OTALabDevice.h>

String id = "1";
OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup(id);
}

void loop(){
    device->handle();
}