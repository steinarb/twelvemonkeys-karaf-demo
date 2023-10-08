import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router';
import apiErrors from './apiErrorsReducer';
import imageUrl from './imageUrlReducer';
import status from './statusReducer';
import lastModified from './lastModifiedReducer';
import contentType from './contentTypeReducer';
import contentLength from './contentLengthReducer';
import description from './descriptionReducer';

export default (history) => combineReducers({
    router: connectRouter(history),
    apiErrors,
    imageUrl,
    status,
    lastModified,
    contentType,
    contentLength,
    description,
});
