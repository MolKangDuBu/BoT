#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "olleh_WiFi_EFF1"    //  와이파이 이름
#define STAPSK  "0000007898"    //  와이파이 비밀번호
#endif
#define PIN_GAS A0
/*와이파이*/
const char* ssid     = STASSID;
const char* password = STAPSK;

/*TCP 서버 포트*/
const int tcpPort = 22485;    //  서버로 사용될 포트(지금 예시에서는 22485를 사용)
WiFiServer wifiServer(tcpPort);
void setup() {
  /*시리얼 모니터 보드레이트*/
  Serial.begin(115200);

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
  pinMode(PIN_GAS,OUTPUT);
}
void loop() {
  
  int gas = analogRead(PIN_GAS);
  float gaspoint = (float)gas/1024*5.0;
  String tostring = "";
  tostring += gaspoint;

  WiFiClient client;

  if (!client.connect("172.30.1.35", 22485)) {
    Serial.println("Client disconnected");//서버 접속에 실패
      Serial.print("IOT3");
      Serial.print(",");
      Serial.print("GAS");
      Serial.print(",");
      Serial.print(tostring);
      Serial.println();
     delay(5000);
    return;
  }
  else
  {
      String recevbline;
      client.write("IOT3");
      client.write(",");
      client.write("GAS");
      client.write(",");
      client.print(tostring);
      client.write("\n");
      Serial.print("IOT3");
      Serial.print(",");
      Serial.print("GAS");
      Serial.print(",");
      Serial.print(tostring);
      Serial.println();
     recevbline = client.readStringUntil('\r');
     Serial.println("Client Connected");
     Serial.println(recevbline);
    }   
       delay(5000);
  }
