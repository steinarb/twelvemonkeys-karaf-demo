package no.priv.bang.demos.frontendkarafdemo;

import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;

public interface ImageService {

    ImageMetadata getMetadata(String imageUrl);

}
