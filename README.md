# Upload Over the Air de código fonte para uma rede IoT

## 1. Introdução

Este repositório tem a finalidade de fornecer um conjunto de ferramentas para possibilitar, controlar e monitorar remotamente o envio e a execução de códigos fontes .ino ou .cpp para microprocessadores/microcontroladores que suportem o acoplamento de sensores. Os scripts são desenvolvidos em Shell, logo, devem ser rodados em um ambiente Linux.

## 2. Instalação / Dependências

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria empresa que possibilita a gerência de placas Arduino a partir da linha de comando; 2) [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), um script python que realiza o envio de código via Over the Air para ESPs. O arquivo espota.py já está incluso na forma de uma cópia do original nesse repositório, logo, o usuário não precisa de preocupar com isso.

É necessário que o arduino-cli seja instalado, ver [instalação arduino-cli](https://arduino.github.io/arduino-cli/latest/installation/). Tendo instalado o software, deve-se realizar alguns passos adicionais. O ESP8266 e o ESP32 não são placas originalmente suportadas pelo arduino-cli, logo, devem ainda ser instalados núcleos para essas placas, visto que elas são de produções de terceiros (recomenda-se ver [instalação placas terceirizadas no arduino-cli](https://create.arduino.cc/projecthub/B45i/getting-started-with-arduino-cli-7652a5)). Para isso, é preciso inserir pacotes adicionais no arquivo de configuração do arduino-cli. Se esse arquivo já não existir, crie-o com:

`arduino-cli config init`

Abra-o em qualquer editor de texto e crie, caso não existir, o campo "additional_urls:", dentro de "board_manager".
Dentro de "additional_urls", irão ser inseridas as URLs para as placas que o usuário deseja. No caso dessa aplicação, para instalar os pacotes para o ESP8266 e o ESP32, o arquivo ficaria assim:

`board_manager:
  additional_urls: 
    - https://arduino.esp8266.com/stable/package_esp8266com_index.json
    - https://dl.espressif.com/dl/package_esp32_index.json`

## 3. Uso

### 3.1. Utilizando as Bibliotecas

Para que o código fonte esteja apto a ser enviado/monitorado remotamente a partir dos scripts deste repositório, é necessário que ele inclua e utilize as funções da biblioteca "DispositivoOTAWS" disponibilizada aqui. Essa biblioteca inclui todos os arquivos com extensão .h e .cpp fornecidos.

Há duas maneiras de incluir a biblioteca no código. O primeiro caso é simplesmente trabalhar com o .ino ou .cpp na mesma pasta dos arquivos .h e .cpp que estão neste repositório. Dessa forma, basta que o usuário insira em seu código a linha:

`#include "DispositivoOTAWS"`

Um segundo caso seria comprimir os arquivos .h e .cpp em um .zip e instalar, por meio da ferramenta de [instalação de bibliotecas da Arduino IDE](https://www.robocore.net/tutoriais/adicionando-bibliotecas-na-ide-arduino#:~:text=Dispon%C3%ADvel%20na%20IDE%20do%20Arduino,Include%20Library). Dessa forma, o usuário deve inserir a linha:

`#include <DispositivoOTAWS>`

Feito isso, é necessário criar um objeto para representar um dispositivo. Isso pode ser feito com:

`Dispositivo* dispositivo = new Dispositivo(<nome do dispositivo>);`

Obviamente, o nome do dispositivo irá variar de acordo com o dispositivo que o usuário deseja utilizar. Uma lista com os dispositivos suportados e como chamá-los por seu nome na hora da criação do objeto encontra-se no arquivo [dispositivosSuportados.txt](https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/blob/main/dispositivosSuportados.txt).

Tendo criado o objeto, basta, na função setup, chamar:

`dispositivo->start();`

E na função loop, chamar:

`dispositivo->handle();`

Essas duas funções irão lidar com os procedimentos de conexão por baixo dos panos. Outras funções serão futuramente implementadas, tais como funções para obter quais sensores estão conectados ao dispositivo, etc.

O arquivo [OTABlink.ino](https://github.com/enzocussuol/OTA-Multiplos-Dispositivos/blob/main/OTABlink.ino) fornece um exemplo de um arquivo .ino que implementa a biblioteca "DispositivoOTAWS" corretamente.

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
