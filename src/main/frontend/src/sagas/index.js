import { fork, all } from "redux-saga/effects";
import metadataSaga from './metadataSaga';
import pluginrescanSaga from './pluginrescanSaga';

export default function* rootSaga() {
    yield all([
        fork(metadataSaga),
        fork(pluginrescanSaga),
    ]);
}
