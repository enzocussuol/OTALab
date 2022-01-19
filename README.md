# OTANetwork - Um ambiente para experimentação e atualização Over-The-Air em dispositivos IoT

## 1. Introdução

Este é um projeto de Iniciação Científica na área de Internet das Coisas. O objetivo desse sistema é prover um ambiente de rápido funcionamento e baixo custo para gerenciamento de dispositivos IoT ([veja quais dispositivos são atualmente suportados](https://github.com/enzocussuol/OTANetwork/blob/main/dispositivosSuportados.txt)). A idéia é que um administrador posicione, sob uma mesma rede Wi-Fi, vários dispositivos com vários sensores acoplados e, com o uso da OTANetwork, seja capaz de gerenciá-los, tanto os monitorando quanto eventualmente atualizando seus códigos-fonte. Note que o uso pode ser extendido para usuários comúns, que não teriam privilégios de administrador, mas que seriam capazes de fazer upload para os dispositivos.

O software é uma mistura de várias linguagens, as quais se destacam: Java e seu framework Spring Boot, para a criação da interface Web; Python, para realização de alguns scripts, entre eles o [espota](https://github.com/esp8266/Arduino/blob/master/tools/espota.py), cuja autoria está no link disponibilizado; Shell, também para realização de alguns scripts essenciais.

O sistema deve rodar em um ambiente Linux para que funcione corretamente.

## 2. Dependências e Instalação

Essa aplicação é desenvolvida a partir de dois principais softwares: 1) [arduino-cli](https://github.com/arduino/arduino-cli), uma ferramenta disponibilizada pela própria marca que possibilita a gerência de placas Arduino a partir da linha de comando; 2) espota, um script python que realiza o envio de código via Over the Air para ESPs. O arquivo espota.py já está incluso na forma de uma cópia do original nesse repositório.

Para iniciar a instalação simplesmente execute:

`git clone https://github.com/enzocussuol/OTANetwork`

Isto irá copiar todos os arquivos deste repositório para seu repositório local. Depois disso, basta instalar algumas poucas dependências.

### 2.1. Arduino-cli

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

### 2.2. JQ

Outro software utilizado foi o [jq](https://stedolan.github.io/jq/), um processador de json para linha de comando. Ele está presente em alguns scripts e para instalá-lo basta rodar:

`sudo apt-get install jq`

### 2.3. Maven

Para organizar as aplicações Java, foi utilizado o [Maven](https://maven.apache.org/). Para instalá-lo basta seguir o tutorial disponível [aqui](https://maven.apache.org/install.html).

## 3. Uso

O sistema no momento é controlado via duas interfaces web, uma para o administrador, e outra para usuários. Também é possível executar tudo via linha de comando, mas este procedimento ainda está em desenvolvimento e deve ser evitado por enquanto.

### 3.1. Cadastro dos dispositivos

O primeiro passo é cadastrar os dispositivos, para isso, deve-se subir o site do administrador. Dentro da pasta OTANetworkAdministrator, rode:

`mvn clean spring-boot:run`

Esse comando irá colocar o site do administrador no ar. Ele pode ser acessado pela url *localhost:8888*.

![Página de administração vazia](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaAdministracaoVazia.png)

Primeiro, deve-se preencher o formulário da esquerda com os dados locais da sua rede Wi-Fi. Note que o sistema utiliza o [protocólo MQTT](https://mqtt.org/) para comunicação entre os dispositivos e o servidor, logo, deve haver um [broker MQTT](https://engprocess.com.br/mqtt-broker/). Esse broker pode ser local, via instalação do [mosquitto](https://mosquitto.org/), ou externo. Essa opção fica aberta ao usuário, o importante é que o IP seja inserido corretamente no formulário.

Feito isso, o formulário da direita deve ser preenchido para cada dispositivo a ser cadastrado. Note que nesse momento o dispositivo deve estar conectado à máquina fisicamente via um cabo USB. Segue um exemplo de como os campos devem ser preenchidos:

![Página de administração preenchida](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaAdministracaoPreenchida.png)

Ao cadastrar um dispositivo, um código-fonte padrão que implementa a biblioteca disponibilizada neste repositório na pasta OTANetworkDevice será enviado à ele via arduino-cli. Mais detalhes sobre essa biblioteca serão dados à frente.

Tendo cadastrado todos os dispositivos, seus dados estarão armazenados na pasta Dispositivos em formato JSON e eles já podem ser desconectados da máquina para serem reconhecidos via Over-The-Air (OTA).

### 3.2. Acesso aos dispositivos

Os dispositivos ativos serão mostrados na página do usuário, para isso, deve-se subir o segundo site. Dentro da pasta OTANetworkClient, rode:

`mvn clean spring-boot:run`

Esse comando irá colocar o site do usuário no ar. Ele pode ser acessado pela url *localhost:9999*.

![Página de usuário vazia](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaClienteVazia.png)

Note que nenhum dispositivo aparece. Isso se deve ao fato de que eles devem ser reconhecidos antes. Dentro da pasta Scripts, basta rodar:

`bash atualizaDispositivos.sh <IP_BROKER>`

Esse script irá atualizar os dispositivos ativos e os inserir dentro da pasta Relatorios. A ideia é que o administrador programe esse script para rodar a cada x unidades de tempo. Agora, ao atualizar a página o usuário deve ser capaz de ver os dispositivos que estão vivos:

![Página de usuário preenchida](https://github.com/enzocussuol/OTANetwork/blob/main/Imagens/paginaClientePreenchida.png)

### 3.2. Utilizando a biblioteca OTANetworkDevice

É essencial que qualquer código enviado para um dispositivo dentro desse sistema implemente a biblioteca OTANetworkDevice. Ela está disponibilizada neste repositório, basta que seja instalada. Para isso, empacote a pasta OTANetworkDevice em um arquivo .zip e rode:

`arduino-cli lib install --zip-path OTANetworkDevice.zip`

Feito isso, o código-fonte deve conter o seguinte template:

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
