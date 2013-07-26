/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.headers;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test for the {@code StaticHeadersWriter}
 *
 * @author Marten Deinum
 * @author Rob Winch
 * @since 3.2
 */
public class StaticHeaderWriterTests {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeaderName() {
        new StaticHeadersWriter(null, "value1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeaderValues() {
        new StaticHeadersWriter("name", (String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorContainsNullHeaderValue() {
        new StaticHeadersWriter("name", "value1", null);
    }

    @Test
    public void sameHeaderShouldBeReturned() {
        String headerName = "X-header";
        String headerValue = "foo";
        StaticHeadersWriter factory = new StaticHeadersWriter(headerName, headerValue);

        factory.writeHeaders(request, response);
        assertThat(response.getHeaderValues(headerName)).isEqualTo(Arrays.asList(headerValue));
    }
}