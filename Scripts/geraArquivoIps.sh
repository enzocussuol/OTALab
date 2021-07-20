#!/bin/bash

# Gera um arquivo ips.txt com todos os ips da rede
# $1 representa a rede do usuário

rm ~/ProjetoOTA/Relatorios/ips.txt # Remove, se existir, o arquivo de ips antigo

for counter in $(seq 2 254);
do
    echo "$1.$counter" >> ~/ProjetoOTA/Relatorios/ips.txt # Simplesmente printa em ips.txt todos os ips da rede
done