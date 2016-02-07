//Serial Command library from https://github.com/scogswell/ArduinoSerialCommand
#include <SerialCommand.h>
#include <SoftwareSerial.h>

SerialCommand SCmd;   // The demo SerialCommand object
#define arduinoLED 13   // Arduino LED on board

boolean DEBUG = false;

// Variables for Callipper Reading
int i;
int sign;
long value;
float measure;
int clockpin = 8;
int datapin = 9;
unsigned long tempmicros;
unsigned long revMillis;

//Variables for Stepper Motor control
int ENA=2;//connected to Arduino's port 2
int IN1=3;//connected to Arduino's port 3
int IN2=4;//connected to Arduino's port 4
int ENB=5;//connected to Arduino's port 5
int IN3=6;//connected to Arduino's port 6
int IN4=7;//connected to Arduino's port 7

// Variables for measuring process
int nbCycles = 1;
int nbSteps = 600; // number of steps for a full revolution
int stepCount = 0; // will 0/600
int currentStep = 1; // will loop 1/2/3/4
int stepDirection = -1; // 1 : clockwise / -1 : counter clockwise

void setup() {
  //Calliper Reading
  Serial.begin(9600);
  pinMode(clockpin, INPUT);
  pinMode(datapin, INPUT);

  //Stepper Motor
  pinMode(ENA,OUTPUT);
  pinMode(ENB,OUTPUT);
  pinMode(IN1,OUTPUT);
  pinMode(IN2,OUTPUT); 
  pinMode(IN3,OUTPUT);
  pinMode(IN4,OUTPUT);
  digitalWrite(ENA,HIGH);//enable motorA
  digitalWrite(ENB,HIGH);//enable motorB

  // Setup callbacks for SerialCommand commands 
  SCmd.addCommand("PING",ping);
  SCmd.addCommand("STEP",doStep); //Do one step 
  SCmd.addCommand("MEASURE",getMeasure); //Get measure and print result 
  SCmd.addCommand("SET_DIRECTION",setDirection); //Set the direction of rotation
  
    SCmd.addDefaultHandler(unrecognized);          // Handler for command that isn't matched  (says "What?") 
}

void loop()
{  
  SCmd.readSerial();     // We don't do much, just process serial commands
}

void getMeasure(){
      if(!DEBUG){
        readCalliper();
      }
      else{ //DEBUG VERSION
        measure = random(0, 10);
      }

      //Serial.print("MEASURE ");
      Serial.println(measure,2);
}

void doStep() {
  currentStep = currentStep + stepDirection;
  if(currentStep > 4) currentStep = 1;
  if(currentStep < 1) currentStep = 4;

  switch (currentStep) {
  case 1:
    digitalWrite(IN1,LOW);
    digitalWrite(IN2,HIGH);
    digitalWrite(IN3,HIGH);
    digitalWrite(IN4,LOW);
    break;
  case 2:
    digitalWrite(IN1,LOW);
    digitalWrite(IN2,HIGH);
    digitalWrite(IN3,LOW);
    digitalWrite(IN4,HIGH);
    break;
  case 3:
    digitalWrite(IN1,HIGH);
    digitalWrite(IN2,LOW);
    digitalWrite(IN3,LOW);
    digitalWrite(IN4,HIGH);
    break;
  case 4:
    digitalWrite(IN1,HIGH);
    digitalWrite(IN2,LOW);
    digitalWrite(IN3,HIGH);
    digitalWrite(IN4,LOW);
  }  
  Serial.println("STEP_ACK"); 
}

void readCalliper() {
  while (digitalRead(clockpin)==HIGH) { 
  } //if clock is LOW wait until it turns to HIGH
  tempmicros=micros();
  while (digitalRead(clockpin)==LOW) { 
  } //wait for the end of the HIGH pulse

  if ((micros()-tempmicros)>500) { //if the HIGH pulse was longer than 500 micros we are at the start of a new bit sequence
    decodeCalliper(); //decode the bit sequence
  }
}

void decodeCalliper() {
  sign=1;
  value=0;

  for (i=0;i<23;i++) {
    while (digitalRead(clockpin)==HIGH) {  
    } //wait until clock returns to HIGH- the first bit is not needed
    while (digitalRead(clockpin)==LOW) { 
    } //wait until clock returns to LOW
    if (digitalRead(datapin)==LOW) {
      if (i<20) {
        value|= 1<<i;
      }
      if (i==20) {
        sign=-1;
      }
    }
  }
  measure=(value*sign)/100.00;
}

void ping()
{
  Serial.println("PING_ACK"); 
}

void setDirection() {
  String arg = SCmd.next();
  if (arg != NULL) {
    stepDirection = arg.toInt();
  } 
  Serial.println("DIR_ACK"); 
}


// This gets set as the default handler, and gets called when no other command matches. 
void unrecognized()
{
  Serial.println("What?"); 
}

