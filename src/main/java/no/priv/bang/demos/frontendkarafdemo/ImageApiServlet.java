package no.priv.bang.demos.frontendkarafdemo;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.osgi.service.log.LogService;

import no.priv.bang.servlet.jersey.JerseyServlet;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=twelvemonkeys-demo)")
@HttpWhiteboardServletName("imageapi")
@HttpWhiteboardServletPattern("/api/*")
public class ImageApiServlet extends JerseyServlet {

    private static final long serialVersionUID = -5114200467422625900L;

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

    @Reference
    public void setImageService(ImageService imageService) {
        addInjectedOsgiService(ImageService.class, imageService);
    }

    @Activate
    public void activate() {
        // Called when DS component is activated
    }
}
