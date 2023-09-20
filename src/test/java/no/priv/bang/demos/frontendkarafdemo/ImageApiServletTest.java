package no.priv.bang.demos.frontendkarafdemo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import static javax.ws.rs.core.MediaType.*;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;
import no.priv.bang.demos.frontendkarafdemo.beans.ImageRequest;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ImageApiServletTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetMetadata() throws Exception {
        var imageService = mock(ImageService.class);
        var imageMetadata = ImageMetadata.with().status(200).lastModified(Date.from(Instant.ofEpochMilli(844453726000L))).contentType("image/jpeg").contentLength(71072).comment("My VFR 750F, in front of Polarsirkelsenteret.  Arctic Circle, Ranamunicipality, Northern Norway.").build();
        when(imageService.getMetadata(anyString())).thenReturn(imageMetadata);
        var logservice = new MockLogService();
        var servlet = new ImageApiServlet();
        servlet.setLogService(logservice);
        servlet.setImageService(imageService);
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);

        var imageRequest = ImageRequest.with().url("https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg").build();
        var request = buildPostUrl("/image/metadata", imageRequest);
        var response = new MockHttpServletResponse();
        servlet.service(request, response);

        assertEquals(200, response.getStatus());
    }

    private MockHttpServletRequest buildPostUrl(String resource, Object body) throws Exception {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(APPLICATION_JSON);
        request.setHeader("content-type", APPLICATION_JSON);
        request.setBodyContent(mapper.writeValueAsString(body));
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/twelvemonkeys-karaf-demo/api" + resource);
        request.setRequestURI("/twelvemonkeys-karaf-demo/api" + resource);
        request.setContextPath("/twelvemonkeys-karaf-demo");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.demos.frontendkarafdemo.resources");
        var servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/twelvemonkeys-karaf-demo");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }
}
