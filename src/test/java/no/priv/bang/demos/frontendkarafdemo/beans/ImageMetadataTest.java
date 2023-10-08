/*
 * Copyright 2020-2023 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.demos.frontendkarafdemo.beans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class ImageMetadataTest {

    @Test
    void testCreate() {
        int status = 200;
        Date lastModified = new Date();
        String contentType = "image/jpeg";
        int contentLength = 128186;
        var description = "This is a description";
        ImageMetadata bean = ImageMetadata.with()
            .status(status)
            .lastModified(lastModified)
            .contentType(contentType)
            .contentLength(contentLength)
            .description(description)
            .build();
        assertEquals(status, bean.getStatus());
        assertEquals(lastModified, bean.getLastModified());
        assertEquals(contentType, bean.getContentType());
        assertEquals(contentLength, bean.getContentLength());
        assertThat(bean.getDescription()).isEqualTo(description);
    }

    @Test
    void testCreateNoArgsConstructor() {
        ImageMetadata bean = ImageMetadata.with().build();
        assertEquals(0, bean.getStatus());
        assertNull(bean.getLastModified());
        assertNull(bean.getContentType());
        assertEquals(0, bean.getContentLength());
    }

}
