import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_FAILURE,
    IMAGEIO_SCAN_FOR_PLUGINS_FAILURE,
} from '../reduxactions';

const apiErrorsReducer = createReducer(0, {
    [IMAGE_METADATA_FAILURE]: (state, action) => ({ ...state, increment: action.payload }),
    [IMAGEIO_SCAN_FOR_PLUGINS_FAILURE]: (state, action) => ({ ...state, decrement: action.payload }),
});

export default apiErrorsReducer;
