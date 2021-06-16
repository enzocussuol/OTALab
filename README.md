# Upload Over the Air de Código Fonte para Múltiplos Dispositivos de Internet das Coisas

## 1. Introdução

Este repositório tem a finalidade de fornecer um conjunto de ferramentas para possibilitar, controlar e monitorar remotamente o envio e a execução de códigos fontes .ino ou .cpp para microprocessadores/microcontroladores que suportem o acoplamento de sensores. Os scripts são desenvolvidos em Shell, logo, devem ser rodados em um ambiente Linux.

## 2. Uso

### 2.1. Utilizando as Bibliotecas

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

O arquivo [OTABlink.ino]() fornece um exemplo de um arquivo .ino que implementa a biblioteca "DispositivoOTAWS" corretamente.
