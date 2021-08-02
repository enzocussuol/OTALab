import paho.mqtt.client as mqttClient
import time
import os

IP_BROKER = "192.168.86.41" # IP do broker
PORTA = 1883 # Numero da porta
# USER = ""
# PASSWORD = ""
MAX_TEMPO_RESPOSTA = 1 # Espera por respostas por no maximo x segundos

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to broker")

        client.subscribe("Inicializacao/inTopic")

        client.publish("Inicializacao/outTopic", "Are you alive?")
    else:
        print("Connection failed")

def on_message(client, userdata, message):
    arqDisp.write(str(message.payload.decode("utf-8")) + "\n")

try:
    os.remove("~/OTA-Multiplos-Dispositivos/Relatorios")
except:
    print("Nao foi possivel remover o arquivo dispositivos.txt (ja nao existe)")
    
arqDisp = open("~/OTA-Multiplos-Dispositivos/Relatorios", "a")

client = mqttClient.Client("Python3")
# client.username_pw_set(user, password=password)

client.connect(IP_BROKER, PORTA, 60)

client.on_connect = on_connect
client.on_message = on_message

tempoInicio = time.time()
while True:
    tempoAtual = time.time()
    tempoPercorrido = tempoAtual - tempoInicio

    if tempoPercorrido >= MAX_TEMPO_RESPOSTA:
        arqDisp.close()
        client.disconnect()
        break

    client.loop()