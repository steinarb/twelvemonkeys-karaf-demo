package no.priv.bang.demos.frontendkarafdemo.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.*;

import no.priv.bang.demos.frontendkarafdemo.ImageService;
import no.priv.bang.demos.frontendkarafdemo.beans.ImageMetadata;
import no.priv.bang.demos.frontendkarafdemo.beans.ImageRequest;

@Path("image")
public class ImageResource {

    @Inject
    ImageService imageService;

    @POST
    @Path("metadata")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ImageMetadata getMetadata(ImageRequest imageRequest) {
        return imageService.getMetadata(imageRequest.getUrl());
    }

}
