# Upload Over the Air de Código Fonte .ino para Múltiplos Dispositivos

## Introdução

Este script foi desenvolvido com o intuito de possibilitar, pela linha de comando, o envio de código com a extensão .ino para um dispositivo que o irá executar. Futuramente, esse script poderá ser usado para gerenciar um conjunto de microprocessadores remotamente, controlando a atividade de cada um. Por se tratar de um script shell, ele deve ser rodado em ambientes Linux.

## Requisitos

### Biblioteca "ArduinoOTA.h"

É necessário que o código em questão inclua em seu corpo a biblioteca "ArduinoOTA.h", uma vez que esta permite a conexão remota do usuário com o dispositivo. Indica-se o uso do próprio código de exemplo encontrado na Arduino IDE como base. É importante que as funções da biblioteca estejam corretamente implementadas, tais como no código de exemplo. Mais informações em [biblioteca ArduinoOTA](https://github.com/jandrassy/ArduinoOTA).

Com a biblioteca presente no código, ao o executar pela primeira vez (por conexão cabeada), ele irá gerar uma porta de rede. Aqui, é considerado o nome padrão dessa porta, que será da forma "nomeDispositivo-[ChipID]". Esse nome pode ser alterado a partir da função ArduinoOTA.setHostname(), mas, é mais prático e mais seguro manter o nome padrão.

Com a porta presente na rede, o script têm caminho livre para continuar, uma vez que basta obter o IP por meio do nome dela para estabelecer a conexão.

### Software arduino-cli

O principal componente que permite o funcionamento do script é o software arduino-cli. Esse software permite ao usuário realizar uma série de atividades com placas compatíveis com o Arduino por meio da linha de comando. Disponível para instalação em [arduino-cli](https://github.com/arduino/arduino-cli).

O arduino-cli, e consequentemente o script, requer que a plataforma do dispositivo em questão esteja instalada. Isso pode ser feito por meio do comando:

`arduino-cli core install arduino:avr`

Para verificar a lista de todas as plataformas aceitas pelo arduino-cli, basta rodar:

`arduino-cli board listall`

Caso o usuário não esteja trabalhando com dispositivos nativos da plataforma Arduino, tais como o ESP32 e o ESP8266, é necessário fazer com que o arduino-cli reconheça esse dispositivo. Para isso, é preciso adicionar os pacotes desses dispositivos no arquivo de configuração do arduino-cli. Caso este arquivo ainda não exista, crie-o com:

`arduino-cli config init`

Abra este arquivo com qualquer editor de texto e procure pela linha "additional_urls". Aqui, adicione a URL do pacote do dispositivo em questão. Por exemplo, se quiséssemos adicionar o dispositivo ESP8266, faríamos:

```
additional_urls:
    [https://arduino.esp8266.com/stable/package_esp8266com_index.json]
```

Agora, basta atualizar o núcleo do arduino-cli com o comando:

`arduino-cli core update-index`

Mais informações sobre o arduino-cli em [documentação arduino-cli](https://arduino.github.io/arduino-cli/latest/getting-started/) e [tutorial arduino-cli](https://create.arduino.cc/projecthub/B45i/getting-started-with-arduino-cli-7652a5).

### Arquivo espota.py

O ultimo componente do script é o arquivo python espota. Esse arquivo está disponível nesse repositório, mas se encontra oficialmente em [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py). Basicamente esse arquivo realiza as conexões da máquina do usuário com o dispositivo remoto por baixo dos panos. Obviamente o usuário deve ter instalado o python.

## Funcionamento

Feito todos os requisitos, basta executar o comando para rodar o script de fato. O script possui 3 parâmetros: 1) O nome da porta de rede do dispositivo; 2) O nome do código fonte sem a extensão .ino; 3) O nome da placa reconhecida pelo arduino-cli para o dispositivo em questão. O comando para execução é:

`sh script.sh <nomePorta> <nomeCodigoFonte> <nomePlaca>`

Por exemplo, se quiséssemos enviar o código fonte OTABlink.ino (código hipotético que faz piscar um LED a cada x segundos) para um esp8266 modelo d1_mini cujo chip possui id 499b6d, faríamos:

`sh script.sh esp8266:499b6d OTABlink esp8266:esp8266:d1_mini`

Detalhe: o arquivo .ino deve estar no mesmo diretório do script. Após a execução, em caso de sucesso, o novo código será armazenado no dispositivo. Além disso, será criada uma pasta com o nome do projeto, onde irá se encontrar uma cópia do arquivo .ino, um arquivo .ino.bin e uma pasta build, com informações sobre a compilação do .ino.

## Considerações

Esse script é um teste para posteriormente implementar um sistema concreto de gerenciamento de múltiplos dispositivos via Internet das Coisas. Ele está sujeito a mudanças e bugs, que serão corrigidos no futuro.
