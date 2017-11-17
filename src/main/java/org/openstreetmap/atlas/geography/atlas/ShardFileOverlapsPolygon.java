package org.openstreetmap.atlas.geography.atlas;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.openstreetmap.atlas.geography.Polygon;
import org.openstreetmap.atlas.geography.sharding.DynamicTileSharding;
import org.openstreetmap.atlas.streaming.resource.File;
import org.openstreetmap.atlas.streaming.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Predicate that uses a sharding tree to determine whether a given atlas shard file is within a
 * given Polygon. It depends on shard files following the naming convention of [name]_[zoom]-[x]-
 * [y].atlas.gz. The .atlas and .gz extensions are both optional. For example,
 * DNK_9-272-162.atlas.gz, DNK_9-272-162.atlas, and DNK_9-272-162 all pass the predicate.
 *
 * @author rmegraw
 */
public class ShardFileOverlapsPolygon implements Predicate<Resource>
{
    private static final Logger logger = LoggerFactory.getLogger(ShardFileOverlapsPolygon.class);

    /**
     * Matches shard filenames such as DNK_9-272-162.atlas.gz, DNK_9-272-162.atlas, DNK_9-272-162
     */
    private static final Pattern shardBaseFilenamePattern = Pattern
            .compile("^.+_(\\d{1,2}-\\d+-\\d+)(\\.atlas)?(\\.gz)?$");

    private final Set<String> shardsOverlappingPolygon;

    public ShardFileOverlapsPolygon(final DynamicTileSharding shardingTree, final Polygon bounds)
    {
        this.shardsOverlappingPolygon = new HashSet<>();
        shardingTree.shards(bounds)
                .forEach(shard -> this.shardsOverlappingPolygon.add(shard.getName()));
    }

    @Override
    public boolean test(final Resource resource)
    {
        boolean result = false;

        if (resource instanceof File)
        {
            final String filename = FilenameUtils.getName(((File) resource).getFile().getName());
            final Matcher matcher = shardBaseFilenamePattern.matcher(filename);
            if (matcher.find())
            {
                final String shardName = matcher.group(1);
                if (this.shardsOverlappingPolygon.contains(shardName))
                {
                    logger.debug("Resource {} overlaps polygon.", resource.getName());
                    result = true;
                }
                else
                {
                    logger.debug("Resource {} does not overlap polygon.", resource.getName());
                }
            }
            else
            {
                logger.debug("Resource {} does not match shard filename pattern.",
                        resource.getName());
            }
        }
        else
        {
            logger.debug("Resource {} is not a File.", resource.getName());
        }

        return result;
    }

}
