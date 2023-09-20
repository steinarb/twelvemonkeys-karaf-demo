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
    const comment = useSelector(state => state.comment);
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
            <p>Comment: <input id="comment" type="text" value={comment} /></p>
        </div>
    );
}
