import { takeLatest, call, put, select } from "redux-saga/effects";
import axios from "axios";
import {
    IMAGE_METADATA_REQUEST,
    IMAGE_METADATA_RECEIVE,
    IMAGE_METADATA_FAILURE,
} from '../reduxactions';

function doMetadata(url) {
    return axios.post('/api/image/metadata', { url });
}

function* sendReceiveMetadata() {
    try {
        const url = yield select(state => state.imageUrl);
        const response = yield call(doMetadata, url);
        const metadata = response.data;
        yield put(IMAGE_METADATA_RECEIVE(metadata));
    } catch (error) {
        yield put(IMAGE_METADATA_FAILURE(error));
    }
}

export default function* metadataSaga() {
    yield takeLatest(IMAGE_METADATA_REQUEST, sendReceiveMetadata);
}
