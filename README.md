# OTALab - Um ambiente para experimentação e atualização Over-The-Air em dispositivos IoT

## 0. Importante - SBRC2022

Para os usuários que chegaram à este repositório pelo Salão de Ferramentas do SBRC2022, recomenda-se que ao invés de realizar o git clone, façam o download da release SBRC2022, uma vez que novas atualizações foram realizadas no repositório desde o envio para à conferência.

## 1. Introdução

O OTALab começou como um projeto de Iniciação Científica pela UFES e se extendeu para a disciplina de Internet das Coisas. O objetivo desse sistema é prover um ambiente de rápido funcionamento e baixo custo para gerenciamento de dispositivos IoT ([veja quais dispositivos são atualmente suportados](https://github.com/enzocussuol/OTALab/blob/main/supportedDevices.txt)). A idéia é que um administrador posicione, sob uma mesma rede Wi-Fi, vários dispositivos com vários sensores acoplados e, com o uso do OTALab, seja capaz de gerenciá-los, tanto os monitorando quanto eventualmente atualizando seus códigos-fonte remotamente via Over-The-Air (OTA). Note que o uso pode ser extendido para usuários comúns, que não teriam privilégios de administrador, mas que seriam capazes de fazer upload para os dispositivos.

O sistema é composto de 3 grandes módulos: O [OTALabBackEnd](https://github.com/enzocussuol/OTALab/tree/main/OTALabBackEnd), desenvolvido em Java e fazendo uso do framework Spring Boot, o qual fornece uma API Rest que coordena o uso de todas as funcionalidades do OTALab. O OTALabBackEnd ainda se comunica com um banco de dados MySQL para persistência dos dados; O [OTALabFrontEnd](https://github.com/enzocussuol/OTALab/tree/main/OTALabFrontEnd), que fornece uma interface amigável para o usuário, mas que ainda não foi implementado; Por fim, o módulo [OTALabDevice](https://github.com/enzocussuol/OTALab/tree/main/OTALabDevice) oferece uma biblioteca Arduino desenvolvida com C++ para ser executada nos dispositivos que farão parte do OTALab.

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria Arduino que possibilita a gerência de placas Arduino a partir da linha de comando; 2) [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), um script python que realiza o envio de código via Over-The-Air para ESPs.

## 2. Containerização com Docker

A fim de tornar o OTALab executável em qualquer ambiente e resolver todas as dependências necessárias para seu correto funcionamento, o projeto foi containerizado utilizando o [Docker](https://www.docker.com/). O arquivo [docker-compose.yml](https://github.com/enzocussuol/OTALab/blob/main/docker-compose.yml) é responsável por inicializar todos os containers, os quais no momento são 2: 1 para o banco de dados MySQL e outro para a aplicação backend em Java, no qual é instalado o arduino-cli e a biblioteca OTALabDevice. Futuramente, convém adicionar mais um container para o frontend.

## 3. Instalação

Para iniciar a instalação clone este repositório com o comando:

`git clone https://github.com/enzocussuol/OTALab`

Isto irá copiar todos os arquivos deste repositório remoto para seu repositório local. Dentro da pasta OTALab, suba o sistema com o comando:

`docker-compose up`

Caso queira executar em background, adicione a flag -d ao final do comando. Após os containers serem inicializados, o uso do sistema é ditado pelo Docker. Parar os containers irá desligar o OTALab assim como retomá-los irá ligar o OTALab.

## 3. Uso

No momento, o OTALab está disponível ao usuário por meio de requisições à sua API Rest. O OTALab implementa a biblioteca [springdoc-openapi](https://springdoc.org/), que fornece uma documentação interativa da API. Para acessá-la, pelo navegador digite a url:

`http://localhost:8080/swagger-ui/index.html`

Aqui, o usuário tem acesso à todos os endpoints da API, podendo os utilizar e ter acesso à sua documentação. As funcionalidades variam desde criar dispositivos, configurar a rede utilizada, fazer upload para dispositivos cadastrados, etc.

## 4. A Biblioteca OTALabDevice

É essencial que qualquer código enviado para um dispositivo dentro do OTALab implemente a biblioteca OTALabDevice, uma biblioteca para Arduino. Ela está disponibilizada neste repositório e é instalada automaticamente no servidor ao subir os containers.

Para o usuário que deseja enviar um código para um dispositivo do OTALab, basta seguir o template abaixo, o qual adiciona o cabeçalho da biblioteca, define o id do dispositivo selecionado, instancia um dispositivo e chama as funções setup e handle, que fazem todo o trabalho de comunicação por baixo dos panos.

```
#include <OTALabDevice.h>

String id = "";
OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup(id);

    /* ... */ 
}

void loop(){
    device->handle();

    /* ... */
}
```

## 5. Quick Start
Como forma de demonstrar as funcionalidades do OTALab, foi elaborado esse quick start, no qual vamos cadastrar no OTALab um dispositivo modelo ESP8266 D1 Mini com um sensor de temperatura e umidade DHT11 acoplado. Após cadastrado, iremos enviar para ele um código via OTA que captura a telemetria e a envia para a plataforma [ThingSpeak](https://thingspeak.com/).

Aqui iremos representar cada passo como uma requisição HTTP à API do OTALab via o comando [cURL](https://www.hostinger.com.br/tutoriais/comando-curl-linux) do Linux, contudo, recomenda-se a utilização da interface interativa disponibilizada, a qual foi discuta na Seção 3. Só não iremos a utilizar nesse documento pois seria necessário um print em cada etapa, o que ocuparia muito espaço.

Também no que se refere à representação nesse quick start, algumas variáveis assumirão o símbolo X, que significa que elas irão variar de usuário para usuário.

### 5.1. Cadastro da Configuração

O primeiro passo é definir qual configuração iremos utilizar:

`
curl -X 'POST' 'http://localhost:8080/configuracoes/create?nomeWiFi=X&senhaWiFi=X&ipBroker=X'
`

Esse comando cria uma configuração com um id, que vamos supor ser igual à 1 nesse exemplo. Agora, precisamos tornar essa configuração ativa:

`
curl -X 'PUT' 'http://localhost:8080/configuracoes/setAsActive/1'
`

### 5.2. Cadastro do Dispositivo e Sensor

Com a configuração ativa, podemos cadastrar o dispositivo e o sensor utilizado. Vamos começar pelo dispositivo.

Para cadastrar um dispositivo no OTALab é preciso de um nome, uma descrição, uma placa e uma porta. Vamos supor que queremos dar o nome do dispositivo de "ESP8266D1Mini" e a descrição "Um ESP8266 D1 Mini com um sensor DHT11 acoplado". Quanto à placa, precisamos escolher a do nosso modelo, um D1 Mini. Podemos ver todas as placas instaladas com:

`
curl -X 'GET' 'http://localhost:8080/recursos/placas/read'
`

Dentre as placas retornadas, existe uma com o nome "esp8266:esp8266:d1_mini", que é a que queremos. Agora quanto à porta, vamos assumir que o dispositivo esteja conectado via USB à máquina hospedeira do OTALab. Para saber em qual porta ele está:

`
curl -X 'GET' 'http://localhost:8080/recursos/portas/read'
`

Vamos supor que ele esteja na porta ttyUSB0. Com isso, temos todas as informações necessárias e podemos cadastrar o dispositivo:

`
curl -X 'POST' 'http://localhost:8080/dispositivos/create?nome="ESP8266D1Mini"&descricao="ESP8266 D1 Mini com um sensor DHT11 acoplado"&placa="esp8266:esp8266:d1_mini"&portaCadastro="ttyUSB0"'
`

Note que um template, assim como o mostrado na Seção 4, é enviado para o dispositivo via USB no momento do cadastro. Com o dispositivo cadastrado, podemos desplugá-lo da máquina, basta que ele esteja ligado na energia para ser reconhecido pelo OTALab. Vamos supor que o dispositivo criado obteve o id 1.

Agora, vamos cadastrar o sensor. Vamos dar o nome de "DHT11" e uma descrição de "Sensor de umidade e temperatura":

`
curl -X 'POST' 'http://localhost:8080/sensores/create?nome="DHT11"&descricao="Sensor de umidade e temperatura"'
`

Novamente, suponhamos que o sensor recebeu o id 1. Agora, para associar o sensor com o dispositivo, precisamos criar um serviço:

`
curl -X 'POST' 'http://localhost:8080/servicos/create?idSensor=1&idDispositivo=1'
`

Novamente, assuma que o serviço criado obteve id 1.

Contudo, não adianta nada o sensor estar associado ao dispositivo se não sabemos as pinagens, isto é, as conexões entre eles. Por esse motivo, um serviço possui uma lista de pinagens. Para o nosso exemplo, vamos assumir as seguintes pinagens:

| ESP8266 D1 Mini | DHT11 |
| :-------------: | :---: |
| VCC             | PIN 1 |
| D1              | PIN 2 |
| GND             | PIN 4 |

Seguindo essa tabela, criamos as pinagens no nosso serviço:

`
curl -X 'POST' 'http://localhost:8080/pinagens/create?pinoDispositivo="VCC"&pinoSensor="PIN 1"&idServico=1'
`

`
curl -X 'POST' 'http://localhost:8080/pinagens/create?pinoDispositivo="D1"&pinoSensor="PIN 2"&idServico=1'
`

`
curl -X 'POST' 'http://localhost:8080/pinagens/create?pinoDispositivo="GND"&pinoSensor="PIN 4"&idServico=1'
`

### 5.3. Envio do Código

Com tudo cadastrado, basta selecionar o dispositivo e enviar nosso código que captura telemetria para ele. Contudo, no OTALab é impossível fazer uploads para dispositivos, mas sim para conexões. Uma conexão nada mais é que um dispositivo vivo na rede, afinal, podemos cadastrar um dispositivo mas ele estar desligado.

Vamos supor que ligamos nosso dispositivo ESP na tomada e ele está conectado à rede definida pela configuração criada. Dessa forma, ao atualizar a lista de conexões, o dispositivo estará nela, estando associado à um IP, que o representa na rede.

Para atualizar a lista de conexões, o OTALab realiza uma comunicação [MQTT](https://mqtt.org/) com os dispositivos, os quais estão programados, pela OTALabDevice, a responderem se estiverem vivos. Vamos utilizar a configuração criada, com id 1, e esperar por respostas até no máximo 10 segundos:

`
curl -X 'PUT' 'http://localhost:8080/conexoes/update?segundosEsperaRespostas=10&idConfiguracao=1'
`

Agora, considerando que tudo deu certo, temos uma conexão que representa o nosso dispositivo, a qual mais uma vez assumiremos que terá o id 1. Vamos finalmente fazer o upload para ele via OTA. O código enviado pode ser encontrado nos [exemplos da biblioteca OTALabDevice](https://github.com/enzocussuol/OTALab/tree/main/OTALabDevice/examples), com o nome de DHT11ThingSpeak. Dentro do código, basta definir o id do dispositivo, no caso 1, e o pino de dados, D1. As configurações dos canais do ThingSpeak ficam para o usuário:

`
curl -X 'POST' 'http://localhost:8080/conexoes/upload/1' -H 'Content-Type: multipart/form-data' -F 'file=@DHT11ThingSpeak.ino'
`

Por fim, basta acessar o ThingSpeak no canal definido e checar os dados de telemetria sendo enviados pelo dispositivo.

## 6. Próximos Passos

O OTALab ainda está em desenvolvimento e algumas coisas ainda devem ser implementadas, entre elas no momento se destacam:

<!--ts-->
* Desenvolvimento de um frontend;
* Realizar uma validação do código antes do envio para os dispositivos;
* Extender o suporte para mais dispositivos, além de ESPs.
<!--te-->

Trabalho sob a licença [Creative Commons 4.0](creativecommons.org), que permite copiar, distribuir e transmitir o trabalho em qualquer suporte ou formato desde que sejam citados a autoria e o licenciante. Está licença não permite o uso para fins comerciais e permite adaptações da obra desde que suas contribuições mantenham a mesma licença que o trabalho original.