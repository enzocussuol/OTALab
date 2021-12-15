#!/bin/bash

sed -i "s/WIFI_NETWORK_NAME/\"$1\"/" $5
sed -i "s/WIFI_NETWORK_PASSWORD/\"$2\"/" $5
sed -i "s/BROKER_IP/\"$3\"/" $5
sed -i "s/DEVICE_NAME/\"$4\"/" $5