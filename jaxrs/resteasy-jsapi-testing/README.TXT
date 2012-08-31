# Running RESTEasy-JSAPI Testing

0. Make sure you have Firefox installed on your computer
1. Download Selenium Server from http://seleniumhq.org/download/
2. Start Selenium Server: java -jar selenium-server-standalone.jar
3. Start jetty server in this test project: mvn jetty:run
4. Uncomment @Ignore in src/test/java/SmokingTest.java
5. Do the testing: mvn test
