import { createAction } from '@reduxjs/toolkit';

export const MODIFY_IMAGE_URL = createAction('MODIFY_IMAGE_URL');

export const IMAGE_METADATA_REQUEST = createAction('IMAGE_METADATA_REQUEST');
export const IMAGE_METADATA_RECEIVE = createAction('IMAGE_METADATA_RECEIVE');
export const IMAGE_METADATA_FAILURE = createAction('IMAGE_METADATA_FAILURE');
