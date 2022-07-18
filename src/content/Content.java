package content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import classes.BomberEntity;
import core.Mod;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.abilities.UnitSpawnAbility;
import mindustry.entities.bullet.BombBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

public class Content {
    public static UnitType seagull, gull, sparrow, owl, tu;
    public static Block bomberFactory, bomberUpgraderT1, bomberUpgraderT2, bomberUpgraderT3, bomberUpgraderT4, aat1;

    public static Effect gullBombExplosion = new Effect(20, (e) -> {
        Draw.color(Pal.bulletYellow);

        if(e.fin() <= 0.3f) {
            Drawf.circles(e.x, e.y, 55f * e.fin());
        }

        Draw.color(Color.gray);

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
    }), owlBombExplosion = new Effect(20f, (e) -> {
        Draw.color(Pal.bulletYellow);

        circle(e.x, e.y, e.fin() * 6f * Vars.tilesize);

        Draw.alpha(0.8f);
        Draw.color(Pal.gray);

        Angles.randLenVectors(e.id, Mathf.rand.random(5) + 20, 6f * Vars.tilesize * 0.85f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 4f * e.fout());
        });
    }), tuBombExplosion = new Effect(20f, (e) -> {
        Draw.color(Pal.bulletYellow);

        circle(e.x, e.y, e.fin() * 20f * Vars.tilesize);

        Draw.alpha(0.8f);
        Draw.color(Pal.gray);

        Angles.randLenVectors(e.id, Mathf.rand.random(10) + 30, 20f * Vars.tilesize * 0.85f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 8f * e.fout());
        });
    }), timerTuExplosion = new Effect(600f, (e) -> {

    }){
        @Override
        protected void add(float x, float y, float rotation, Color color, Object data) {
            super.add(x, y, rotation, color, data);
            Time.run(lifetime, () -> {
                Effect.shake(6f, 16f, x, y);
                Damage.damage(x, y, 20 * tilesize, 3e3f * 4);
                tuBombExplosion.at(x, y);
            });
        }
    };

    static {
        EntityMapping.register(Mod.name + "-seagull", BomberEntity::new);
        EntityMapping.register(Mod.name + "-gull", BomberEntity::new);
        EntityMapping.register(Mod.name + "-sparrow", BomberEntity::new);
        EntityMapping.register(Mod.name + "-owl", BomberEntity::new);
        EntityMapping.register(Mod.name + "-tu", BomberEntity::new);
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
                shoot.shots = 8;
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
            engineOffset = 13f;
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

        owl = new UnitType("owl"){{
            health = 12e3f;
            speed = 25f*8f/60f;
            accel = 1f / 90f;
            drag = 1f / 60f;
            flying = true;
            hitSize = 30f;
            targetAir = false;
            engineOffset = 13f;
            range = 125f / 2f * Vars.tilesize;
            faceTarget = false;
            armor = 0f;
            itemCapacity = 180;
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
                shoot.shots = 8;
                shoot.shotDelay = 0.1f * 60f;
                bullet = new BombBulletType(300f, Vars.tilesize * 6f){{
                    width = 8f;
                    height = 8f;
                    hitEffect = owlBombExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;

                    status = StatusEffects.blasted;
                    statusDuration = 600f;
                }};
            }});
        }};

        tu = new UnitType("tu"){{
            health = 12e3f;
            speed = 35f*8f/60f;
            accel = 1f / 60f;
            drag = 1f / 30f;
            flying = true;
            hitSize = 35f;
            targetAir = false;
            engineOffset = 18f;
            range = 250f / 2f * Vars.tilesize;
            faceTarget = false;
            armor = 0f;
            itemCapacity = 180;
            targetFlags = new BlockFlag[]{BlockFlag.factory, null};
            circleTarget = true;
            ammoType = new ItemAmmoType(Items.blastCompound);

            abilities.add(new UnitSpawnAbility(seagull, 60f * 10f, 0, 0));

            weapons.add(new Weapon(){{
                minShootVelocity = speed / 10f;
                x = 0f;
                y = 0f;
                shootY = 0f;
                reload = 8f * 60f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 15f;
                ignoreRotation = true;
                shootSound = Sounds.none;
                shoot.shots = 8;
                shoot.shotDelay = 1f * 60f;
                bullet = new BombBulletType(0f, 0f){{
                    width = 16f;
                    height = 16f;
                    hitEffect = Fx.none;
                    shootEffect = timerTuExplosion;
                    smokeEffect = Fx.none;
                }
                    @Override
                    public void hit(Bullet b, float x, float y) {
                        super.hit(b, x, y);
                        randLenVectors(b.id, 50 + Mathf.random(20), 20 * tilesize, (ex, ey) -> {
                            Tile tile = Vars.world.tileWorld(x + ex, y + ey);
                            if(tile != null) Fires.create(tile);
                        });
                    }
                };
            }});
        }};

        bomberFactory = new UnitFactory("bomber-factory"){{
            requirements(Category.units, with(Items.silicon, 50, Items.lead, 50));
            plans = Seq.with(
                    new UnitPlan(seagull, 60f * 10f, with(Items.silicon, 20, Items.lead, 20))
            );
            size = 3;
            consumePower(1f);
        }};

        bomberUpgraderT1 = new Reconstructor("bomber-upgrader-t1"){{
            requirements(Category.units, with(Items.silicon, 100, Items.graphite, 100));

            size = 3;
            consumePower(2f);
            consumeItems(with(Items.silicon, 40, Items.graphite, 40));

            constructTime = 60f * 20f;

            upgrades.addAll(
                    new UnitType[]{seagull, gull}
            );
        }};

        bomberUpgraderT2 = new Reconstructor("bomber-upgrader-t2"){{
            requirements(Category.units, with(Items.silicon, 200, Items.graphite, 200, Items.titanium, 200));

            size = 5;
            consumePower(4f);
            consumeItems(with(Items.silicon, 80, Items.graphite, 80, Items.titanium, 80));

            constructTime = 60f * 40f;

            upgrades.addAll(
                    new UnitType[]{gull, sparrow}
            );
        }};

        bomberUpgraderT3 = new Reconstructor("bomber-upgrader-t3"){{
            requirements(Category.units, with(Items.graphite, 400, Items.silicon, 400, Items.titanium, 400, Items.thorium, 400));

            size = 7;
            consumePower(8f);
            consumeItems(with(Items.silicon, 160, Items.graphite, 160, Items.titanium, 160, Items.thorium, 160));

            constructTime = 60f * 80f;

            upgrades.addAll(
                    new UnitType[]{sparrow, owl}
            );
        }};

        bomberUpgraderT4 = new Reconstructor("bomber-upgrader-t4"){{
            requirements(Category.units, with(Items.silicon, 800, Items.graphite, 800, Items.thorium, 800, Items.titanium, 800, Items.plastanium, 800));

            size = 9;
            consumePower(16f);
            consumeItems(with(Items.silicon, 320, Items.graphite, 320, Items.thorium, 320, Items.titanium, 320, Items.plastanium, 320));

            constructTime = 60f * 160f;
            upgrades.addAll(
                    new UnitType[]{owl, tu}
            );
        }};

        aat1 = new ItemTurret("aa-t1"){{
            health = 500;
            size = 2;

            hasItems = true;
            itemCapacity = 20;



            requirements(Category.defense, with(Items.copper, 35));
        }};
    }
}
