/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.geolabel.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class GeoLabelClientV1IT {

    private String gvqExample = "http://schemas.geoviqua.org/GVQ/4.0/example_documents/PQM_UQM_combined/DigitalClimaticAtlas_mt_an_v10.xml";

    @Test
    public void testCreateGeoLabelMetadataStream() throws IOException {
        InputStream metadataStream = getClass().getResourceAsStream("FAO_GEO_Network_iso19139.xml");
        InputStream svg = GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(metadataStream).getSVG();
        String svgString = IOUtils.toString(svg);

        assertTrue(svgString.contains("<title>Standards Compliance. Standard name: ISO 19115:2003/19139, version 1.0.</title>"));
        assertTrue("ensure has no feedback info", svgString.contains("<title>User Feedback</title>"));
    }

    @Test
    public void testCreateGeoLabelMetadataAndFeedbackStream() throws IOException {
        InputStream metadataStream = getClass().getResourceAsStream("FAO_GEO_Network_iso19139.xml");
        InputStream feedbackStream = getClass().getResourceAsStream("GVQ_Feedback_All_Available.xml");

        InputStream svg = GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(metadataStream).setFeedbackDocument(feedbackStream).getSVG();
        String svgString = IOUtils.toString(svg);

        assertTrue(svgString.contains("<title>User Feedback. Number of feedbacks 3. Average rating: 3 (2 ratings).</title>"));
    }

    @Test
    public void testCreateGeoLabelMetadataUrl() throws IOException {
        InputStream svg = GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(this.gvqExample).getSVG();
        String svgString = IOUtils.toString(svg);

        assertTrue("organization", svgString.contains("Animal and Plant Biology and Ecology Department"));
        assertTrue(svgString.contains("<title>Standards Compliance. Standard name: ISO 19115:2003/19139, version 1.0.</title>"));
        assertTrue("ensure has no feedback info", svgString.contains("<title>User Feedback</title>"));
    }

    @Test
    public void testCreateGeoLabelMetadataUrlAndFeedbackStream() throws IOException {
        InputStream feedbackStream = getClass().getResourceAsStream("GVQ_Feedback_All_Available.xml");

        InputStream svg = GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(this.gvqExample).setFeedbackDocument(feedbackStream).getSVG();
        String svgString = IOUtils.toString(svg);

        assertTrue(svgString.contains("<title>User Feedback. Number of feedbacks 3. Average rating: 3 (2 ratings).</title>"));
    }

    @Test
    public void testCreateGeoLabelMetadataUrlCache() throws IOException {
        GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(this.gvqExample).setUseCache(true).getSVG();

        assertTrue(GeoLabelCache.hasSVG(this.gvqExample));

        InputStream svg = GeoLabelClientV1.createGeoLabelRequest().setMetadataDocument(this.gvqExample).setUseCache(true).getSVG();
        String svgString = IOUtils.toString(svg);

        assertTrue("organization", svgString.contains("Animal and Plant Biology and Ecology Department"));
        assertTrue(svgString.contains("<title>Standards Compliance. Standard name: ISO 19115:2003/19139, version 1.0.</title>"));
        assertTrue("ensure has no feedback info", svgString.contains("<title>User Feedback</title>"));
    }

    @Test
    public void testCreateGeoLabelNoContent() {
        try {
            GeoLabelClientV1.createGeoLabelRequest().getSVG();
            fail("Server should send error code");
        }
        catch (IOException e) {
            assertTrue(e.getMessage().contains("Bad Request"));
        }

    }
}
