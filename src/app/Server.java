package app;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
	
	static int server_port;
	static String target_host;
	static int target_port;

	static int off_header;
	static byte[] buff_header = new byte[10000];

	static byte[] buff_payload;
	
	static void perror(String msg) {
		System.out.println(msg);
	} 
	static void perrorexit(String msg) {
		perror(msg);
		System.exit(0);
	}
	
	static String read_line(InputStream is) {
		try {
			int c;
			int startOffset = off_header;
			while (true) {
				if (off_header >= buff_header.length) perrorexit("read_line: read buffer overflow");
				c = is.read();
				if (c == -1) perrorexit("read_line: unexpected EOF");
				buff_header[off_header++] = (byte)c;
				if (c == '\r') {
					if (off_header >= buff_header.length) perrorexit("read_line: read buffer overflow");
					c = is.read();
					if (c == -1) perrorexit("read_line: unexpected EOF");
					buff_header[off_header++] = (byte)c;
					if (c != '\n') perrorexit("read_line: incomplete CRLF sequence");
					int sz = off_header-startOffset-2;
					String ret = new String(buff_header, startOffset, sz);
					//System.out.println("read_line: received: "+ret);
					return ret;
				}
			}
		} catch (IOException e) {
			perrorexit("readline: IOException");
			return null;
		}
	}
	
	static void read_buf(InputStream is, byte[] buff, int len) {
		try {
			int offset = 0;
			if ((buff.length-offset)<len) perrorexit("read_buf: read buffer overflow");
			while (len>0) {
				int ret = is.read(buff, offset, len);
				if (ret == -1) perrorexit("read_buf: read failed");
				//System.out.println("read_buf: received: "+ret+" bytes");
				offset += ret;
				len -= ret;
			}
		} catch (IOException e) {
			perrorexit("read_buf: IOException");
		}
	}
	
	static void write_buf(OutputStream os, byte[] buff, int len) {
		try {
			os.write(buff, 0, len);
		} catch (IOException e) {
			perrorexit("write_buf: IOException");
		}
	}

	static int content_length(String line) {
	
		if (line.startsWith("Content-Length: ")) {
			int index = line.indexOf(" ")+1;
			String len = line.substring(index);
			try {
				return Integer.parseInt(len);
			} catch (Exception ex) {
				return 0;
			}
		}
		return 0;
	}
			
	static String get_request(String line) {
		int index = line.indexOf(" ");
		return line.substring(0, index);
	}
	
	static String get_return(String line) {
		int index1 = line.indexOf(" ")+1;
		int index2 = line.indexOf(" ", index1);
		return line.substring(index1, index2);
	}


	static String get_path(String line) {
		int index1 = line.indexOf(" ")+2;
		int index2 = line.indexOf(" ", index1);
		return line.substring(index1, index2);
	}


	static void handle_web(Socket cli) {

		try {
			int len, content_len, sz;
			String line, path, request;
			InputStream is;
			OutputStream os;

			off_header = 0;

			is = cli.getInputStream();
			line = read_line(is);
			request = get_request(line);
			path = get_path(line);

			content_len = 0;
			while (true) {
				line = read_line(is);
				len = content_length(line);
				if (len>0) content_len = len;
				if (line.length() == 0) break;
			}
			//System.out.println("Web request header: "+off_header);
			//System.out.println("Web request Content_Length: "+content_len);
			if (content_len>0) {
				buff_payload = new byte[content_len];
				read_buf(is, buff_payload, content_len);
			}

			os = cli.getOutputStream();
			if (request.equals("GET")) {
				System.out.println("Web request GET ("+path+")");
				try {
					sz = (int)new File(path).length();
					FileInputStream fis = new FileInputStream(path);
					buff_payload = new byte[sz];
					read_buf(fis, buff_payload, sz);
					String headers = "HTTP/1.0 200 OK\r\n";
					//System.out.println("Web response: "+headers);
					byte headerb[] = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					headers = "Content-Length: "+sz+"\r\n\r\n";
					//System.out.println("Web response: "+headers);
					headerb = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					write_buf(os, buff_payload, sz);
					System.out.println("Web response: "+sz);
					cli.close();
					return;
				} catch (Exception ex) {
					perror("file not found");
					String headers = "HTTP/1.0 404 Not Found\r\n\r\n";
					byte headerb[] = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					System.out.println("Web response: not found\n");
					cli.close();
					return;
				}
			}
			perrorexit("Web request: unknown");
		} catch (IOException e) {
			perrorexit("handle_web: IOException");
		}
	}


	static void handle_inter(Socket cli) {

		try {
			int content_len, len;
			String line, path, request, ret;
			InputStream cis, sis;
			OutputStream cos, sos;

			off_header = 0;

			cis = cli.getInputStream();
			line = read_line(cis);
			request = get_request(line);
			path = get_path(line);

			content_len = 0;
			while (true) {
				line = read_line(cis);
				len = content_length(line);
				if (len>0) content_len = len;
				if (line.length() == 0) break;
			}
			//System.out.println("Web request header; "+off_header);
			//System.out.println("Web request Content_Length: "+content_len);
			
			byte[] buff_payload = null;
			if (content_len>0) {
				buff_payload = new byte[content_len];
				read_buf(cis, buff_payload, content_len);
			}

			cos = cli.getOutputStream();
			if (request.equals("GET")) {
				System.out.println("Web request GET ("+path+")");
				try {
					//System.out.println("handle_inter: before socket "+target_host+"/"+target_port);
					Socket serv = new Socket(target_host, target_port);

					sos = serv.getOutputStream();
					sis = serv.getInputStream();

					write_buf(sos, buff_header, off_header);
					if (content_len>0) write_buf(sos, buff_payload, content_len);

					//System.out.println("Inter request Have written: "+(off_header+content_len));

					off_header = 0;
					line = read_line(sis);
					ret = get_return(line);
					//System.out.println("Inter response: "+ret);

					content_len = 0;
					while (true) {
						line = read_line(sis);
						len = content_length(line);
						if (len>0) content_len = len;
						if (line.length() == 0) break;
					}
					System.out.println("Inter response Content_Length: "+content_len);
					if (content_len>0) {
						buff_payload = new byte[content_len];
						read_buf(sis, buff_payload, content_len);
					}
					serv.close();
					//System.out.println("----------------INTER WRITE-----------");

					////////////////////////////////////////////////////////////////////
					// touch the payload
					//byte b = buff_payload[0];
					//////////////////////////////////////////////////////////////////


					write_buf(cos, buff_header, off_header);
					if (content_len>0) write_buf(cos, buff_payload, content_len);

					//System.out.println("Inter response: "+(off_header+content_len));
					cli.close();
					return;
				} catch (Exception ex) {
					perror("file not found");
					String headers = "HTTP/1.0 404 Not Found\r\n\r\n";
					byte headerb[] = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(cos, headerb, headerb.length);
					//System.out.println("Web response: not found\n");
					cli.close();
					return;
				}
			}
			perrorexit("Web request unknown");
		} catch (IOException e) {
			perrorexit("handle_inter: IOException");
		}
	}

	static enum Role {UNKNOWN, WEB, INTER, LB};

	public static void main(String args[]) {

		try {
			Role role = Role.UNKNOWN;

			// java Server web|inter|lb <server_port> <target_host> <target_port>

			if (args.length < 2) perrorexit("main: bad args number");
			server_port = Integer.parseInt(args[1]);

			switch (args[0]) {
			case "web":
				System.out.println("web server");
				role = Role.WEB; break;
			case "inter":
				System.out.println("intermediate server");
				role=Role.INTER;
				if (args.length != 4) perrorexit("main: bad args number");
				target_host = args[2];
				target_port = Integer.parseInt(args[3]);
				break;
			case "lb":
				System.out.println("load balancing server");
				role=Role.LB;
				if (args.length != 4) perrorexit("main: bad args number");
				target_host = args[2];
				target_port = Integer.parseInt(args[3]);
				break;
			}

			ServerSocket ss = new ServerSocket(server_port);

			while (true) {
				Socket cli = ss.accept();

				switch (role) {
				case WEB: handle_web(cli); break;
				case INTER: handle_inter(cli); break;
				case LB: handle_inter(cli); break;
				}
				//System.out.println("close connection\n");
			}
		} catch (NumberFormatException e) {
			perrorexit("main: bad args");
		} catch (IOException e) {
			perrorexit("main: IOException");
		}
	}
}



