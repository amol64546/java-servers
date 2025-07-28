package com.json.springservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Person")
public class Person {
  private String name;
  private int age;

  // Default constructor is required for XML
  public Person() {}

  public Person(String name, int age) {
    this.name = name;
    this.age = age;
  }

  // Getters & Setters (important for both JSON and XML)
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }
}
