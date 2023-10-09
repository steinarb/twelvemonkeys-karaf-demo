package no.priv.bang.demos.frontendkarafdemo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.w3c.dom.NodeList;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;

import static com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil.*;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;
import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata.ImageMetadataBuilder;

@Component
public class ImageServiceProvider implements ImageService {

    static final int EXIF_DATETIME = 306;
    static final int EXIF_DESCRIPTION = 0x010e;
    static final int EXIF_EXIF = 34665;
    static final int EXIF_USER_COMMENT = 37510;
    private HttpConnectionFactory connectionFactory;
    private Logger logger;

    @Reference
    public void setLogservice(LogService logservice) {
        logger = logservice.getLogger(getClass());
    }

    @Override
    public ImageMetadata getMetadata(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return fetchImageWithHttpAndReadImageMetadata(imageUrl);
        }

        return null;
    }

    private ImageMetadata fetchImageWithHttpAndReadImageMetadata(String imageUrl) {
        try {
            final var metadataBuilder = ImageMetadata.with();
            var connection = getConnectionFactory().connect(imageUrl);
            connection.setRequestMethod("GET");
            readAndParseImageMetadata(imageUrl, metadataBuilder, connection);

            return metadataBuilder
                .status(connection.getResponseCode())
                .contentType(connection.getContentType())
                .contentLength(getAndParseContentLengthHeader(connection))
                .build();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error when reading metadata for %s",  imageUrl), e);
        }
    }

    private void readAndParseImageMetadata(String imageUrl, final ImageMetadataBuilder metadataBuilder, HttpURLConnection connection) {
        try(var input = ImageIO.createImageInputStream(connection.getInputStream())) {
            metadataBuilder.lastModified(new Date(connection.getLastModified()));
            var readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                var reader = readers.next();
                try {
                    logger.info("reader class: {}", reader.getClass().getCanonicalName());
                    reader.setInput(input, true);
                    var metadata = reader.getImageMetadata(0);
                    metadataBuilder.description(findJfifComment(metadata));
                } finally {
                    reader.dispose();
                }
            }
            var exifSegment = readSegments(input, JPEG.APP1, "Exif");
            readExifImageMetadata(imageUrl, metadataBuilder, exifSegment);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error when reading image metadata for %s",  imageUrl), e);
        }
    }

    void readExifImageMetadata(String imageUrl, final ImageMetadataBuilder metadataBuilder, List<JPEGSegment> exifSegment) throws IOException {
        exifSegment.stream().map(s -> s.data()).findFirst().ifPresent(exifData -> {
                try {
                    exifData.read();
                    var exif = (CompoundDirectory) new TIFFReader().read(ImageIO.createImageInputStream(exifData));
                    extractMetadataFromExifTags(metadataBuilder, exif, imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error reading EXIF data of %s",  imageUrl), e);
                }
            });
    }

    private void extractMetadataFromExifTags(final ImageMetadataBuilder metadataBuilder, CompoundDirectory exif, String imageUrl) {
        for (var entry : exif) {
            if (entry.getIdentifier().equals(EXIF_DATETIME)) {
                extractExifDatetime(metadataBuilder, entry, imageUrl);
            } else if (entry.getIdentifier().equals(EXIF_DESCRIPTION)) {
                metadataBuilder.title(entry.getValueAsString());
            } else if (entry.getIdentifier().equals(EXIF_EXIF)) {
                var nestedExif = (IFD) entry.getValue();
                for (var nestedEntry : nestedExif) {
                    if (nestedEntry.getIdentifier().equals(EXIF_USER_COMMENT)) {
                        var userCommentRaw = (byte[]) nestedEntry.getValue();
                        var splitUserComment = splitUserCommentInEncodingAndComment(userCommentRaw);
                        metadataBuilder.description(new String(splitUserComment.get(1), StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    void extractExifDatetime(final ImageMetadataBuilder metadataBuilder, Entry entry, String imageUrl) {
        try {
            var exifDateTimeFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            exifDateTimeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            var datetime = exifDateTimeFormat.parse(entry.getValueAsString());
            metadataBuilder.lastModified(datetime);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Error parsing EXIF 306/DateTime entry of %s",  imageUrl), e);
        }
    }

    private String findJfifComment(IIOMetadata metadata) {
        return StreamSupport.stream(iterable(metadata.getAsTree("javax_imageio_1.0").getChildNodes()).spliterator(), false)
            .filter(n -> "Text".equals(n.getNodeName()))
            .findFirst()
            .flatMap(n -> StreamSupport.stream(iterable(n.getChildNodes()).spliterator(), false).findFirst())
            .map(n -> n.getAttribute("value")).orElse(null);
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

    public void setConnectionFactory(HttpConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    List<byte[]> splitUserCommentInEncodingAndComment(byte[] userCommentRaw) {
        var encoding = Arrays.copyOf(userCommentRaw, 8);
        var comment = Arrays.copyOfRange(userCommentRaw, 8, userCommentRaw.length);
        return Arrays.asList(encoding, comment);
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
