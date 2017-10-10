package org.openstreetmap.atlas.tags.annotations.validation;

import java.io.Serializable;

/**
 * Checks if the value of a tag has at least one non-whitespace character
 *
 * @author cstaylor
 */
public class NonEmptyStringValidator implements TagValidator, Serializable
{
    private static final long serialVersionUID = -1940458043573529280L;

    @Override
    public boolean isValid(final String value)
    {
        return value.trim().length() > 0;
    }
}
