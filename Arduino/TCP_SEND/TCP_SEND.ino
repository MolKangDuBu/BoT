#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "SK_WiFi421A"    //  와이파이 이름
#define STAPSK  "1507069287"    //  와이파이 비밀번호
#endif

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
  pinMode(13,OUTPUT);
}
void loop() {
  
 WiFiClient client;

  if (!client.connect("192.168.35.46", 3002)) {
    Serial.println("Client disconnected");//서버 접속에 실패
    return;
  }
  else
  {
    String recevbline;
    if(count == 0)
    {
     client.write("GAS1");
     recevbline = client.readStringUntil('\r');
     ++count;
    }
    else
    {
      client.write("GAS2");
     recevbline = client.readStringUntil('\r');
     --count;
    }
  
     
     Serial.println("Client Connected");
     Serial.println(recevbline);
  }
  delay(5000);
}
