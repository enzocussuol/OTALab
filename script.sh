#!/bin/bash

IPSENSOR="$(dig +short $1.lan)" # Obtem ip do sensor

echo $IPSENSOR

mkdir $2 # Cria pasta para o projeto
mkdir $2/build # Cria pasta dentro do projeto que tem os arquivos de compilacao

cp $2.ino $2/ # Move o arquivo para dentro da pasta do projeto

echo "Compilando..."

arduino-cli compile --fqbn $3 $2/ --build-path $PWD/$2/build # Compila o projeto

mv $2/build/$2.ino.bin $2/ # Move o .bin da pasta de build para a pasta do projeto

echo "Enviando..."

python espota.py -d -i $IPSENSOR -f $2/$2.ino.bin # Carrega o codigo para o microcontrolador via OTA