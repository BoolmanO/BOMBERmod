package content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
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
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.indexer;

public class Content {
    public static UnitType seagull, gull, sparrow;

    public static Effect gullBombExplosion = new Effect(20, (e) -> {
        color(Pal.bulletYellow);

        if(e.fin() <= 0.3f) {
            Drawf.circles(e.x, e.y, 55f * e.fin());
        }

        color(Color.gray);

        Draw.alpha(0.8f);
        randLenVectors(e.id, 5, 8f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fin() * 2f);
        });
        Draw.alpha(1f);

        Drawf.light(e.x, e.y, 50f, Pal.lighterOrange, 0.8f * e.fout());
    }), sparrowBombExplosion = new Effect(20f, (e) -> {
        Draw.color(Pal.bulletYellow);

        circle(e.x, e.y, e.fin() * 40f);

        Draw.alpha(0.8f);
        Draw.color(Pal.gray);

        Angles.randLenVectors(e.id, Mathf.rand.random(5) + 15, 30f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 3f * e.fout());
        });
    });

    static {
        EntityMapping.register(Mod.name + "-seagull", BomberEntity::new);
        EntityMapping.register(Mod.name + "-gull", BomberEntity::new);
        EntityMapping.register(Mod.name + "-sparrow", BomberEntity::new);
    }

    public static void load(){
        seagull = new UnitType("seagull"){{
            health = 250;
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
            itemCapacity = 50;
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
                bullet = new BombBulletType(10f, Vars.tilesize * 3f){{
                    width = 4f;
                    height = 4f;
                    hitEffect = gullBombExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }});
        }};

        gull = new UnitType("gull"){{
            health = 300;
            speed = 20f*8f/60f;
            accel = 0.05f;
            drag = 0.015f;
            flying = true;
            hitSize = 16f;
            targetAir = true;
            engineOffset = 10f;
            faceTarget = true;
            armor = 0f;
            itemCapacity = 80;
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
                        randLenVectors(b.id, 20, splashDamageRadius, (ex, ey) -> {
                            Tile tile = Vars.world.tileWorld(x + ex, y + ey);
                            if(tile != null) Fires.create(tile);
                        });
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

        sparrow = new UnitType("sparrow"){{
            health = 1200;
            speed = 15f*8f/60f;
            accel = 0.015f;
            drag = 0.01f;
            flying = true;
            hitSize = 20f;
            targetAir = false;
            engineOffset = 10f;
            range = 500f;
            faceTarget = false;
            armor = 2f;
            itemCapacity = 140;
            targetFlags = new BlockFlag[]{BlockFlag.factory, null};
            circleTarget = true;
            ammoType = new ItemAmmoType(Items.blastCompound);

            weapons.add(new Weapon(){{
                minShootVelocity = speed / 2f;
                x = 0f;
                y = 0f;
                shootY = 0f;
                reload = 5f * 60f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 15f;
                ignoreRotation = true;
                shootSound = Sounds.none;
                shoot.shots = 3;
                shoot.shotDelay = 0.35f * 60f;
                bullet = new BombBulletType(200f, Vars.tilesize * 5f){{
                    width = 8f;
                    height = 8f;
                    hitEffect = sparrowBombExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    status = StatusEffects.blasted;
                    statusDuration = 240f;
                }
                    @Override
                    public void hit(Bullet b, float x, float y) {
                        super.hit(b, x, y);
                        randLenVectors(b.id, 15 + Mathf.random(5), splashDamageRadius * 0.6f, (ex, ey) -> {
                            Tile tile = Vars.world.tileWorld(x + ex, y + ey);
                            if(tile != null) Fires.create(tile);
                        });
                    }
                };
            }});
        }};
    }
}
