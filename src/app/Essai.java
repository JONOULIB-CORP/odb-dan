package app;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Essai {
	
	static final int BUF_SIZE = 50000000;
	static byte buf[] = new byte[BUF_SIZE];
	static int offset;

    static void write_buf(OutputStream os, byte buffer[], int len) {}

	static void read_buf(InputStream is, int len) {}

	static void handle_web(Socket cli) {
int lala;
		try {
			int len, content_len, sz;
			String line, path, request;
			InputStream is;
			OutputStream os;
           

			offset = 0;
			is = cli.getInputStream();
			//line = read_line(is);
			//request = get_request(line);
			//path = get_path(line);
            len = 0;
            line = "";
            request = "";
            path = "";

			content_len = 0;
			while (true) {
				//line = read_line(is);
				//len = content_length(line);
				if (len>0) content_len = len;
				if (line.length() == 0) break;
			}
			//System.out.println("Web request Content_Length: "+content_len);
			if (content_len>0) read_buf(is, content_len);

			os = cli.getOutputStream();
			if (request.equals("GET")) {
				System.out.println("Web request GET ("+path+")");
				try {
					sz = (int)new File(path).length();
					FileInputStream fis = new FileInputStream(path);
					offset = 0;
					read_buf(fis, sz);
					String headers = "HTTP/1.0 200 OK\r\n";
					//System.out.println("Web response: "+headers);
					byte headerb[] = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					headers = "Content-Length: "+sz+"\r\n\r\n";
					//System.out.println("Web response: "+headers);
					headerb = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					write_buf(os, buf, sz);
					//System.out.println("Web response: "+sz);
					cli.close();
					return;
				} catch (Exception ex) {
					//perror("file not found");
					String headers = "HTTP/1.0 404 Not Found\r\n\r\n";
					byte headerb[] = headers.getBytes(StandardCharsets.US_ASCII);
					write_buf(os, headerb, headerb.length);
					System.out.println("Web response: not found\n");
					cli.close();
					return;
				}
			}
			//perrorexit("Web request: unknown");
		} catch (IOException e) {
			//perrorexit("handle_web: IOException");
		}
	}
	
}