package odb;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContext;
public class MyHttpServletRequest extends HttpServletRequestWrapper {
    
public MyHttpServletRequest(HttpServletRequest request) {
super(request);
}
@Override
public jakarta.servlet.ServletInputStream getInputStream() throws IOException {
// Fournir MyServletInputStream basé sur MyInputStream
return new MyServletInputStream(new MyInputStream(super.getInputStream(), false, null));  // Changé à false
}
}