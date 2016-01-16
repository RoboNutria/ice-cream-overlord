package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.entities.Cone;
import com.dcoppetti.lordcream.utils.Assets;

public class BulletFactory {
	
	public static enum BulletType {
		Cone,
		EnemyBullet
	}
	
	public static void createPlayerBullet(BulletType type, World world, Vector2 position, Vector2 velocity) {
		// TODO Performance: We could allocate the regions to not get them and sort each time
		if(type == BulletType.Cone) {
			Array<TextureRegion> regions = Assets.getAtlasRegions(IceCreamOverlordGame.SPRITES_PACK_FILE, "cone", "-", 1);
			Cone c = new Cone(type, regions.first(), world, velocity, position);
			c.setAnimationRegions(regions);
		}
	}

}
