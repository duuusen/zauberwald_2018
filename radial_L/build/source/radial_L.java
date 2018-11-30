import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class radial_L extends PApplet {

// ArrayList<Data> data = new ArrayList<Data>();
Table table;
Rings ringWind;
Rings ringRain;
Rings ringSun;
Clock cConsumption;
Clock cSun;
Clock cWind;
Clock cRain;

float water = 0;
float wind = 0;
float solar = 0;
float t = 0;

float maxValWind = 3565;
float maxValRain = 6.6f;
float maxValSun = 59.5f;

public void setup() {
  
  
  noCursor();
  load();
  cConsumption=new Clock("consumption", color(255), color(200), 2500000); //eigentlich 3000000
  cWind=new Clock("wind", color(255, 0, 255, 200), color(255, 0, 255), 45);
  cSun=new Clock("sun", color(255, 255, 0, 200), color(255, 255, 0), 150);
  cRain=new Clock("rainfall", color(0, 255, 255, 200), color(0, 255, 255), 10);
  ringWind = new Rings("wind", color(255, 0, 255), 160, 3565);
  ringRain = new Rings("rainfall", color(0, 255, 255), 160, 59.5f);
  ringSun = new Rings("sun", color(255, 255, 0), 160, 6.6f);

  ringWind.addRingOutside();
  ringRain.addRingOutside();
  ringSun.addRingOutside();
}

public void draw() {
  t += 0.2f / 60.0f / 5.0f;
  background(0);
  cConsumption.display();
  cSun.display();
   cWind.display();
  cRain.display();
  ringSun.paint();
  ringWind.paint();
  ringRain.paint();
  fill(0);
  noStroke();
  ellipse(width/2,height/2,250,250);
  if (t > 1) {
    t = 0;
  }
}
class Rings {
  int ringCount = 0;
  float minRadius = 0.0f;
  float ringDistance = 20.0f;
  float amplitude = 0.4f;
  int ringColor;
  float seed;

  Rings(String rName, int ringColor, float radius, float mapper) {
    this.ringColor = ringColor;
    this.minRadius = radius;
    this.seed = random(0, 1000000);
    for (int i=0; i<table.getRowCount(); i++) {
      float data = table.getFloat(i, rName);
      float dataMapped = map(data, 0, mapper, 0, 150); // this maps rain and sun proportionally to the wind Data
      // float ampMapped = map(mapPropRainSun,0,3565,0,100);
      amplitude = dataMapped;
      println(rName + dataMapped);
    }
  }
  public void addRingInside() {
    if(minRadius - ringDistance < 0) {
      return;
    }
    minRadius -= ringDistance;
    ringCount++;
  }
  public void addRingOutside() {
    ringCount++;
  }
  public void removeRingInside() {
    if(ringCount <= 0) {
      return;
    }
    ringCount--;
    // Kleinster Ring wurde entfernt: neuer kleinster Ring ist da wo zuvor der zweitkleinste lag (minRadius + ringDistance)
    minRadius += ringDistance;
  }
  public void removeRingOutside() {
    ringCount--;
    if(ringCount < 0) {
      ringCount = 0;
    }
  }
  public void paint() {
    stroke(ringColor);
    strokeWeight(3);
    noFill();
    for(int i = 0; i<ringCount; i++) {
      paintRing(i);
    }
  }
  public void paintRing(int i) {
    float radius = (minRadius + (i * ringDistance))/2;
    float variance = amplitude * radius;
    float min = radius - variance;
    float max = radius + variance;

    pushMatrix();
    translate(width/2, height/2);
    beginShape();
    for (float angle = 0; angle < 360; angle += 0.8f) {
      float theta = radians(angle);
      float x = cos(theta);
      float y = sin(theta);
      float r1 = noise(theta, (frameCount+seed)*0.06f);
      r1 = map(r1, 0, 1, min, max);
      vertex(x*r1, y*r1);
    }
    endShape(CLOSE);
    popMatrix();
  }
}
class WeatherData {
  float sun;
  float rain;
  float wind;

  WeatherData(float sun, float rain, float wind) {
    this.rain = rain;
    this.sun = sun;
    this.wind = wind;
  }
}
ArrayList<WeatherData> weatherData;
public void load() {
  table = loadTable("all_data_2017.csv", "header,csv");
  weatherData = new ArrayList<WeatherData>();
    for (TableRow row : table.rows()) {
      float rain = row.getFloat("rainfall");
      float sun = row.getFloat("sun");
      float wind = row.getFloat("wind");
      WeatherData wd = new WeatherData(rain,sun,wind);
      weatherData.add(wd);
    }
}
class Clock { //void viz(float t, float water, float wind, float solar)
  ArrayList<PVector> list;
  int counter=0;
  int counterArray= 0;
  int Fill;
  int Stroke;
  float angle = 0;
  float angleSpeed = 2.2f;
  float lineSize;

  Clock(String cName, int fill, int stroke, float mapper) {
    list = new ArrayList<PVector>();
    for (int i=0; i<table.getRowCount(); i++) {
      float data = table.getFloat(i, cName);
      float newSize = map(data, 0, mapper, 0, height/2);
      lineSize = newSize;
      float x= cos(radians(angle))*lineSize+width/2;
      float y= sin(radians(angle))*lineSize+height/2;
      angle = angle + angleSpeed;
      list.add(new PVector(x, y));
    }
    Fill=fill;
    Stroke=stroke;
  }

  public void display() {
    beginShape();
    noStroke();
    curveVertex(width/2, height/2);
    curveVertex(width/2, height/2);
    for (int i= counterArray; i<counter; i++) {
      //stroke(255);
      fill(Fill);
      curveVertex(list.get(i).x, list.get(i).y);
    }
    endShape();

    if (counter>165&&counter<list.size()-1&&frameCount%1==0) {
      counterArray++;
    }

    if (counter<list.size()&&frameCount%1==0) {
      counter++;
    }
  }
}
  public void settings() {  size(800, 800,P3D);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "radial_L" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
