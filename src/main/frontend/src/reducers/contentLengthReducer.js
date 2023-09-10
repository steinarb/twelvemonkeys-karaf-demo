import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const contentLengthReducer = createReducer(0, {
    [IMAGE_METADATA_RECEIVE]: (state, action) => parseInt(action.payload.contentLength) || 0,
});

export default contentLengthReducer;
