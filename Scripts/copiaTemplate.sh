#!/bin/bash

sed "s/String id = \"\"/String id = \"$1\"/" OTATemplate.ino > OTADefault/OTADefault.ino
