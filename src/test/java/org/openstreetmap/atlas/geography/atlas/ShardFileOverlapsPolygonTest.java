package org.openstreetmap.atlas.geography.atlas;

import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.Polygon;
import org.openstreetmap.atlas.geography.Rectangle;
import org.openstreetmap.atlas.geography.sharding.DynamicTileSharding;
import org.openstreetmap.atlas.streaming.resource.File;
import org.openstreetmap.atlas.streaming.resource.Resource;
import org.openstreetmap.atlas.streaming.resource.StringResource;

/**
 * Unit test for ShardFileOverlapsPolygon atlas resource predicate.
 *
 * @author rmegraw
 */
public class ShardFileOverlapsPolygonTest
{
    private static final DynamicTileSharding SHARDING_TREE = new DynamicTileSharding(new File(
            ShardFileOverlapsPolygonTest.class.getResource("tree-6-14-100000.txt").getFile()));

    private static final Polygon POLYGON = Rectangle.forCorners(
            Location.forString("55.5868837,12.3541246"), Location.forString("55.752623,12.71942"));

    private static final Predicate<Resource> PREDICATE = new ShardFileOverlapsPolygon(SHARDING_TREE,
            POLYGON);

    @Test
    public void testFilenameFormat()
    {
        // variations on valid filename
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1095-641.atlas")));
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1095-641")));

        // some filenames that aren't formatted properly
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_11_1095_641.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_11_1095_641.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/11-1095-641.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_11-1095-641.atl.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_110-1095-641.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_11-1095-641.atlas.gzip")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/foo")));
        Assert.assertFalse(PREDICATE.test(new File("")));
    }

    @Test
    public void testNotAFile()
    {
        // resources that are not files
        Assert.assertFalse(
                PREDICATE.test(new StringResource("/some/path/DNK_11-1095-641.atlas.gz")));
    }

    @Test
    public void testShardsInBounds()
    {
        // shards that should overlap a polygon in Copehagen
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1095-641.atlas.gz")));
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1094-641.atlas.gz")));
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1095-640.atlas.gz")));
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_11-1094-640.atlas.gz")));
        Assert.assertTrue(PREDICATE.test(new File("/some/path/DNK_10-548-320.atlas.gz")));
    }

    @Test
    public void testShardsOutOfBounds()
    {
        // shards that should not overlap a polygon in Copenhagen
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-546-319.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-547-319.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-548-319.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-546-320.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-546-321.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-547-321.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-548-321.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-546-321.atlas.gz")));
        Assert.assertFalse(PREDICATE.test(new File("/some/path/DNK_10-548-321.atlas.gz")));
    }

}
