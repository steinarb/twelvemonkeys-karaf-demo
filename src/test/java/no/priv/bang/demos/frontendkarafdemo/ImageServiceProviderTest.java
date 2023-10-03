package no.priv.bang.demos.frontendkarafdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ImageServiceProviderTest {

    @Test
    void testReadOldJpegWithComment() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var imageFileName = "acirc1.jpg";
        var imageFileAttributes = Files.readAttributes(Path.of(getClass().getClassLoader().getResource(imageFileName).toURI()), BasicFileAttributes.class);
        var lastModifiedTime = imageFileAttributes.lastModifiedTime().toMillis();
        var inputstream = getClass().getClassLoader().getResourceAsStream(imageFileName);
        var connection = mock(HttpURLConnection.class);
        when(connection.getLastModified()).thenReturn(lastModifiedTime);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.getMetadata("http://localhost/acirc1.jpg");
        assertNotNull(imageMetadata);
        assertEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getComment()).startsWith("My VFR 750F");
    }

    @Test
    void testReadJpegWithExifMetadata() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var imageFileName = "CIMG0068.JPG";
        var imageFileAttributes = Files.readAttributes(Path.of(getClass().getClassLoader().getResource(imageFileName).toURI()), BasicFileAttributes.class);
        var lastModifiedTime = imageFileAttributes.lastModifiedTime().toMillis();
        var inputstream = getClass().getClassLoader().getResourceAsStream(imageFileName);
        var connection = mock(HttpURLConnection.class);
        when(connection.getLastModified()).thenReturn(lastModifiedTime);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.getMetadata("http://localhost/CIMG0068.JPG");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getComment()).isNullOrEmpty();
    }

}
