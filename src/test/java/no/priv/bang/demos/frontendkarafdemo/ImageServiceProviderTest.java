package no.priv.bang.demos.frontendkarafdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ImageServiceProviderTest {

    @Test
    void testReadOldJpegWithComment() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var inputstream = getClass().getClassLoader().getResourceAsStream("acirc1.jpg");
        var connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.getMetadata("http://localhost/acirc1.jpg");
        assertNotNull(imageMetadata);
        assertThat(imageMetadata.getComment()).startsWith("My VFR 750F");
    }

}
