#!/bin/bash

PASS="123456"
DN_SERVER="server"
DN_CLIENT="client"
DN_UNTRUSTED="untrusted"
VALIDITY=1000
HOST=$1
DIR=$2

cd ${DIR}
rm -f client.* server.* client2.* server2.* untrusted.*

function generate_keystore
{
  FILE_NAME=$1
  DN=$2
  HOST_NAME=$3
  PASSWORD=$4

  keytool -genkey \
        -keyalg RSA \
        -alias $DN \
        -keystore $FILE_NAME \
        -storepass $PASSWORD \
        -validity $VALIDITY \
        -keysize 2048 \
        -keypass $PASSWORD \
        -dname 'CN=Tomas, OU=Tomas, O=Tomas, L=Brno, ST=Czech Republic, C=CZ' \
        -ext "SAN=dns:localhost,ip:$HOST_NAME"
  keytool -importkeystore -srckeystore $FILE_NAME \
     -destkeystore ${FILE_NAME%.*}.p12 -deststoretype PKCS12 \
     -srcstorepass $PASSWORD -deststorepass $PASSWORD
}

function export_certificate
{
  FILE_NAME=$1
  ALIAS=$2
  EXPORT_FILE_NAME=$3
  PASSWORD=$4

  keytool -export -alias $ALIAS -keystore $FILE_NAME -storepass $PASSWORD -file $EXPORT_FILE_NAME
}

function import_certificate
{
  FILE_NAME=$1
  ALIAS=$2
  IMPORT_FILE_NAME=$3
  PASSWORD=$4
  
  keytool -import -noprompt -alias $ALIAS -keystore $FILE_NAME -storepass $PASSWORD -file $IMPORT_FILE_NAME
}

generate_keystore "server.keystore" "$DN_SERVER" "$HOST" "$PASS"
generate_keystore "server2.keystore" "$DN_SERVER" "199.111.68.7" "$PASS"
generate_keystore "client.keystore" "$DN_CLIENT" "$HOST" "$PASS"
generate_keystore "untrusted.keystore" "$DN_UNTRUSTED" "$HOST" "$PASS"

export_certificate "server.keystore" "$DN_SERVER" "server.crt" "$PASS"
export_certificate "server2.keystore" "$DN_SERVER" "server2.crt" "$PASS"
export_certificate "client.keystore" "$DN_CLIENT" "client.crt" "$PASS"
export_certificate "untrusted.keystore" "$DN_UNTRUSTED" "untrusted.crt" "$PASS"

import_certificate "server.truststore" "$DN_CLIENT" "client.crt" "$PASS"
import_certificate "client.truststore" "$DN_SERVER" "server.crt" "$PASS"
import_certificate "client2.truststore" "$DN_SERVER" "server2.crt" "$PASS"