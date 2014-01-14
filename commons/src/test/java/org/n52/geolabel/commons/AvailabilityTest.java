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

package org.n52.geolabel.commons;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.geolabel.commons.LabelFacet.Availability;

public class AvailabilityTest {

    @SuppressWarnings("boxing")
    @Test
    public void testSmallerThan() {
        assertThat("NA is smaller than NOT_AVAILABLE",
                   Availability.NA.compareTo(Availability.NOT_AVAILABLE),
                   is(equalTo( -1)));

        assertThat("NA is smaller than AVAILABLE_HIGER",
                   Availability.NA.compareTo(Availability.AVAILABLE_HIGHER),
                   lessThan(0));

        assertThat("NOT_AVAILABLE is smaller than AVAILABLE_HIGHER",
                   Availability.NOT_AVAILABLE.compareTo(Availability.AVAILABLE_HIGHER),
                   lessThan(0));
        assertThat("NA is smaller than AVAILABLE_HIGER",
                   Availability.NA.compareTo(Availability.AVAILABLE_HIGHER),
                   lessThan(0));

        assertThat("AVAILABLE_HIGHER is smaller than AVAILABLE",
                   Availability.AVAILABLE_HIGHER.compareTo(Availability.AVAILABLE),
                   lessThan(0));
    }

    @SuppressWarnings("boxing")
    @Test
    public void testLargerThan() {
        assertThat(Availability.NOT_AVAILABLE.compareTo(Availability.NA), is(equalTo(1)));
        assertThat(Availability.AVAILABLE_HIGHER.compareTo(Availability.NA), greaterThan(0));
        assertThat(Availability.AVAILABLE_HIGHER.compareTo(Availability.NOT_AVAILABLE), greaterThan(0));
        assertThat(Availability.AVAILABLE.compareTo(Availability.NOT_AVAILABLE), greaterThan(0));
        assertThat(Availability.AVAILABLE.compareTo(Availability.AVAILABLE_HIGHER), greaterThan(0));
    }

    @Test
    public void testLabelUpdate() {
        Label l = new Label();
        l.getCitationsFacet().updateAvailability(Availability.AVAILABLE);
        l.getUserFeedbackFacet().updateAvailability(Availability.NOT_AVAILABLE);
        l.getExpertFeedbackFacet().updateAvailability(Availability.NA);
        l.getProducerCommentsFacet().updateAvailability(Availability.NA);
        l.getQualityInformationFacet().updateAvailability(Availability.AVAILABLE);
        l.getStandardsComplianceFacet().updateAvailability(Availability.NA);
        l.getLineageFacet().updateAvailability(Availability.NA);

        Label parent = new Label();
        parent.getUserFeedbackFacet().updateAvailability(Availability.AVAILABLE);
        parent.getProducerCommentsFacet().updateAvailability(Availability.NOT_AVAILABLE);
        parent.getQualityInformationFacet().updateAvailability(Availability.NA);
        // parent.getLineageFacet().updateAvailability(Availability.AVAILABLE);
        parent.getStandardsComplianceFacet().updateAvailability(Availability.AVAILABLE);

        l.parentUpdate(parent);

        assertEquals(Availability.AVAILABLE, l.getCitationsFacet().getAvailability());
        assertEquals(Availability.AVAILABLE_HIGHER, l.getUserFeedbackFacet().getAvailability());
        assertEquals(Availability.NA, l.getExpertFeedbackFacet().getAvailability());
        assertEquals(Availability.AVAILABLE, l.getCitationsFacet().getAvailability());
        assertEquals(Availability.NA, l.getProducerCommentsFacet().getAvailability());
        assertEquals(Availability.NA, l.getLineageFacet().getAvailability());
        assertEquals(Availability.AVAILABLE, l.getQualityInformationFacet().getAvailability());
    }
}
