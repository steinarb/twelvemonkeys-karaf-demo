import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    MODIFY_IMAGE_URL,
    IMAGE_METADATA_REQUEST,
} from '../reduxactions';


export default function Metadata() {
    const imageUrl = useSelector(state => state.imageUrl);
    const status = useSelector(state => state.status);
    const lastModified = useSelector(state => state.lastModified);
    const contentType = useSelector(state => state.contentType);
    const contentLength = useSelector(state => state.contentLength);
    const description = useSelector(state => state.description);
    const dispatch = useDispatch();

    return (
        <div>
            <h1>Get image metadata</h1>
            <p>
                Image URL:
                <input id="imageUrl" type="text" value={imageUrl} onChange={e => dispatch(MODIFY_IMAGE_URL(e.target.value))} />
            </p>
            <p>
                <button onClick={() => dispatch(IMAGE_METADATA_REQUEST())}>Get metadata</button>
            </p>
            <p>Status code: <input id="status" type="text" value={status} /></p>
            <p>Last modified: <input id="lastModified" type="text" value={lastModified} /></p>
            <p>Content type: <input id="contentType" type="text" value={contentType} /></p>
            <p>Content length: <input id="contentLength" type="text" value={contentLength} /></p>
            <p>Description: <input id="description" type="text" value={description} /></p>
            <h1>Some sample images</h1>
            <ol>
                <li><a href="https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg">A 1996 vintage JFIF without EXIM metadata and with a comment in the metadata (TwelveMonkeys finds the comment) (created by cjpeg)</a></li>
                <li><a href="https://www.bang.priv.no/bilder/test/CIMG0068.JPG">A picture taken by a Casio Exilim II digital camera in October 2005 (autumn leaves at Gålå)</a></li>
                <li><a href="https://www.bang.priv.no/bilder/test/CIMG0068_with_description.JPG">The Gålå autumn picture with an ImageDescription tag added by exiftool (TwelveMonkeys EXIFReader finds the tag)</a></li>
                <li><a href="https://www.bang.priv.no/bilder/test/CIMG0068_with_description_and_title.JPG">The Gålå autumn picture with an ImageDescription and Title tag added by exiftool (this is the tag that caused this issue)</a></li>
                <li><a href="https://www.bang.priv.no/bilder/test/CIMG0068_with_description_and_user_comment.JPG">The Gålå autumn picture with an ImageDescription and UserComment tag added by exiftool (I still haven&apos;t found the UserComment tag with TwelveMonkeys but with your comments above I have hopes I eventually will. I think I found this in the source code...?)</a></li>
            </ol>
        </div>
    );
}
