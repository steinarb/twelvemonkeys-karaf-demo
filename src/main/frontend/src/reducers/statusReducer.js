import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_RECEIVE,
} from '../reduxactions';

const statusReducer = createReducer(0, {
    [IMAGE_METADATA_RECEIVE]: (state, action) => parseInt(action.payload.status) || 0,
});

export default statusReducer;
