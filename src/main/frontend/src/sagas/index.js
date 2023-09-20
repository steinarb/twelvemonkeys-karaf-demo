import { fork, all } from "redux-saga/effects";
import metadataSaga from './metadataSaga';

export default function* rootSaga() {
    yield all([
        fork(metadataSaga),
    ]);
}
