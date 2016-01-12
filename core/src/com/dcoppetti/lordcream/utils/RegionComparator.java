package com.dcoppetti.lordcream.utils;

import java.util.Comparator;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * @author Diego Coppetti
 *
 */
public class RegionComparator implements Comparator<AtlasRegion> {

	@Override
	public int compare(AtlasRegion region1, AtlasRegion region2) {
		return region1.name.compareTo(region2.name);
	}

}
