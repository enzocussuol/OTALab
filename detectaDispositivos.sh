#!/bin/bash

while read ip # Pega cada linha do arquivo ips.txt
do
    nslookup "$ip" | awk -v ip="$ip" '/name/{print substr($NF,1,length($NF)-1),ip}' >>dispositivos.txt # Imprime todos os dispositivos da rede em dispositivos.txt
done < "ips.txt"