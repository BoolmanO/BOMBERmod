package content;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BombBulletType;
import mindustry.gen.Sounds;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.BlockFlag;

public class Content {
    public static UnitType abc;

    public static void load(){
        abc = new UnitType("abc"){{
            health = 125;
            speed = 10f*8f/60f;
            accel = 0.08f;
            drag = 0.016f;
            flying = true;
            hitSize = 10f;
            targetAir = false;
            engineOffset = 4f;
            range = 160f;
            faceTarget = false;
            armor = 0f;
            itemCapacity = 0;
            targetFlags = new BlockFlag[]{BlockFlag.factory, null};
            circleTarget = true;
            ammoType = new ItemAmmoType(Items.blastCompound);

            weapons.add(new Weapon(){{
                minShootVelocity = 10f*8f/60f/2f;
                x = 0f;
                y = 0f;
                shootY = 0f;
                reload = 12f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 15f;
                ignoreRotation = true;
                shootSound = Sounds.none;
                shoot.shots = 4;
                shoot.shotDelay = 0.1f * 60f;
                bullet = new BombBulletType(7f, Vars.tilesize * 3f){{
                    width = 4f;
                    height = 4f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }});
        }};
    }
}
