import { createReducer } from '@reduxjs/toolkit';
import {
    MODIFY_IMAGE_URL,
} from '../reduxactions';

const imageUrlReducer = createReducer('', {
    [MODIFY_IMAGE_URL]: (state, action) => action.payload,
});

export default imageUrlReducer;
