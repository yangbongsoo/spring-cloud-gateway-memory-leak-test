package requestDownstream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;

	public RequestHandler(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		log.debug("New client! Connected IP : {}, port : {}", connection.getInetAddress(), connection.getPort());
		OutputStream out = null;
		InputStream in = null;
		try {
			in = connection.getInputStream();
			out = connection.getOutputStream();

			// request
			DataInputStream dis = new DataInputStream(in);
			byte[] byteArr = new byte[25000];
			int readByteCount = dis.read(byteArr);
			log.info("readByteCount : {}", readByteCount);
			String data = new String(byteArr, 0, readByteCount, "UTF-8");

//			String[] split = data.split("\n");
//			for (int i = 0; i< split.length; i++) {
//				System.out.println(i + ": " + split[i]);
//			}
//			log.info("request : {}", data);

//			String bodyString = split[split.length-1];


			// response
			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = data.getBytes();
//			response400Header(dos, body.length);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
	}

	//	@Override
	//	public void run() {
	//		log.debug("New client! Connected IP : {}, port : {}",connection.getInetAddress(), connection.getPort());
	//		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
	//			DataOutputStream dos = new DataOutputStream(out);
	//			byte[] body = "Hello World".getBytes();
	//			response200Header(dos, body.length);
	//			responseBody(dos, body);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}

	private void response400Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 400 BAD REQUEST \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void response500Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 400 INTERNEL SERVER ERROR \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
