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
        assertThat(imageMetadata.getTitle()).isNullOrEmpty();
        assertThat(imageMetadata.getDescription()).startsWith("My VFR 750F");
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
        assertThat(imageMetadata.getTitle()).isNullOrEmpty();
        assertThat(imageMetadata.getDescription()).isNullOrEmpty();
    }

    @Test
    void testReadJpegWithDescriptionInExifMetadata() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var imageFileName = "CIMG0068_with_description.JPG";
        var imageFileAttributes = Files.readAttributes(Path.of(getClass().getClassLoader().getResource(imageFileName).toURI()), BasicFileAttributes.class);
        var lastModifiedTime = imageFileAttributes.lastModifiedTime().toMillis();
        var inputstream = getClass().getClassLoader().getResourceAsStream(imageFileName);
        var connection = mock(HttpURLConnection.class);
        when(connection.getLastModified()).thenReturn(lastModifiedTime);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.getMetadata("http://localhost/CIMG0068_with_description.JPG");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getTitle()).startsWith("Autumn leaves at Gålå");
        assertThat(imageMetadata.getDescription()).isNullOrEmpty();
    }

    @Test
    void testReadJpegWithDescriptionAndUserCommentInExifMetadata() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var imageFileName = "CIMG0068_with_description_and_user_comment.JPG";
        var imageFileAttributes = Files.readAttributes(Path.of(getClass().getClassLoader().getResource(imageFileName).toURI()), BasicFileAttributes.class);
        var lastModifiedTime = imageFileAttributes.lastModifiedTime().toMillis();
        var inputstream = getClass().getClassLoader().getResourceAsStream(imageFileName);
        var connection = mock(HttpURLConnection.class);
        when(connection.getLastModified()).thenReturn(lastModifiedTime);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.getMetadata("http://localhost/CIMG0068_with_description_and_user_comment.JPG");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getTitle()).startsWith("Autumn leaves at Gålå");
        assertThat(imageMetadata.getDescription()).startsWith("Pretty red leaves");
    }

    @Test
    void testSplitUserCommentInEncodingAndComment() throws Exception {
        byte[] userCommentRaw = {65, 83, 67, 73, 73, 0, 0, 0, 80, 114, 101, 116, 116, 121, 32, 114, 101, 100, 32, 108, 101, 97, 118, 101, 115};
        var provider = new ImageServiceProvider();
        var splitUserComment = provider.splitUserCommentInEncodingAndComment(userCommentRaw);
        var encoding = new String(splitUserComment.get(0), "UTF-8");
        var comment = new String(splitUserComment.get(1), "UTF-8");
        assertThat(encoding).startsWith("ASCII");
        assertThat(comment).isEqualTo("Pretty red leaves");
    }

}
