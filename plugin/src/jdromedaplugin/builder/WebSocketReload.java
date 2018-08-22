package jdromedaplugin.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
// TODO Loggin g

public class WebSocketReload extends WebSocketAdapter {

	public static List<Session> sessions = new ArrayList<Session>();
	public Session sess;

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		sessions.add(sess);
		this.sess = sess;
		System.out.println("Browser is connected: " + sess);
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		// currently no task
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		sessions.remove(sess);
		sess = null;
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		sessions.remove(sess);
		sess = null;
	}

	public static void reloadAll() {
		for (Session session : sessions) {
			if (session.isOpen()) {
				try {
					session.getRemote().sendString("reload");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
