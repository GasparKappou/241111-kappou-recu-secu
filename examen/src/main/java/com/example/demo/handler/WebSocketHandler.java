package com.example.demo.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.model.MessageModel;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

	private List<WebSocketSession> sessions = new ArrayList<>();
	private List<TextMessage> mensajes = new ArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// Cuando se establece una conexión, agregar la sesión a la lista
		sessions.add(session);
		// Enviar todos los mensajes anteriores al nuevo cliente
		for (TextMessage mensaje : mensajes) {
			session.sendMessage(new TextMessage(mensaje.getPayload()));
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		System.out.println(message);
		String responseText = "";
		Gson gson = new Gson();

		MessageModel user = gson.fromJson(message.getPayload(), MessageModel.class);

		System.out.println(user.getEmail());

		if (payload.contains("123ABC") == false) {
			responseText = "{\"name\": \"Server\", \"email\": \"" + user.getEmail()
					+ "\", \"message\": \"se conecto incorrectamente\"}";
			for (WebSocketSession webSocketSession : sessions) {
				if (webSocketSession.isOpen()) {
					webSocketSession.sendMessage(new TextMessage(responseText));
				}
			}
		} else if (payload.contains("123ABC")) {
			responseText = "{\"name\": \"Server\", \"email\": \"" + user.getEmail()
					+ "\", \"message\": \"se conecto correctamente\"}";
			for (WebSocketSession webSocketSession : sessions) {
				if (webSocketSession.isOpen()) {
					webSocketSession.sendMessage(new TextMessage(responseText));
				}
			}
		}
		// Guardar el mensaje en la lista de mensajes
		mensajes.add(new TextMessage(responseText));

		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
			throws Exception {
		// Eliminar la sesión cuando se cierra la conexión
		sessions.remove(session);
	}
}