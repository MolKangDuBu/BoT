#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "SecurityLab(2.4)"    //  와이파이 이름
#define STAPSK  "security915"  //와이파이 비밀번호
#endif
#include <DHT.h>
#define PIN_DHT 5
#define PIN_TYPE DHT11
DHT DHTsensor(PIN_DHT, PIN_TYPE);

/*와이파이*/
const char* ssid     = STASSID;
const char* password = STAPSK;

int count = 0;

/*TCP 서버 포트*/
const int tcpPort = 22485;    //  서버로 사용될 포트(지금 예시에서는 22485를 사용)
WiFiServer wifiServer(tcpPort);
void setup() {
  /*시리얼 모니터 보드레이트*/
  Serial.begin(115200);
  /*온습도조절계 초기화*/
   DHTsensor.begin();
  /*WiFi setup*/
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  /*와이파이 연결될 때까지 반복*/
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  wifiServer.begin();
  Serial.println("Server On");
  Serial.print("Port: ");
  Serial.println(tcpPort);
  pinMode(PIN_DHT,OUTPUT);
}
void loop() {
 double humidity = DHTsensor.readHumidity();
 double temp = DHTsensor.readTemperature();
 String HUM = "";
 String TEMP ="";
 HUM += humidity;
 TEMP += temp;
 WiFiClient client;

  if (!client.connect("192.168.0.12", 3001)) {
      delay(3000);
     Serial.print("IOT2");
     Serial.print(",");
     Serial.print("HUM/TEMP");
     Serial.print(",");
     Serial.print(humidity);
     Serial.print(",");
     Serial.println(temp);
    Serial.println("Client disconnected");//서버 접속에 실패
    return;
  }
  else
  {
     String recevbline;
     Serial.print("Humidity : ");
     Serial.print(humidity);
     Serial.print(" Temperature : ");
     Serial.print(temp);
     Serial.println(" ºC");
     client.print("IoT1,tmpHum,"+HUM+","+TEMP);
     recevbline = client.readStringUntil('\r');
     Serial.println("Client Connected");
     Serial.println(recevbline);
     
  }
  delay(5000);
}
