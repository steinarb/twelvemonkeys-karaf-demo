import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGEIO_SCAN_FOR_PLUGINS_RECEIVE,
} from '../reduxactions';

const jpegreadersReducer = createReducer([], {
    [IMAGEIO_SCAN_FOR_PLUGINS_RECEIVE]: (state, action) => action.payload,
});

export default jpegreadersReducer;
