set PASS="123456"
set DN_SERVER="server"
set DN_CLIENT="client"
set DN_UNTRUSTED="untrusted"
set VALIDITY=1000
set HOST=%~1
set DIR=%~2

cd %DIR%
del /Q /F client.* server.* client2.* server2.* untrusted.* 2>nul

>output.txt (
  call :generate_keystore "server", %DN_SERVER%, %HOST%, %PASS%
  call :generate_keystore "server2", %DN_SERVER%, "199.111.68.7", %PASS%
  call :generate_keystore "client", %DN_CLIENT%, %HOST%, %PASS%
  call :generate_keystore "untrusted", %DN_UNTRUSTED%, "127.0.0.1", %PASS%

  call :export_certificate "server.keystore", %DN_SERVER%, "server.crt", %PASS%
  call :export_certificate "server2.keystore", %DN_SERVER%, "server2.crt", %PASS%
  call :export_certificate "client.keystore", %DN_CLIENT%, "client.crt", %PASS%
  call :export_certificate "untrusted.keystore", %DN_UNTRUSTED%, "untrusted.crt", %PASS%

  call :import_certificate "server.truststore", %DN_CLIENT%, "client.crt", %PASS%
  call :import_certificate "client.truststore", %DN_SERVER%, "server.crt", %PASS%
  call :import_certificate "client2.truststore", %DN_SERVER%, "server2.crt", %PASS%
)
del /Q /F output.txt 2>nul
del /Q /F output2.txt 2>nul
EXIT /B %ERRORLEVEL%

:generate_keystore
  set FILE_NAME=%~1
  set DN=%~2
  set HOST_NAME=%~3
  set PASSWORD=%~4

  keytool -genkey ^
        -keyalg RSA ^
        -alias %DN% ^
        -keystore "%FILE_NAME%.keystore" ^
        -storepass %PASSWORD% ^
        -validity %VALIDITY% ^
        -keysize 2048 ^
        -keypass %PASSWORD% ^
        -dname "CN=Tomas, OU=Tomas, O=Tomas, L=Brno, ST=Czech Republic, C=CZ" ^
        -ext "SAN=dns:localhost,ip:%HOST_NAME%" >>output2.txt
  rem export as PKCS12
  keytool -importkeystore -srckeystore "%FILE_NAME%.keystore" ^
    -destkeystore "%FILE_NAME%.p12" -deststoretype PKCS12 ^
    -srcstorepass %PASSWORD% -deststorepass %PASSWORD% >>output2.txt
EXIT /B 0

:export_certificate
  set FILE_NAME=%~1
  set ALIAS=%~2
  set EXPORT_FILE_NAME=%~3
  set PASSWORD=%~4

  keytool -export -alias %ALIAS% -keystore %FILE_NAME% -storepass %PASSWORD% -file %EXPORT_FILE_NAME% >>output2.txt
EXIT /B 0

:import_certificate
  set FILE_NAME=%~1
  set ALIAS=%~2
  set IMPORT_FILE_NAME=%~3
  set PASSWORD=%~4

  keytool -import -noprompt -alias %ALIAS% -keystore %FILE_NAME% -storepass %PASSWORD% -file %IMPORT_FILE_NAME% >>output2.txt
EXIT /B 0
