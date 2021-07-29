#!/bin/bash

# Gera os arquivos de dispositivos ativos e inativos na rede (considerando somente dispositivos esps) (MOMENTANEO!)

# Remove, se existirem, os arquivos de dispositivos ativos e inativos
MyPath=$(pwd)
AtivosPath="$MyPath/Relatorios/ativos.json"
InativosPath="$MyPath/Relatorios/inativos.txt"
DispPath="$MyPath/Relatorios/dispositivos.txt"

rm AtivosPath
rm InativosPath

echo "[" >> "$AtivosPath"

vetorAtivos=()

i=0 # Contador que ira funcionar como indice de cada dispositivo ativo
while read linha
do
    IFS=' ' read -ra ADDR <<< $linha # Pega cada parte da linha separando por espaco
    if [[ ${ADDR[0]} == *"esp"* ||  ${ADDR[0]} == *"ESP"* ]]; then # Se o nome do dispositivos contiver a substring "esp", o dispositivo sera valido (MOMENTANEO!)
        ping -c 1 ${ADDR[1]} # Pinga no esp para ver se ele esta ativo ou inativo
        if [ $? -eq 0 ] # Se foi retornado 0, o esp esta ativo
        then
            curl ${ADDR[1]} > aux.json # Pega o conteudo json que esta no webserver do esp, ja que ele esta ativo
            if [ $? -eq 0 ] # Se o conteudo foi obtido com sucesso, adiciona o esp ao arquivo de ativos
            then
                vetorAtivos+=("$(echo "$(jq ".id += $i" aux.json)")")
                let "i++" # Incrementa o contador, ja que foi adicionado um dispositivo ativo
            fi
        else # Se foi retornado diferente de 0, o esp esta inativo
            echo "${ADDR[0]} ${ADDR[1]}" >> "$InativosPath" # Insere o esp no arquivo de inativos no formato txt
        fi
    fi
done < "$DispPath" # Le cada linha do arquivo de dispositivos

tamVetorAtivos=${#vetorAtivos[@]}
numVirgulas=tamVetorAtivos-1

for ((counter=0; counter < tamVetorAtivos; counter++));
do
    echo ${vetorAtivos[$counter]} >> "$AtivosPath"
    if [[ $counter -lt numVirgulas ]]; then
        echo "," >> "$AtivosPath"
    fi
done

echo "]" >> "$AtivosPath"

rm aux.json # Remove o arquivo auxiliar