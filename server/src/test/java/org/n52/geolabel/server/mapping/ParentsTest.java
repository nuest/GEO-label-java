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
package org.n52.geolabel.server.mapping;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.n52.geolabel.commons.Label;
import org.n52.geolabel.commons.LabelFacet.Availability;
import org.n52.geolabel.server.config.GeoLabelObjectMapper;
import org.n52.geolabel.server.config.TransformationDescriptionLoader;
import org.n52.geolabel.server.config.TransformationDescriptionResources;

public class ParentsTest {

    private MetadataTransformer transformer;

    @Before
    public void newMetadataTransformer() {
        TransformationDescriptionResources res = new TransformationDescriptionResources("http://not.available/1=/transformations/transformerSOS20.json,http://not.available/2=/transformations/transformerSML10.json");
        this.transformer = new MetadataTransformer(new TransformationDescriptionLoader(res,
                                                                                       new GeoLabelObjectMapper(res),
                                                                                       true));
    }

    @Test
    public void manualLabel() throws IOException {
        Label l = new Label();
        l.getCitationsFacet().updateAvailability(Availability.AVAILABLE_HIGHER);
        l.getLineageFacet().updateAvailability(Availability.AVAILABLE_HIGHER);
        l.getProducerProfileFacet().updateAvailability(Availability.AVAILABLE_HIGHER);
        l.getStandardsComplianceFacet().updateAvailability(Availability.AVAILABLE_HIGHER);

        File f = File.createTempFile("geolabel-parent-test-", ".svg");
        FileWriter fr = new FileWriter(f);
        l.toSVG(fr, "test", 200);
        System.out.println("Saved test file to " + f.getAbsolutePath());
    }

    private void testLabel(Label label) {
        assertThat("standards compliance is found",
                   label.getStandardsComplianceFacet().getAvailability(),
                   equalTo(Availability.AVAILABLE));
        // TODO when using XPath 2.0 we can do string concatenation of arbitrary result length and insert the
        // actual versions.
        // assertThat("standards compliance title contains versions",
        // label.getStandardsComplianceFacet().getTitle(),
        // allOf(containsString("1.0.0"), containsString("2.0.0")));
        assertThat("standards compliance title contains version number",
                   label.getStandardsComplianceFacet().getTitle(),
                   containsString("versions: 2"));
        assertThat("standards compliance title contains standard name",
                   label.getStandardsComplianceFacet().getTitle(),
                   containsString("OGC:SOS"));
        assertThat("producer profile contains name",
                   label.getProducerProfileFacet().getTitle(),
                   containsString("52North"));
        assertThat("producer profile contains url",
                   label.getProducerProfileFacet().getTitle(),
                   containsString("http://52north.org/swe"));
    }

    @Test
    public void testCapabilitiesWithCitation() throws IOException {
        Label label = testCapabilitiesDocument("sml/ws2500-with-history-and-processchain.xml",
                                               "sml/SOS20-capabilities_with-publication-and-issue.xml");

        testLabel(label);

        assertThat("label contains one discovered issue",
                   label.getProducerCommentsFacet().getTitle(),
                   allOf(containsString("1 known issues"), containsString("location is not optimal")));
        assertThat("label contains quality information on dataset level",
                   label.getQualityInformationFacet().getTitle(),
                   containsString("dataset level"));
        assertThat("label contains lineage information on dataset level",
                   label.getLineageFacet().getTitle(),
                   containsString("dataset level"));
    }

    private Label testCapabilitiesDocument(String metadata, String parent) throws MalformedURLException, IOException {
        InputStream metadataStream = getClass().getClassLoader().getResourceAsStream(metadata);
        InputStream parentMetadataStream = getClass().getClassLoader().getResourceAsStream(parent);

        Label l = new Label();
        l.setMetadataUrl(new URL("http://not.available.net"));
        l.setFeedbackUrl(new URL("http://not.available.net"));

        Label label = this.transformer.updateGeoLabel(metadataStream, l);
        label = this.transformer.updateGeoLabel(parentMetadataStream, label, true);
        return label;
    }

}
