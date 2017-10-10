package org.openstreetmap.atlas.tags.annotations.validation;

import java.io.Serializable;

/**
 * Does no checking: only used for permitting any kind of values in a tag
 *
 * @author cstaylor
 */
public class NoneValidator implements TagValidator, Serializable
{
    private static final long serialVersionUID = 4792657965616648005L;

    @Override
    public boolean isValid(final String value)
    {
        return true;
    }
}
