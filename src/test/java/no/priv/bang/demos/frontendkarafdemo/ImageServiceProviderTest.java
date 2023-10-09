package no.priv.bang.demos.frontendkarafdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;
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
    void testGetMetadataWithNonExistingUrl() throws Exception {
        var provider = new ImageServiceProvider();

        var e = assertThrows(RuntimeException.class, () -> provider.getMetadata("http://localhost/CIMG0068_with_description_and_user_comment.JPG"));
        assertThat(e.getMessage()).startsWith("Error when reading image metadata for");
    }

    @Test
    void testGetMetadataWithNullImageUrl() throws Exception {
        var provider = new ImageServiceProvider();

        var metadata = provider.getMetadata(null);
        assertNull(metadata);
    }

    @Test
    void testGetMetadataWithEmptyImageUrl() throws Exception {
        var provider = new ImageServiceProvider();

        var metadata = provider.getMetadata("");
        assertNull(metadata);
    }

    @Test
    void testGetMetadataWithHttpConnectionThatThrowsIOException() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenThrow(IOException.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var e = assertThrows(RuntimeException.class, () -> provider.getMetadata("http://localhost/CIMG0068_with_description_and_user_comment.JPG"));
        assertThat(e.getMessage()).startsWith("Error when reading image metadata for");
    }

    @Test
    void testGetMetadataWithIOExceptionWhenConnectingWithHttp() throws Exception {
        var provider = new ImageServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenThrow(IOException.class);
        provider.setConnectionFactory(connectionFactory);

        var e = assertThrows(RuntimeException.class, () -> provider.getMetadata("http://localhost/CIMG0068_with_description_and_user_comment.JPG"));
        assertThat(e.getMessage()).startsWith("Error when reading metadata for");
    }

    @Test
    void testReadExifImageMetadataWithIOException() throws Exception {
        var provider = new ImageServiceProvider();
        var imageUrl = "http://localhost/image.jpg";
        var builder = ImageMetadata.with();
        var jpegSegment = mock(JPEGSegment.class);
        var exifData = mock(InputStream.class);
        when(exifData.read()).thenThrow(IOException.class);
        when(jpegSegment.data()).thenReturn(exifData);
        var exifSegment = Collections.singletonList(jpegSegment);

        var e = assertThrows(RuntimeException.class, () -> provider.readExifImageMetadata(imageUrl, builder, exifSegment));
        assertThat(e.getMessage()).startsWith("Error reading EXIF data of");
    }

    @Test
    void testExtractExifDatetimeWithParseException() {
        var provider = new ImageServiceProvider();
        var builder = ImageMetadata.with();
        var entry = mock(Entry.class);
        when(entry.getValueAsString()).thenReturn("not a parsable date");
        var imageUrl = "http://localhost/image.jpg";

        var e = assertThrows(RuntimeException.class, () -> provider.extractExifDatetime(builder, entry, imageUrl));
        assertThat(e.getMessage()).startsWith("Error parsing EXIF 306/DateTime entry of");
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
