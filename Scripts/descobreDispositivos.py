import paho.mqtt.client as mqttClient
import time
import os
import sys

IP_BROKER = sys.argv[1] # IP do broker
PORTA = 1883 # Numero da porta
# USER = ""
# PASSWORD = ""
MAX_TEMPO_RESPOSTA = 30 # Espera por respostas por no maximo x segundos

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to broker")

        client.subscribe("Inicializacao/inTopic")

        client.publish("Inicializacao/outTopic", "Are you alive?")
    else:
        print("Connection failed")

def on_message(client, userdata, message):
    print("Um dispositivo foi reconhecido!")

    arqDisp.write(str(message.payload.decode("utf-8")) + "\n")

pathDisp = "/home/" + os.environ.get("USER") + "/OTANetwork/Relatorios/dispositivos.txt"

try:
    os.remove(pathDisp)
except:
    print("Nao foi possivel remover o arquivo dispositivos.txt (ja nao existe)")

arqDisp = open(pathDisp, "a")

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