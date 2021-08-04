#!/bin/bash

# Envia um codigo para todos os dispositivos ativos na rede
# $1 representa o nome do projeto (escolhido pelo usuario)
AtivosPath="/home/${USER}/OTA-Multiplos-Dispositivos/Relatorios/ativos.json"
EnviaCodPath="/home/${USER}/OTA-Multiplos-Dispositivos/Scripts/enviaCodigo.sh"

numAtivos="$(jq length $AtivosPath)" # Obtem o numero de dispositivos ativos

# Loop entre cada dispositivo i ativo
echo $numAtivos

for ((counter=0; counter < numAtivos; counter++));
do
    bash $EnviaCodPath $counter $1 # Envia o codigo para o dispositivo i
done