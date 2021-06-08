#!/bin/bash

# Envia um codigo para o dispositivo que possuir o indice tal qual foi passado como parametro
# $1 representa o indice do dispositivo no arquivo ativos.json
# $2 representa o nome do projeto (escolhido pelo usuario)

# Obtem o ip e a placa do dispositivo a partir do seu indice
IP="$(echo $(jq "select(.id == $1) | .ip" ~/ProjetoOTA/Relatorios/ativos.json))"
placa="$(echo $(jq "select(.id == $1) | .placa" ~/ProjetoOTA/Relatorios/ativos.json))"

# Remove as aspas das strings ip e placa
IP="$(echo $IP | sed 's/"//g')"
placa="$(echo $placa | sed 's/"//g')"

echo $IP
echo $placa

mkdir $2 # Cria pasta para o projeto
mkdir $2/build # Cria pasta dentro do projeto que tem os arquivos de compilacao

# Copia as bibliotecas necessarias para atualizacao via OTA para dentro da pasta do projeto
cp DispositivoOTAWS.cpp $2/
cp DispositivoOTAWS.h $2/

cp Sensor.cpp $2/
cp Sensor.h $2/

cp $2.ino $2/ # Move o arquivo para dentro da pasta do projeto

echo "Compilando..."

arduino-cli compile --fqbn $placa $2/ --build-path $PWD/$2/build # Compila o projeto

mv $2/build/$2.ino.bin $2/ # Move o .bin da pasta de build para a pasta do projeto

echo "Enviando..."

python espota.py -d -i $IP -f $2/$2.ino.bin # Carrega o codigo para o microcontrolador via OTA