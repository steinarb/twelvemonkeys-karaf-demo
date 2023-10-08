import { createReducer } from '@reduxjs/toolkit';
import {
    IMAGE_METADATA_FAILURE,
} from '../reduxactions';

const apiErrorsReducer = createReducer(0, {
    [IMAGE_METADATA_FAILURE]: (state, action) => ({ ...state, increment: action.payload }),
});

export default apiErrorsReducer;
