#!/bin/bash

# Gera um arquivo ips.txt com todos os ips da rede

rm ~/ProjetoOTA/Relatorios/ips.txt # Remove, se existir, o arquivo de ips antigo

for counter in $(seq 2 254);
do
    echo "192.168.86.$counter" >> ~/ProjetoOTA/Relatorios/ips.txt # Simplesmente printa em ips.txt todos os ips da rede
done