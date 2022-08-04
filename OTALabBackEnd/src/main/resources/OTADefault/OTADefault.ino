#include <OTALabDevice.h>

String id = "10";
OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup(id);
}

void loop(){
    device->handle();
}
