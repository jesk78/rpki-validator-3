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
package net.ripe.rpki.validator3.api.roas;

import lombok.Value;
import net.ripe.ipresource.IpRange;
import net.ripe.rpki.commons.crypto.cms.roa.RoaCms;
import net.ripe.rpki.validator3.domain.RoaPrefix;

import java.util.stream.Stream;

@Value(staticConstructor = "of")
public class ValidatedPrefix {
    String prefix;
    Integer maximumLength;
    int effectiveMaximumLength;
    long asn;

    public static ValidatedPrefix of(RoaPrefix roaPrefix) {
        return of(
            roaPrefix.getPrefix(),
            roaPrefix.getMaximumLength(),
            roaPrefix.getMaximumLength() != null ? roaPrefix.getMaximumLength() : IpRange.parse(roaPrefix.getPrefix()).getPrefixLength(),
            roaPrefix.getAsn()
        );
    }

    public static Stream<ValidatedPrefix> of(RoaCms roaCms) {
        long asn = roaCms.getAsn().longValue();
        return roaCms.getPrefixes().stream()
            .map(prefix -> ValidatedPrefix.of(
                prefix.getPrefix().toString(),
                prefix.getMaximumLength(),
                prefix.getEffectiveMaximumLength(),
                asn
            ));
    }
}
