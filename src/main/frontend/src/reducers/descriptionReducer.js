import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const descriptionReducer = createReducer('', {
    [IMAGE_METADATA_RECEIVE]: (state, action) => action.payload.description || '',
});

export default descriptionReducer;
