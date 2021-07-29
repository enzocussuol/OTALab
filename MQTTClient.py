import paho.mqtt.client as mqttClient
import time
from datetime import datetime

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to broker")

        global Connected                # Use global variable
        Connected = True                # Signal connection
    else:
        print("Connection failed")

def on_message(client, userdata, message):
    # data_e_hora_atuais = datetime.now()
    # data_e_hora_em_texto = data_e_hora_atuais.strftime('%d/%m/%Y %H:%M:%S')
    # # print(data_e_hora_em_texto)
    # print "Message received: "  + message.payload + " " + data_e_hora_em_texto + '\n'
    # with open('/root/teste/test.txt','a+') as f:
    #      f.write("Message received: "  + message.payload + " " + data_e_hora_em_texto + '\n' )

    dataHoraAtual = datetime.now()
    dataHoraAtual = dataHoraAtual.strftime('%d/%m/%Y %H:%M:%S')

    print(dataHoraAtual)

    print("Message received: "  + str(message.payload) + " " + dataHoraAtual + '\n')
 
Connected = False   # Global variable for the state of the connection

IPBroker = "192.168.86.41"  # Broker address
port = 1883                 # Broker port
# user = ""                 # Connection username
# password = ""             # Connection password

client = mqttClient.Client("Python3")               # Create new instance
# client.username_pw_set(user, password=password)   # Set username and password

client.on_connect = on_connect                      # Attach function to callback
client.on_message = on_message                      # Attach function to callback

client.connect(IPBroker, port, 60) # Connect

# Subscriptions
client.subscribe("inTopic")
client.subscribe("outTopic")

client.loop_forever() # Then keep listening forever