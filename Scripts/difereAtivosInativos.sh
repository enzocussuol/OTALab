#!/bin/bash
# Gera os arquivos de dispositivos ativos e inativos na rede

AtivosPath="$HOME/OTALab/Relatorios/ativos.json"
InativosPath="$HOME/OTALab/Relatorios/inativos.txt"
DispPath="$HOME/OTALab/Relatorios/dispositivos.txt"
DispsPath="$HOME/OTALab/Dispositivos/"

rm $AtivosPath
rm $InativosPath

echo "{\"dispositivos\":[" >> "$AtivosPath"

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
            nomeDispositivo=$(jq ".nome" aux.json)
            nomeDispositivo="$(echo $nomeDispositivo | sed 's/"//g')"
            
            cp $DispsPath$nomeDispositivo.json aux.json
            
            echo "$(jq ". += {\"id\":$i}" aux.json)" > aux.json
            echo "$(jq ". += {\"ip\":\"$dispositivo\"}" aux.json)" > aux.json

            vetorAtivos+=("$(cat aux.json)")
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

echo "]}" >> "$AtivosPath"

rm aux.json # Remove o arquivo auxiliar