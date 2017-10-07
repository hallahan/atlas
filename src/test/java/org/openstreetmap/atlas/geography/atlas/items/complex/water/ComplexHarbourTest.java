package org.openstreetmap.atlas.geography.atlas.items.complex.water;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.complex.Finder;
import org.openstreetmap.atlas.geography.atlas.items.complex.waters.ComplexWaterEntity;
import org.openstreetmap.atlas.geography.atlas.items.complex.waters.ComplexWaterEntityFinder;
import org.openstreetmap.atlas.geography.atlas.items.complex.waters.WaterType;

import com.google.common.collect.Iterables;

/**
 * Tests {@link ComplexWaterEntity} of {@link WaterType#HARBOUR} creation.
 *
 * @author mgostintsev
 */
public class ComplexHarbourTest
{
    @Rule
    public final ComplexHarborTestRule rule = new ComplexHarborTestRule();

    @Test
    public void testHarbourFromArea()
    {
        // TODO
    }

    @Test
    public void testHarbourFromRelation()
    {
        final Atlas harborAsRelation = this.rule.getHarborAsRelationAtlas();
        final Iterable<ComplexWaterEntity> waterEntities = new ComplexWaterEntityFinder()
                .find(harborAsRelation, Finder::ignore);

        for (final ComplexWaterEntity entity : waterEntities)
        {
            System.out.println(entity.toString());
        }
        Assert.assertTrue(Iterables.size(waterEntities) == 1);
        Assert.assertTrue(waterEntities.iterator().next().getWaterType().equals(WaterType.HARBOUR));
    }
}
