#!/bin/bash
# Gera os arquivos de dispositivos ativos e inativos na rede

MyPath=$(pwd)
AtivosPath="$MyPath/Relatorios/ativos.json"
InativosPath="$MyPath/Relatorios/inativos.txt"
DispPath="$MyPath/Relatorios/dispositivos.txt"

rm "$MyPath/Relatorios/ativos.json"
rm "$MyPath/Relatorios/inativos.txt"

echo "[" >> "$AtivosPath"

vetorAtivos=()

i=0 # Contador que ira funcionar como indice de cada dispositivo ativo
while read dispositivo
do
    ping -c 1 $dispositivo # Pinga no dispositivo para ver se ele esta ativo ou inativo
    if [ $? -eq 0 ] # Se foi retornado 0, o dispositivo esta ativo
    then
        curl $dispositivo > aux.json # Pega o conteudo json que esta no webserver do dispositivo, ja que ele esta ativo
        if [ $? -eq 0 ] # Se o conteudo foi obtido com sucesso, adiciona o dispositivo ao arquivo de ativos
        then
            vetorAtivos+=("$(echo "$(jq ".id += $i" aux.json)")")
            let "i++" # Incrementa o contador, ja que foi adicionado um dispositivo ativo
        fi
    else # Se foi retornado diferente de 0, o dispositivo esta inativo
        echo "$dispositivo" >> "$InativosPath" # Insere o dispositivo no arquivo de inativos no formato txt
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