/*
 * Copyright 2020-2024 Steinar Bang
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
        var title = "A fine picture";
        var description = "This is a description";
        ImageMetadata bean = ImageMetadata.with()
            .status(status)
            .lastModified(lastModified)
            .contentType(contentType)
            .contentLength(contentLength)
            .title(title)
            .description(description)
            .build();
        assertEquals(status, bean.status());
        assertEquals(lastModified, bean.lastModified());
        assertEquals(contentType, bean.contentType());
        assertEquals(contentLength, bean.contentLength());
        assertThat(bean.title()).isEqualTo(title);
        assertThat(bean.description()).isEqualTo(description);
    }

    @Test
    void testCreateNoArgsConstructor() {
        ImageMetadata bean = ImageMetadata.with().build();
        assertEquals(0, bean.status());
        assertNull(bean.lastModified());
        assertNull(bean.contentType());
        assertEquals(0, bean.contentLength());
        assertNull(bean.title());
        assertNull(bean.description());
    }

}
