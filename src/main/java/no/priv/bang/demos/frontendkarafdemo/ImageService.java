package no.priv.bang.demos.frontendkarafdemo;

import java.util.List;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;

public interface ImageService {

    ImageMetadata getMetadata(String imageUrl);

    List<String> scanForImageioPlugins();

}
