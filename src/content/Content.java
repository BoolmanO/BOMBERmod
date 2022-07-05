package content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import classes.BomberEntity;
import core.Mod;
import mindustry.Vars;
import mindustry.ai.types.SuicideAI;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BombBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.indexer;

public class Content {
    public static UnitType seagull, gull;

    public static Effect gullExplosion = new Effect(20, (e) -> {
        color(Pal.bulletYellow);

        e.scaled(6, i -> {
            stroke(2f * i.fout());
            Lines.circle(e.x, e.y, Vars.tilesize * 3f * i.fout());
        });

        color(Color.gray);

        randLenVectors(e.id, 5, 2f + 10f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 2.5f);
        });

        color(Pal.lighterOrange);
        stroke(e.fout());

        randLenVectors(e.id + 1, 4, 1f + 15f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });

        Drawf.light(e.x, e.y, 50f, Pal.lighterOrange, 0.8f * e.fout());
    });

    static {
        EntityMapping.register(Mod.name + "-seagull", BomberEntity::new);
        EntityMapping.register(Mod.name + "-gull", BomberEntity::new);
    }

    public static void load(){
        seagull = new UnitType("seagull"){{
            Log.info(name);

            health = 125;
            speed = 15f*8f/60f;
            accel = 0.08f;
            drag = 0.02f;
            flying = true;
            hitSize = 7f;
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
                minShootVelocity = speed / 2f;
                x = 0f;
                y = 0f;
                shootY = 0f;
                reload = 2.5f * 60f;
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

        gull = new UnitType("gull"){{
            health = 600;
            speed = 20f*8f/60f;
            accel = 0.05f;
            drag = 0.015f;
            flying = true;
            hitSize = 16f;
            targetAir = true;
            engineOffset = 10f;
            faceTarget = true;
            armor = 0f;
            itemCapacity = 0;
            circleTarget = false;
            ammoType = new ItemAmmoType(Items.blastCompound);

            weapons.add(new Weapon(){{
                reload = 24f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                shootSound = Sounds.explosion;
                x = shootY = 0f;
                mirror = false;
                range = 1f;
                bullet = new BulletType(){{
                    collidesTiles = true;
                    collides = true;
                    hitSound = Sounds.explosion;

                    rangeOverride = 30f;
                    hitEffect = Fx.pulverize;
                    speed = 0f;
                    splashDamageRadius = Vars.tilesize * 5f;
                    instantDisappear = false;
                    hittable = false;
                    collidesAir = true;
                    collidesGround = true;
                    makeFire = true;
                }

                    @Override
                    public void createSplashDamage(Bullet b, float x, float y) {
                    Damage.damage(b.team, x, y, splashDamageRadius, b.damage * b.damageMultiplier(), false, collidesAir, collidesGround, scaledSplashDamage, b);

                    if(status != StatusEffects.none) {
                        Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
                    }
                    indexer.eachBlock(null, x, y, splashDamageRadius, other -> other.team != b.team, other -> Fires.create(other.tile));
                    }
                };
            }
                @Override
                protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet) {
                    bullet.damage = unit.health;
                    unit.destroy();
                }
            });
        }};
    }
}
