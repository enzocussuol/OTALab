#!/bin/bash

IP=$1
placa=$2

echo $IP
echo $placa

mkdir $3 # Cria pasta para o projeto
mkdir $3/build # Cria pasta dentro do projeto que tem os arquivos de compilacao

cp uploads/$3.ino $3/ # Move o arquivo para dentro da pasta do projeto

echo "Compilando..."

arduino-cli compile --fqbn $placa $3/ --build-path $PWD/$3/build # Compila o projeto

mv $3/build/$3.ino.bin $3/ # Move o .bin da pasta de build para a pasta do projeto

echo "Enviando..."

python scripts/espota.py -d -i $IP -f $3/$3.ino.bin # Carrega o codigo para o microcontrolador via OTA

rm uploads/$3.ino
rm -r $3