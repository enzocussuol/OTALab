#!/bin/bash

arquivoAAlterar="$HOME/Arduino/libraries/OTALabDevice/src/OTALabDevice.h"

sed -i "s/WIFI_NETWORK_NAME .*/WIFI_NETWORK_NAME \"\"/" $arquivoAAlterar
sed -i "s/WIFI_NETWORK_PASSWORD .*/WIFI_NETWORK_PASSWORD \"\"/" $arquivoAAlterar
sed -i "s/BROKER_IP .*/BROKER_IP \"\"/" $arquivoAAlterar
sed -i "s/DEVICE_ID .*/DEVICE_ID \"\"/" $arquivoAAlterar
