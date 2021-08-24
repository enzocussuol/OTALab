#include "Sensor.h"

Sensor::Sensor(String nome){
    this->nome = nome;
    this->conexoes = new std::map<String, String>;
}

String Sensor::getNome() const{
    return this->nome;
}

std::map<String, String>* Sensor::getConexoes() const{
    return this->conexoes;
}