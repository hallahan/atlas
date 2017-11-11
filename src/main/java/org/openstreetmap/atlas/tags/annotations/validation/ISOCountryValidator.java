package org.openstreetmap.atlas.tags.annotations.validation;

import java.io.Serializable;

import org.openstreetmap.atlas.locale.IsoCountry;

/**
 * Checks if the value of a tag matches an ISO2 or ISO3 country code
 *
 * @author cstaylor
 */
public class ISOCountryValidator implements TagValidator, Serializable
{
    private static final long serialVersionUID = -4177082128188664186L;

    @Override
    public boolean isValid(final String value)
    {
        return IsoCountry.isValidCountryCode(value);
    }
}
