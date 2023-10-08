import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const titleReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.title || '',
});

export default titleReducer;
