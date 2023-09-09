package no.priv.bang.demos.frontendkarafdemo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadataNode;

import org.osgi.service.component.annotations.Component;
import org.w3c.dom.NodeList;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;

@Component
public class ImageServiceProvider implements ImageService {

    private HttpConnectionFactory connectionFactory;

    @Override
    public ImageMetadata getMetadata(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String comment = null;
                var connection = getConnectionFactory().connect(imageUrl);
                connection.setRequestMethod("GET");
                try(var input = ImageIO.createImageInputStream(connection.getInputStream())) {
                    var readers = ImageIO.getImageReaders(input);
                    if (readers.hasNext()) {
                        var reader = readers.next();
                        reader.setInput(input, true);
                        var metadata = reader.getImageMetadata(0);
                        comment = StreamSupport.stream(iterable(metadata.getAsTree("javax_imageio_1.0").getChildNodes()).spliterator(), false)
                            .filter(n -> "Text".equals(n.getNodeName()))
                            .findFirst()
                            .flatMap(n -> StreamSupport.stream(iterable(n.getChildNodes()).spliterator(), false).findFirst())
                            .map(n -> n.getAttribute("value")).orElse(null);
                    }
                } catch (IOException e) {
                    new RuntimeException(String.format("Error when reading image metadata for %s",  imageUrl), e);
                }

                return ImageMetadata.with()
                    .status(connection.getResponseCode())
                    .lastModified(new Date(connection.getHeaderFieldDate("Last-Modified", 0)))
                    .contentType(connection.getContentType())
                    .contentLength(getAndParseContentLengthHeader(connection))
                    .comment(comment)
                    .build();
            } catch (IOException e) {
                new RuntimeException(String.format("Error when reading metadata for %s",  imageUrl), e);
            }
        }
        return null;
    }

    private int getAndParseContentLengthHeader(HttpURLConnection connection) {
        String contentLengthHeader = connection.getHeaderField("Content-Length");
        return contentLengthHeader != null ? Integer.parseInt(contentLengthHeader) : 0;
    }

    private HttpConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new HttpConnectionFactory() {

                    @Override
                    public HttpURLConnection connect(String url) throws IOException {
                        return (HttpURLConnection) new URL(url).openConnection();
                    }
                };
        }
        return connectionFactory;
    }

    public static Iterable<IIOMetadataNode> iterable(final NodeList nodeList) {
        return () -> new Iterator<IIOMetadataNode>() {

                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < nodeList.getLength();
                }

                @Override
                public IIOMetadataNode next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    return (IIOMetadataNode) nodeList.item(index++);
                }
            };
    }

}