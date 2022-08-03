#!/bin/bash

arquivoAAlterar="$HOME/Arduino/libraries/OTALabDevice/src/Config.h"

sed -i "s/.*WIFI_NETWORK_NAME.*/#define WIFI_NETWORK_NAME \"$1\"/" $arquivoAAlterar
sed -i "s/.*WIFI_NETWORK_PASSWORD.*/#define WIFI_NETWORK_PASSWORD \"$2\"/" $arquivoAAlterar
sed -i "s/.*BROKER_IP.*/#define BROKER_IP \"$3\"/" $arquivoAAlterar
