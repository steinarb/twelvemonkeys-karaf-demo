package no.priv.bang.demos.frontendkarafdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.osgi.service.adapters.logservice.LogServiceAdapter;

@Component(service={Servlet.class}, property={"alias=/frontend-karaf-demo"} )
public class ReactServlet extends HttpServlet {
    private static final long serialVersionUID = 250817058831319271L;
    private final LogServiceAdapter logservice = new LogServiceAdapter();

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice.setLogService(logservice);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null) {
                // Browsers won't redirect to bundle.js if the servlet path doesn't end with a "/"
                addSlashToServletPath(request, response);
                return;
            }

            String resource = findResourceFromPathInfo(pathInfo);
            String contentType = guessContentTypeFromResourceName(resource);
            response.setContentType(contentType);
            try(ServletOutputStream responseBody = response.getOutputStream()) {
                try(InputStream resourceFromClasspath = getClass().getClassLoader().getResourceAsStream(resource)) {
                    if (resourceFromClasspath != null) {
                        copyStream(resourceFromClasspath, responseBody);
                        response.setStatus(200);
                        return;
                    }

                    String message = String.format("Resource \"%s\" not found on the classpath", resource);
                    logservice.log(LogService.LOG_ERROR, message);
                    response.sendError(404, message);
                }
            }
        } catch (IOException e) {
            logservice.log(LogService.LOG_ERROR, "Frontend servlet caught exception ", e);
            response.setStatus(500); // Report internal server error
        }
    }

    String guessContentTypeFromResourceName(String resource) {
        String contentType = URLConnection.guessContentTypeFromName(resource);
        if (contentType != null) {
            return contentType;
        }

        String extension = resource.substring(resource.lastIndexOf('.') + 1);
        if ("xhtml".equals(extension)) {
            return "text/html";
        }

        if ("js".equals(extension)) {
            return "application/javascript";
        }

        if ("css".equals(extension)) {
            return "text/css";
        }

        return null;
    }

    private String findResourceFromPathInfo(String pathInfo) {
        if ("/".equals(pathInfo)) {
            return "index.xhtml";
        }

        return pathInfo;
    }

    private void addSlashToServletPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(String.format("%s/", request.getServletPath()));
    }

    private void copyStream(InputStream input, ServletOutputStream output) throws IOException {
        int c;
        while((c = input.read()) != -1) {
            output.write(c);
        }
    }

}
