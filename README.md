# OTALab - Um ambiente para experimentação e atualização Over-The-Air em dispositivos IoT

## 1. Introdução

Este é um projeto de Iniciação Científica pela UFES na área de Internet das Coisas. O objetivo desse sistema é prover um ambiente de rápido funcionamento e baixo custo para gerenciamento de dispositivos IoT ([veja quais dispositivos são atualmente suportados](https://github.com/enzocussuol/OTANetwork/blob/main/dispositivosSuportados.txt)). A idéia é que um administrador posicione, sob uma mesma rede Wi-Fi, vários dispositivos com vários sensores acoplados e, com o uso do OTALab, seja capaz de gerenciá-los, tanto os monitorando quanto eventualmente atualizando seus códigos-fonte. Note que o uso pode ser extendido para usuários comúns, que não teriam privilégios de administrador, mas que seriam capazes de fazer upload para os dispositivos.

O software é uma mistura de várias linguagens, as quais se destacam: Java e seu framework Spring Boot, para a criação da interface Web; Python, para realização de alguns scripts, entre eles o [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), cuja autoria está no link disponibilizado; Shell, também para realização de alguns scripts essenciais; C++, para desenvolvimento de uma biblioteca Arduino.

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria Arduino que possibilita a gerência de placas Arduino a partir da linha de comando; 2) espota, um script python que realiza o envio de código via Over the Air para ESPs. O arquivo espota.py já está incluso na forma de uma cópia do original nesse repositório.

O sistema deve rodar em um ambiente Linux para que funcione corretamente.

## 2. Instalação

Para iniciar a instalação simplesmente execute dentro do seu diretório /home/$USER:

`git clone https://github.com/enzocussuol/OTALab`

Isto irá copiar todos os arquivos deste repositório remoto para seu repositório local. Depois disso, basta instalar algumas poucas dependências.

## 3. Dependências

Antes de mais nada, para o correto funcionamento do OTALab é necessário que se tenha instalado Java e Python3.

### 3.1. Arduino-cli

É necessário que o arduino-cli seja instalado, ver [instalação arduino-cli](https://arduino.github.io/arduino-cli/latest/installation/). Tendo instalado o software, deve-se realizar alguns passos adicionais. O ESP8266 e o ESP32 não são placas originalmente suportadas pelo arduino-cli, logo, devem ainda ser instalados núcleos para essas placas, visto que elas são de produções de terceiros (recomenda-se ver [instalação placas terceirizadas no arduino-cli](https://create.arduino.cc/projecthub/B45i/getting-started-with-arduino-cli-7652a5)).

Dito isso, devem ser adicionados os pacotes necessários para essas placas funcionarem corretamente. É necessário inserir esses pacotes no arquivo de configuração do arduino-cli, logo, caso este ainda não tenha sido criado, faça:

`arduino-cli config init`

Esse comando irá gerar um arquivo chamado arduino-cli.yaml, que pode ser encontrado na pasta oculta .arduino15. Abra-o em qualquer editor de texto e crie, caso não existir, o campo "additional_urls:", dentro de "board_manager".
Dentro de "additional_urls", irão ser inseridas as URLs para as placas que o usuário deseja. No caso dessa aplicação o arquivo ficaria assim:

```
board_manager:
  additional_urls: 
    - https://arduino.esp8266.com/stable/package_esp8266com_index.json
    - https://dl.espressif.com/dl/package_esp32_index.json
```

Feito isso, basta rodar o comando:

`arduino-cli core update-index`

Agora, instale de fato as placas com os comandos:

`arduino-cli core install esp8266:esp8266`

`arduino-cli core install esp32:esp32`

Para checar as placas instaladas, basta rodar:

`arduino-cli board listall`

O resultado deve ser uma série de placas para o ESP8266 e para o ESP32.

### 3.2. Pyserial

O arduino-cli faz uso de um pacote python chamado Pyserial. Recomenda-se a instalação dele via [pip](https://pypi.org/project/pip/):

`pip install pyserial`

### 3.3. Paho-MQTT

Um dos scripts faz uso do protocolo MQTT em python. Para isso, ele utiliza o pacote paho-mqtt, instale-o com:

`pip install paho-mqtt`

### 3.4. JQ

Outro software utilizado foi o [jq](https://stedolan.github.io/jq/), um processador de json para linha de comando. Ele está presente em alguns scripts e para instalá-lo basta rodar:

`sudo apt-get install jq`

### 3.5. Maven

Para organizar as aplicações Java, foi utilizado o [Maven](https://maven.apache.org/). Para instalá-lo basta seguir o tutorial disponível [aqui](https://maven.apache.org/install.html).

## 4. A Biblioteca OTALabDevice

É essencial que qualquer código enviado para um dispositivo dentro desse sistema implemente a biblioteca OTALabDevice. Ela está disponibilizada neste repositório, basta que seja instalada. Para isso, empacote a pasta OTALabDevice em um arquivo .zip e rode:

`arduino-cli lib install --zip-path OTALabDevice.zip`

A biblioteca OTALabDevice contém como dependência a biblioteca [PubSubClient](https://github.com/knolleary/pubsubclient), necessária para comunicações MQTT. Algumas versões do arduino-cli instalam ela automaticamente ao instalar a OTALabDevice, contudo, em algumas outras versões isso não é feito. Para saber se a biblioteca PubSubClient foi instalada, rode:

`arduino-cli lib list`

Caso a PubSubClient apareça, ela foi instalada. Caso contrário:

`arduino-cli lib install PubSubClient`

Feito isso, o código-fonte deve conter o seguinte template:

```
#include <OTALabDevice.h>

OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setWiFiNetworkName(WIFI_NETWORK_NAME);
    device->setWiFiNetworkPassword(WIFI_NETWORK_PASSWORD);
    device->setBrokerIP(BROKER_IP);
    device->setName(DEVICE_NAME);

    device->setup();
}

void loop(){
    device->handle();
}
```

As macros em maiúsculo serão substituidas de acordo com o dispositivo escolhido em tempo de compilação pelo sistema.

Note que caso o código-fonte faça uso de uma biblioteca de terceiros, esta deve estar instalada no arduino-cli localizado no servidor.

## 5. Uso

O sistema no momento é controlado via duas interfaces web, uma para o administrador, e outra para experimentadores. Também é possível executar tudo via linha de comando, mas este procedimento ainda está em desenvolvimento e deve ser evitado por enquanto.

### 5.1. Cadastro dos dispositivos

Antes de qualquer coisa, para cadastrar os dispositivos é necessário privilégios de superusuário do Linux, uma vez que os scripts são responsáveis por dar permissão para as portas USB às quais os dispositivos estão conectados. Portanto, forneça sua senha com:

`sudo su`

Agora sim é possível cadastrar os dispositivos, para isso, deve-se subir o site do administrador. Dentro da pasta AdminWebViewer, rode:

`mvn clean spring-boot:run`

Esse comando irá colocar o site do administrador no ar. Ele pode ser acessado pela url *localhost:8888/OTALabAdmin*.

![Página de administração vazia](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaAdministracaoVazia.png)

Primeiro, deve-se preencher o formulário da esquerda com os dados locais da sua rede Wi-Fi. Note que o sistema utiliza o [protocólo MQTT](https://mqtt.org/) para comunicação entre os dispositivos e o servidor, logo, deve haver um [broker MQTT](https://engprocess.com.br/mqtt-broker/). Esse broker pode ser local, via instalação do [mosquitto](https://mosquitto.org/), ou externo. Essa opção fica aberta ao usuário, o importante é que o IP seja inserido corretamente no formulário.

Feito isso, o formulário da direita deve ser preenchido para cada dispositivo a ser cadastrado. Note que nesse momento o dispositivo deve estar conectado à máquina fisicamente via um cabo USB. Segue um exemplo de como os campos devem ser preenchidos:

![Página de administração preenchida](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaAdministracaoPreenchida.png)

Ao cadastrar um dispositivo, um código-fonte padrão que implementa a biblioteca disponibilizada neste repositório na pasta OTALabDevice será enviado à ele via arduino-cli. Mais detalhes sobre essa biblioteca serão dados à frente.

Tendo cadastrado todos os dispositivos, seus dados estarão armazenados na pasta Dispositivos em formato JSON e eles já podem ser desconectados da máquina para serem reconhecidos via Over-The-Air (OTA).

### 5.2. Acesso aos dispositivos

Os dispositivos ativos serão mostrados na página do usuário, para isso, deve-se subir o segundo site. Dentro da pasta ExperimentadorWebViewer, rode:

`mvn clean spring-boot:run`

Esse comando irá colocar o site do experimentador no ar. Ele pode ser acessado pela url *localhost:9999/OTALabExperimentador*.

![Página de usuário vazia](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaClienteVazia.png)

Note que nenhum dispositivo aparece. Isso se deve ao fato de que eles devem ser reconhecidos como ativos pelo sistema. Nessa mesma página, existe um botão com uma lupa que, ao ser acionado, dispara uma sequência de scripts que reconhece os dispositivos que estão ativos naquele momento, isto é, aqueles que estão rodando um código que implementa a biblioteca do OTA Lab. Esse reconhecimento é realizado via MQTT e, ao final, os dispositivos aparecerão da seguinte forma:

![Página de experimentador preenchida](https://github.com/enzocussuol/OTALab/blob/main/Imagens/telaExperimentador.png)

### 6. Próximos Passos

O sistema ainda está em desenvolvimento e algumas coisas ainda devem ser implementadas.

<!--ts-->
* Realizar uma validação do código antes do envio
* Testar fisicamente o sistema assim como adicionar novos dispositivos suportados que não sejam da família ESP
* Automatizar o processo de instalação da aplicação
* Automatizar o processo de instalação das bibliotecas de terceiros
* Elaborar melhor o cadastro dos sensores
<!--te-->
