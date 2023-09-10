import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const contentTypeReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.contentType,
});

export default contentTypeReducer;
