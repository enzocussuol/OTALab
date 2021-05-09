#!/bin/bash

# Gera um arquivo com todos os possiveis ips da rede

for counter in $(seq 2 254);
do
    echo "192.168.86.$counter" >>ips.txt;
done