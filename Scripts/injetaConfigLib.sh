#!/bin/bash

arquivoAAlterar="$HOME/Arduino/libraries/OTALabDevice/src/OTALabDevice.h"

sed -i "s/WIFI_NETWORK_NAME \"\"/WIFI_NETWORK_NAME \"$1\"/" $arquivoAAlterar
sed -i "s/WIFI_NETWORK_PASSWORD \"\"/WIFI_NETWORK_PASSWORD \"$2\"/" $arquivoAAlterar
sed -i "s/BROKER_IP \"\"/BROKER_IP \"$3\"/" $arquivoAAlterar
sed -i "s/DEVICE_ID \"\"/DEVICE_ID $4/" $arquivoAAlterar
