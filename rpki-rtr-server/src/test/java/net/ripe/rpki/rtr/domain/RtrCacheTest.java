/**
 * The BSD License
 *
 * Copyright (c) 2010-2018 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.rpki.rtr.domain;

import net.ripe.ipresource.Asn;
import net.ripe.ipresource.IpRange;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RtrCacheTest {

    private static final Set<RtrDataUnit> EMPTY_ANNOUNCEMENTS = Collections.emptySet();
    private static final Set<RtrDataUnit> EMPTY_WITHDRAWALS = Collections.emptySet();
    private static final Set<RtrDataUnit> SINGLE_ANNOUNCEMENT = Collections.singleton(RtrDataUnit.prefix(Asn.parse("AS3333"), IpRange.parse("127.0.0.0/8"), 14));
    private static final Set<RtrDataUnit> SINGLE_WITHDRAWAL = Collections.singleton(RtrDataUnit.prefix(Asn.parse("AS3333"), IpRange.parse("127.0.0.0/8"), 14));

    private RtrCache subject = new RtrCache();

    @Test
    public void should_increase_serial_when_valid_pdus_change() {
        assertThat(subject.getSerialNumber()).isEqualTo(0);
        subject.updateValidatedPdus(SINGLE_ANNOUNCEMENT);
        assertThat(subject.getSerialNumber()).isEqualTo(1);
        subject.updateValidatedPdus(EMPTY_ANNOUNCEMENTS);
        assertThat(subject.getSerialNumber()).isEqualTo(2);
    }

    @Test
    public void should_not_increase_serial_when_there_are_no_valid_pdu_changes() {
        assertThat(subject.getSerialNumber()).isEqualTo(0);
        subject.updateValidatedPdus(SINGLE_ANNOUNCEMENT);
        assertThat(subject.getSerialNumber()).isEqualTo(1);
        subject.updateValidatedPdus(SINGLE_ANNOUNCEMENT);
        assertThat(subject.getSerialNumber()).isEqualTo(1);
    }

    @Test
    public void should_add_delta_when_valid_pdus_change() {
        subject.updateValidatedPdus(SINGLE_ANNOUNCEMENT);

        Optional<VersionedSet<RtrDataUnit>.Delta> maybeDelta0_1 = subject.getDeltaFrom(0);
        assertThat(maybeDelta0_1).isPresent();
        VersionedSet<RtrDataUnit>.Delta delta0_1 = maybeDelta0_1.get();
        assertThat(delta0_1.getAdditions()).isEqualTo(SINGLE_ANNOUNCEMENT);
        assertThat(delta0_1.getRemovals()).isEqualTo(EMPTY_WITHDRAWALS);

        subject.updateValidatedPdus(EMPTY_ANNOUNCEMENTS);

        Optional<VersionedSet<RtrDataUnit>.Delta> maybeDelta1_2 = subject.getDeltaFrom(1);
        assertThat(maybeDelta1_2).isPresent();
        VersionedSet<RtrDataUnit>.Delta delta1_2 = maybeDelta1_2.get();
        assertThat(delta1_2.getAdditions()).isEqualTo(EMPTY_ANNOUNCEMENTS);
        assertThat(delta1_2.getRemovals()).isEqualTo(SINGLE_WITHDRAWAL);

        Optional<VersionedSet<RtrDataUnit>.Delta> maybeDelta0_2 = subject.getDeltaFrom(0);
        assertThat(maybeDelta0_2).isPresent();
        VersionedSet<RtrDataUnit>.Delta delta0_2 = maybeDelta0_2.get();
        assertThat(delta0_2.getAdditions()).isEqualTo(EMPTY_ANNOUNCEMENTS);
        assertThat(delta0_2.getRemovals()).isEqualTo(EMPTY_WITHDRAWALS);
    }
}