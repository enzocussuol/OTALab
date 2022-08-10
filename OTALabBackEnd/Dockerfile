FROM maven:3.8-jdk-11
COPY . .
COPY OTALabBackEnd/init.sh init.sh
COPY OTALabDevice OTALabDevice
RUN chmod +x init.sh
RUN ./init.sh
CMD mvn -f OTALabBackEnd/pom.xml clean spring-boot:run