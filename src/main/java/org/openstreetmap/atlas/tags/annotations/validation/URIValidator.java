package org.openstreetmap.atlas.tags.annotations.validation;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Validator for verifying a String follows the proper URI syntax
 *
 * @author cstaylor
 */
public class URIValidator extends ExactMatchValidator
{
    private static final long serialVersionUID = 2733528204999294769L;

    @Override
    public boolean isValid(final String value)
    {
        if (super.isValid(value))
        {
            return true;
        }
        try
        {
            new URI(value);
            return true;
        }
        catch (final URISyntaxException oops)
        {
            return false;
        }
    }
}
