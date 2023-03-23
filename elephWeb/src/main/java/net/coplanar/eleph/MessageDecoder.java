package net.coplanar.eleph;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

  @Override
  public Message decode(String jsonMessage) throws DecodeException {

	System.out.println(jsonMessage);
    JsonObject jsonObject = Json
        .createReader(new StringReader(jsonMessage)).readObject();
    Message message = new Message();
    message.setType(jsonObject.getString("type"));
    message.setPayload(jsonObject.getJsonObject("payload"));
    return message;

  }

  @Override
  public boolean willDecode(String jsonMessage) {
    try {
      // Check if incoming message is valid JSON
      Json.createReader(new StringReader(jsonMessage)).readObject();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void init(EndpointConfig ec) {
    System.out.println("MessageDecoder -init method called");
  }

  @Override
  public void destroy() {
    System.out.println("MessageDecoder - destroy method called");
  }

}