#!/bin/bash

echo "Running initialization script..."

apt-get update
apt install python3

curl -fsSL https://raw.githubusercontent.com/arduino/arduino-cli/master/install.sh | sh

arduino-cli config init

sed -i '2s/.*/  additional_urls:\n    - https:\/\/arduino.esp8266.com\/stable\/package_esp8266com_index.json\n    - https:\/\/dl.espressif.com\/dl\/package_esp32_index.json/g' /root/.arduino15/arduino-cli.yaml
sed -i 's/enable_unsafe_install: false/enable_unsafe_install: true/g' /root/.arduino15/arduino-cli.yaml

arduino-cli core update-index
arduino-cli core install esp8266:esp8266
arduino-cli core install esp32:esp32

apt install zip

zip -r OTALabDevice.zip OTALabDevice

arduino-cli lib install PubSubClient
arduino-cli lib install --zip-path OTALabDevice.zip