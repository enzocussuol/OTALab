import paho.mqtt.client as mqttClient
from datetime import datetime

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to broker")

        client.subscribe("inTopic")

        client.publish("outTopic", "Are you alive?")
    else:
        print("Connection failed")

def on_message(client, userdata, message):
    dataHoraAtual = datetime.now()
    dataHoraAtual = dataHoraAtual.strftime('%d/%m/%Y %H:%M:%S')

    print(dataHoraAtual)
    print("Message received: "  + str(message.payload) + " " + dataHoraAtual + '\n')

IPBroker = "192.168.86.41"
port = 1883
# user = ""
# password = ""

client = mqttClient.Client("Python3")
# client.username_pw_set(user, password=password)

client.on_connect = on_connect
client.on_message = on_message

client.connect(IPBroker, port, 60)

client.loop_forever()