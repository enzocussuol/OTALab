#include <OTALabDevice.h>

String id = "";
OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup(id);
}

void loop(){
    device->handle();
}