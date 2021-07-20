# Upload Over the Air de código fonte para uma rede IoT

## 1. Introdução

Este repositório tem a finalidade de fornecer um conjunto de ferramentas para possibilitar, controlar e monitorar remotamente o envio e a execução de códigos fontes .ino ou .cpp para microprocessadores/microcontroladores que suportem o acoplamento de sensores. Os scripts são desenvolvidos em Shell, logo, devem ser rodados em um ambiente Linux.

## 2. Instalação / Dependências

### 2.1. Softwares Necessários

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria empresa que possibilita a gerência de placas Arduino a partir da linha de comando; 2) [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), um script python que realiza o envio de código via Over the Air para ESPs. O arquivo espota.py já está incluso na forma de uma cópia do original nesse repositório, logo, o usuário não precisa se preocupar com isso.

É necessário que o arduino-cli seja instalado, ver [instalação arduino-cli](https://arduino.github.io/arduino-cli/latest/installation/). Tendo instalado o software, deve-se realizar alguns passos adicionais. O ESP8266 e o ESP32 não são placas originalmente suportadas pelo arduino-cli, logo, devem ainda ser instalados núcleos para essas placas, visto que elas são de produções de terceiros (recomenda-se ver [instalação placas terceirizadas no arduino-cli](https://create.arduino.cc/projecthub/B45i/getting-started-with-arduino-cli-7652a5)).

Resumindo e especificando o passo a passo do link acima para o caso dos dois [dispositivos suportados](https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/blob/main/dispositivosSuportados.txt) até então, faça:

`arduino-cli core install esp8266:esp8266`

`arduino-cli core install esp32:esp32`

Feito isso, para checar as placas instaladas, basta rodar:

`arduino-cli board listall`

O resultado deve ser uma série de placas para o ESP8266 e para o ESP32. Agora, devem ser adicionados os pacotes necessários para essas placas funcionarem corretamente. É necessário inserir esses pacotes no arquivo de configuração do arduino-cli, logo, caso este ainda não tenha sido criado, faça:

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

Além disso, outro software utilizado foi o [jq](https://stedolan.github.io/jq/), um processador de json para linha de comando. Para instalá-lo, basta rodar:

`sudo apt-get install jq`

### 2.2. Instalação de Bibliotecas

O arduino-cli também permite a instalação de bibliotecas externas, assim como a Arduino IDE. Essas bibliotecas vão parar no mesmo repositório (.arduino15/staging/libraries). Para obter ajuda sobre como gerenciar bibliotecas com o arduino-cli, rode:

`arduino-cli lib --help`

Com relação a implementação da biblioteca fornecida nesse repositório, apenas uma biblioteca externa foi utilizada, a que fornece os mecanismos para implementação do protocolo MQTT. Caso esta já não esteja instalada, basta executar:

`arduino-cli lib install "PubSubClient"`

Bibliotecas adicionais irão depender de cada projeto e cabe ao usuário instalá-las quando for necessário. Lembrando que isso pode ser feito tanto pela Arduino IDE quanto pelo arduino-cli.

## 3. Uso

### 3.1. Utilizando as Bibliotecas

Para que o código fonte esteja apto a ser enviado/monitorado remotamente a partir dos scripts deste repositório, é necessário que ele inclua e utilize as funções da biblioteca "DispositivoOTAWS" disponibilizada aqui. Essa biblioteca inclui todos os arquivos com extensão .h e .cpp fornecidos.

Para utilizar a biblioteca, o usuário deve inserir em seu código:

`#include "DispositivoOTAWS"`

Os scripts garantirão que os arquivos .h e .cpp irão parar na mesma pasta do código. Futuramente, essa questão será repensada para que seja possível instalar a biblioteca.

Feito isso, é necessário criar um objeto para representar um dispositivo. Isso pode ser feito com:

`Dispositivo* dispositivo = new Dispositivo(<nome do dispositivo>);`

Obviamente, o nome do dispositivo irá variar de acordo com o dispositivo que o usuário deseja utilizar. Uma lista com os dispositivos suportados e como chamá-los por seu nome na hora da criação do objeto encontra-se no arquivo [dispositivosSuportados.txt](https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/blob/main/dispositivosSuportados.txt).

Tendo criado o objeto, basta, na função setup, chamar:

`dispositivo->start();`

E na função loop, chamar:

`dispositivo->handle();`

Essas duas funções irão lidar com os procedimentos de conexão por baixo dos panos. Outras funções serão futuramente implementadas, tais como funções para obter quais sensores estão conectados ao dispositivo, etc.

Na pasta Exemplos então arquivos que implementam a biblioteca corretamente.

### 3.2. Utilizando os Scripts

Para o correto funcionamento do sistema, os scripts devem ser executados em uma ordem correta. Os próximos tópicos irão explicar o que cada script faz, os quais devem ser executados na ordem em que aparecem nos tópicos.

#### 3.2.1. geraArquivosIps.sh

Esse script simplesmente gera um arquivo .txt dentro da pasta Relatorios com todos os ips possíveis de uma determinada rede.

#### 3.2.2. detectaDispositivos.sh

Esse script também gera um arquivo .txt dentro da pasta Relatorios com todos os ips ativos na rede, isto é, que estão de fatos conectados.

#### 3.2.3. verificaDisponibilidade.sh

Aqui, o script irá gerar dois arquivos. Primeiro, há uma filtragem para considerar apenas dispositivos de Internet das Coisas (momentâneamente, o script está considerando apenas ESPs). Depois, é gerado, novamente na pasta Relatorios, um arquivo ativos.json, que irá conter uma lista de dispositivos ativos no formato .json, onde cada dispositivo recebe um id na ordem em que foi inserido no arquivo e um arquivo inativos.txt, que irá conter uma lista de dispositivos inativos, sem id's.

#### 3.2.4. enviaCodigo.sh e enviaTodos.sh

Esses dois scripts realizam de fato o envio do código para o dispositivo via OTA. O enviaTodos.sh é simplesmente um loop que chama o enviaCodigo.sh. O script enviaCodigo.sh compila e envia o código. A compilação é realizada a partir do software [arduino-cli](https://github.com/arduino/arduino-cli) e o envio é realizado pelo script python [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py).

O script enviaCodigo.sh é parâmetrizado e deve ser executado na linha de comando na forma:

`bash enviaCodigo.sh <id> <nomeProjeto>`

Onde \<id\> é o id do dispositivo em ativos.json para o qual o código está sendo enviado e \<nomeProjeto\> é o nome do projeto do usuário, por exemplo, OTABlink.

Ao final do script, existirá uma pasta com o nome do projeto definido pelo usuário.
  
Caso seja escolhido o script enviaTodos.sh, basta passar somente o nome do projeto como parâmetro.

### 4. Próximos Passos

O sistema ainda está incompleto. Os próximos passos são: 1) Realizar uma validação do código antes do envio; 2) Testar fisicamente o sistema assim como adicionar novos dispositivos suportados; 3) Elaborar uma interface gráfica (web) com o usuário.
