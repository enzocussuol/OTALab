#!/bin/bash

# Envia um codigo para todos os dispositivos ativos na rede
# $1 representa o nome do projeto (escolhido pelo usuario)

numAtivos="$(jq length ~/ProjetoOTA/Relatorios/ativos.json)" # Obtem o numero de dispositivos ativos

# Loop entre cada dispositivo i ativo
echo $numAtivos

for ((counter=0; counter < numAtivos; counter++));
do
    bash ~/ProjetoOTA/Scripts/enviaCodigo.sh $counter $1 # Envia o codigo para o dispositivo i
done