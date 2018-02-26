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
package net.ripe.rpki.validator3.api;

import lombok.Data;
import net.ripe.rpki.validator3.domain.RpkiObjects;

import java.util.Comparator;
import java.util.Locale;

@Data(staticConstructor = "of")
public class Sorting {
    final By by;
    final Direction direction;

    public static Sorting parse(String sortBy, String sortDirection) {
        try {
            By by = By.valueOf(sortBy.toUpperCase(Locale.ROOT));
            Direction direction = Direction.valueOf(sortDirection.toUpperCase(Locale.ROOT));
            return of(by, direction);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Comparator<? super RpkiObjects.RoaPrefix> comparator() {
        Comparator<RpkiObjects.RoaPrefix> columns;
        switch (by) {
            case PREFIX:
                columns = Comparator.comparing(RpkiObjects.RoaPrefix::getPrefix)
                    .thenComparingInt(RpkiObjects.RoaPrefix::getLength)
                    .thenComparing(RpkiObjects.RoaPrefix::getAsn)
                    .thenComparing(RpkiObjects.RoaPrefix::getTrustAnchor);
                break;
            case ASN:
                columns = Comparator.comparing(RpkiObjects.RoaPrefix::getAsn)
                    .thenComparing(RpkiObjects.RoaPrefix::getPrefix)
                    .thenComparing(RpkiObjects.RoaPrefix::getTrustAnchor);
                break;
            case LOCATION:
                columns = Comparator.comparing(RpkiObjects.RoaPrefix::getUri)
                    .thenComparing(RpkiObjects.RoaPrefix::getTrustAnchor);
                break;
            case TA:
            default:
                columns = Comparator.comparing(RpkiObjects.RoaPrefix::getTrustAnchor)
                    .thenComparing(RpkiObjects.RoaPrefix::getAsn)
                    .thenComparing(RpkiObjects.RoaPrefix::getPrefix);
                break;
        }
        return direction == Direction.DESC ? columns.reversed() : columns;
    }

    public enum Direction {
        ASC, DESC
    }

    public enum By {
        PREFIX, ASN, TA, KEY, LOCATION, STATUS
    }
}
