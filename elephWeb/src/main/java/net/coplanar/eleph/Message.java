package net.coplanar.eleph;

import jakarta.json.JsonObject;

public class Message {

  private String type;
  private JsonObject payload;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public JsonObject getPayload() {
    return payload;
  }

  public void setPayload(JsonObject payload) {
    this.payload = payload;
  }

}