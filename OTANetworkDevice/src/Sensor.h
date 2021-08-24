#ifndef SENSOR_H
    #define SENSOR_H

    class Sensor;

    #include <Arduino.h>
    #include <map>

    class Sensor{
        private:
            String nome;
            std::map<String, String>* conexoes;
        public:
            Sensor(String);
            String getNome() const;
            std::map<String, String>* getConexoes() const;
    };
#endif