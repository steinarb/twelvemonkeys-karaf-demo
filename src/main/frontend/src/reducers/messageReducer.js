import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const messageReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.message || '',
});

export default messageReducer;
