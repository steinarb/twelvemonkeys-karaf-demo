import { takeLatest, call, put } from "redux-saga/effects";
import axios from "axios";
import {
    IMAGEIO_SCAN_FOR_PLUGINS_REQUEST,
    IMAGEIO_SCAN_FOR_PLUGINS_RECEIVE,
    IMAGEIO_SCAN_FOR_PLUGINS_FAILURE,
} from '../reduxactions';

function doPluginrescan() {
    return axios.get('/api/image/scanforplugins');
}

function* sendReceivePluginrescan() {
    try {
        const response = yield call(doPluginrescan);
        const jpegreaders = response.data;
        yield put(IMAGEIO_SCAN_FOR_PLUGINS_RECEIVE(jpegreaders));
    } catch (error) {
        yield put(IMAGEIO_SCAN_FOR_PLUGINS_FAILURE(error));
    }
}

export default function* pluginrescanSaga() {
    yield takeLatest(IMAGEIO_SCAN_FOR_PLUGINS_REQUEST, sendReceivePluginrescan);
}
