package no.priv.bang.demos.frontendkarafdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ImageServiceProviderTest {

    @Test
    void testSForImageioPlugins() {
        var provider = new ImageServiceProvider();
        var jpegreaders = provider.scanForImageioPlugins();
        assertThat(jpegreaders).isNotEmpty().contains("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader");
    }

}
