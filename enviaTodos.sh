#!/bin/bash

# INCOMPLETO: FALTA RESOLVER A SITUACAO DO NOME DA PORTA
# NAO EXECUTAR!

while read linha # Le cada linha do arquivo dispositivos.txt
do
    IFS=' ' read -ra ADDR <<< $linha # Pega cada parte da linha separado por espaco
    for i in "${ADDR[@]}" # Chama enviaCodigo.sh mandando a primeira parte da linha (nome do dispositivo na rede)
    do
        bash enviaCodigo.sh $i $1 $2 # FALTA RESOLVER: $2 COMO NOME DA PORTA!!!
        break
    done
done < "dispositivos.txt"