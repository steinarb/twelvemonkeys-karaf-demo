import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const lastModifiedReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => new Date(action.payload.lastModified).toISOString(),
});

export default lastModifiedReducer;
