import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const commentReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.comment || '',
});

export default commentReducer;
