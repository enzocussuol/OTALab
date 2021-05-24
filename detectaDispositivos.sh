#!/bin/bash

# Gera um arquivo dispositivos.txt com todos os dispositivos conectados na rede

rm ~/ProjetoOTA/Relatorios/dispositivos.txt # Remove, se existir, o arquivo de dispositivos antigo

while read ip
do
    # Da um nslookup em cada ip, se responder, joga o nome e o ip no arquivo de dispositivos
    nslookup "$ip" | awk -v ip="$ip" '/name/{print substr($NF,1,length($NF)-1),ip}' >> ~/ProjetoOTA/Relatorios/dispositivos.txt
done < ~/ProjetoOTA/Relatorios/ips.txt # Le cada linha do arquivo de ips