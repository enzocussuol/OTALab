# Upload Over the Air de código fonte para uma rede IoT

## 1. Introdução

Este repositório tem a finalidade de fornecer um conjunto de ferramentas para possibilitar, controlar e monitorar remotamente o envio e a execução de códigos fontes .ino ou .cpp para microprocessadores/microcontroladores que suportem o acoplamento de sensores. O sistema é desenvolvido para ser executado em um ambiente Linux.

## 2. Instalação / Dependências

### 2.1. Softwares Necessários

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria marca que possibilita a gerência de placas Arduino a partir da linha de comando; 2) [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), um script python que realiza o envio de código via Over the Air para ESPs. O arquivo espota.py já está incluso na forma de uma cópia do original nesse repositório, logo, o usuário não precisa se preocupar com isso.

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

Além disso, outro software utilizado foi o [jq](https://stedolan.github.io/jq/), um processador de json para linha de comando. Para instalá-lo, basta rodar:

`sudo apt-get install jq`

### 2.2. Instalação de Bibliotecas

O arduino-cli também permite a instalação de bibliotecas externas, assim como a Arduino IDE. Essas bibliotecas vão parar no mesmo repositório (.arduino15/staging/libraries). Para obter ajuda sobre como gerenciar bibliotecas com o arduino-cli, rode:

`arduino-cli lib --help`

Com relação a implementação da biblioteca fornecida nesse repositório, apenas uma biblioteca externa foi utilizada, a que fornece os mecanismos para implementação do protocolo MQTT. Caso esta já não esteja instalada, basta executar:

`arduino-cli lib install "PubSubClient"`

Bibliotecas adicionais irão depender de cada projeto e cabe ao usuário instalá-las quando for necessário. Lembrando que isso pode ser feito tanto pela Arduino IDE quanto pelo arduino-cli.

### 2.3. Broker MQTT

Como já dito, o sistema faz uso do [protocolo MQTT](https://www.hitecnologia.com.br/blog/o-que-e-protocolo-mqtt/) para se comunicar com os dispositivos. Para isso, é necessário que exista um broker no qual tanto os dispositivos quanto os scripts se conectarão. No presente momento, qualquer broker público irá servir, mas, é recomendável a utilização do [mosquitto](https://mosquitto.org/).

Tudo que o usuário precisa é saber o IP do broker. No caso do mosquitto, o IP será o da própria máquina (127.0.0.1).

### 2.4. Diretório

Para que o sistema funcione corretamente, é recomendável que o usuário baixe os arquivos deste repositório na forma de um .zip, pelo próprio GitHub. Feito isso, basta descompactar o .zip em sua pasta pessoal no Linux.

É importante que o usuário não faça alterações nos nomes das pastas, pois isso pode acarretar em problemas no sistema em geral.

Uma outra opção seria clonar esse repositório em uma pasta com o nome OTA-Multiplos-Dispositivos, localizada na pasta pessoal. Isso poderia ser feito com o comando:

`git clone https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/`

## 3. Uso

### 3.1. O arquivo de Configuração Conf.h

O primeiro passo para o usuário utilizar o sistema é inserir seus dados internos dentro do arquivo de configuração. Futuramente, pretende-se melhorar essa parte para tornar a experiência do usuário mais agradável.

Esse arquivo está dentro da pasta Biblioteca. Aqui, o usuário deve inserir o nome da sua rede WiFi, a senha dessa rede e o IP do broker escolhido. Todos os campos devem estar entre aspas duplas, pois serão interpretados como strings. Por exemplo, se a minha rede WiFi se chama teste, a senha é 123 e o IP do meu broker é 123.123.123.123, o arquivo Conf.h deve ser modificado para:

```
  NOME_WIFI "teste"
  SENHA_WIFI "123"
  IP_BROKER "123.123.123.123"
```

### 3.2. Utilizando as Bibliotecas

Para que o código fonte esteja apto a ser enviado/monitorado remotamente a partir dos scripts deste repositório, é necessário que ele inclua e utilize as funções da biblioteca disponibilizada aqui. Essa biblioteca está na pasta Biblioteca e inclui todos os arquivos .h e .cpp necessários.

Para utilizar a biblioteca, o usuário deve inserir em seu código:

`#include "Dispositivo"`

Os scripts garantirão que os arquivos .h e .cpp irão parar na mesma pasta do código. Futuramente, essa questão será repensada para que seja possível instalar a biblioteca.

Feito isso, é necessário criar um objeto para representar um dispositivo. Isso pode ser feito com:

`Dispositivo* dispositivo = new Dispositivo(<nome do dispositivo>);`

Obviamente, o nome do dispositivo irá variar de acordo com o dispositivo que o usuário deseja utilizar. Uma lista com os dispositivos suportados e como chamá-los por seu nome na hora da criação do objeto encontra-se no arquivo [dispositivosSuportados.txt](https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/blob/main/dispositivosSuportados.txt).

Tendo criado o objeto, basta, na função setup, chamar:

`dispositivo->start();`

E na função loop, chamar:

`dispositivo->handle();`

Essas duas funções irão lidar com os procedimentos de conexão e processamento por baixo dos panos. Outras funções serão futuramente implementadas, tais como funções para obter quais sensores estão conectados ao dispositivo, etc.

Na pasta Exemplos estão arquivos que implementam a biblioteca corretamente, assim, o usuário pode se basear neles para implementar seu próprio código.

### 3.3. O Primeiro Envio

É importante que o primeiro envio do código para o dispositivo seja realizado via cabo. Isso se deve ao fato de que as bibliotecas precisam reconhecer o dispositivo na rede para que seja possível eventualmente enviar código por essa rede para esse dispositivo.

Após esse envio via cabo, que pode também ser realizado a partir da Arduino IDE ou pelo arduino-cli, o envio pode ser realizado via OTA normalmente a partir dos scripts fornecidos.

### 3.4. Utilizando os Scripts

Para o correto funcionamento do sistema, os scripts devem ser executados em uma ordem correta. Os próximos tópicos irão explicar o que cada script faz, os quais devem ser executados na ordem em que aparecem aqui.

#### 3.4.1. descobreDispositivos.py

Esse script se baseia em detectar os dispositivos na rede a partir do protocolo MQTT. Ele irá se conectar à um broker, o qual os dispositivos também devem estar conectados, e os enviar uma mensagem perguntando se estão vivos. Feito isso, ele irá esperar 1 segundo por respostas. Se um dispositivo escutou a mensagem, ele irá enviar de volta seu IP, que será guardado em um arquivo chamado dispositivos.txt, armazenado na pasta Relatorios. Sua execução é feita com:

`python3 descobreDispositivos.py <IPBroker>`

Onde \<ipBroker\> é o mesmo IP do broker inserido no arquivo Conf.h.

#### 3.4.2. difereAtivosInativos.sh

Esse script simplesmente vai ler o arquivo dispositivos.txt, e, para cada IP dentro dele, ou seja, para cada dispositivo, ele vai pingar nesse IP. Se o IP responder, o dispositivo está ativo naquele momento, e será inserido em um arquivo ativos.json, no formato JSON. Se não, ele irá ser inserido em um arquivo inativos.txt. Ambos esses arquivos estarão na pasta Relatorios. O script pode ser executado com:

`bash difereAtivosInativos.sh`

#### 3.4.3. enviaCodigo.sh e enviaTodos.sh

Esses dois scripts realizam de fato o envio do código para o dispositivo via OTA. O enviaTodos.sh é simplesmente um loop que chama o enviaCodigo.sh. O script enviaCodigo.sh compila e envia o código para o dispositivo escolhido.

O script enviaCodigo.sh é parâmetrizado e deve ser executado na linha de comando na forma:

`bash Scripts/enviaCodigo.sh <id> <nomeProjeto>`

Onde \<id\> é o id do dispositivo em ativos.json para o qual o código está sendo enviado e \<nomeProjeto\> é o nome do projeto do usuário, por exemplo, OTABlink.

Ao final do script, existirá uma pasta com o nome do projeto definido pelo usuário.
  
Caso seja escolhido o script enviaTodos.sh, basta passar somente o nome do projeto como parâmetro.

### 4. Próximos Passos

O sistema ainda está incompleto. Os próximos passos são:

<!--ts-->
* Realizar uma validação do código antes do envio
* Testar fisicamente o sistema assim como adicionar novos dispositivos suportados
* Elaborar uma interface gráfica (web) com o usuário
* Atualizar os scripts para que a biblioteca seja instalada, retirando a obrigação do código do usuário estar no mesmo diretório que ela
* Automatizar o processo de instalação da aplicação
* Implementar de fato o MQTT para gerenciar os sensores que estão acoplados nos dispositivos
* Resolver a questão do arquivo de configuração para dados pessoais do usuário
* Fazer com que o upload do usuário vá parar no servidor
<!--te-->
