package net.revtut.libraries.minecraft.bukkit.guns;

import net.revtut.libraries.Libraries;
import net.revtut.libraries.minecraft.bukkit.guns.events.GunFireEvent;
import net.revtut.libraries.minecraft.bukkit.guns.events.GunHitEvent;
import net.revtut.libraries.minecraft.bukkit.guns.events.GunReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Gun Object
 */
public abstract class Gun extends ItemStack {

    /**
     * Name of the gun
     */
    private final String name;

    /**
     * Fire rate in RPM (rounds per minute)
     */
    private final int fireRate;

    /**
     * Reload time of the gun in seconds
     */
    private final int reloadTime;

    /**
     * Speed that the projectile has when leaves the muzzle
     */
    private final int muzzleVelocity;

    /**
     * Accuracy of the gun
     */
    private final float accuracy;

    /**
     * Shoot recoil
     */
    private final float recoil;

    /**
     * Size of the magazine
     */
    private final int magazineSize;

    /**
     * Number of bullets per shot
     */
    private final int bulletsPerShot;

    /**
     * Bullet of the gun
     */
    private final Bullet bullet;

    /**
     * Constructor of Gun
     * @param name name of the gun
     * @param appearance appearance of the gun
     * @param fireRate fire rate of the gun
     * @param reloadTime reload time of the gun
     * @param muzzleVelocity muzzle velocity
     * @param accuracy accuracy of the gun
     * @param recoil recoil of the gun
     * @param magazineSize size of the magazine
     * @param bulletsPerShot number of bullets per shot
     * @param bullet bullet of the gun
     */
    public Gun(final String name, final ItemStack appearance, final int fireRate, final int reloadTime, final int muzzleVelocity, final float accuracy, final float recoil, final int magazineSize, final int bulletsPerShot, final Bullet bullet) {
        super(appearance.getType(), appearance.getAmount(), appearance.getDurability());

        this.name = name;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.muzzleVelocity = muzzleVelocity;
        this.accuracy = accuracy;
        this.recoil = recoil;
        this.magazineSize = magazineSize;
        this.bulletsPerShot = bulletsPerShot;
        this.bullet = bullet;

        GunManager.getInstance().addGun(this);
    }

    /**
     * Get the name of the gun
     * @return name of the gun
     */
    public String getName() {
        return name;
    }

    /**
     * Get the item stack of the gun
     * @return item stack of the gun
     */
    public ItemStack getItemStack() {
        return this;
    }

    /**
     * Get the fire rate of the gun in RPM (rounds per minute)
     * @return fire rate of the gun
     */
    public int getFireRate() {
        return fireRate;
    }

    /**
     * Get the reload time of the gun
     * @return reload time of the gun
     */
    public int getReloadTime() {
        return reloadTime;
    }

    /**
     * Get the bullet velocity when leaving the muzzle
     * @return bullet velocity when leaving the muzzle
     */
    public int getMuzzleVelocity() {
        return muzzleVelocity;
    }

    /**
     * Get the accuracy of the gun
     * @return accuracy of the gun
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * Get the recoil of the gun
     * @return recoil of the gun
     */
    public float getRecoil() {
        return recoil;
    }

    /**
     * Get the size of the magazine
     * @return size of the magazine
     */
    public int getMagazineSize() {
        return magazineSize;
    }

    /**
     * Get the number of bullets per shot
     * @return number of bullets per shot
     */
    public int getBulletsPerShot() {
        return bulletsPerShot;
    }

    /**
     * Get the bullet of the gun
     * @return bullet of the gun
     */
    public Bullet getBullet() {
        return bullet;
    }

    /**
     * Shoot the gun
     * @param shooter player to shoot from
     */
    public void shoot(final LivingEntity shooter) {
        final GunManager gunManager = GunManager.getInstance();

        // Check if player can shoot
        final long lastShot = gunManager.getLastShot(shooter);
        if(lastShot != -1) {
            final long currentTime = System.nanoTime();
            final long delayPerShot = getFireRate() / 60000000000l; // Delay between each shot in nanoseconds

            if(currentTime - lastShot < delayPerShot)
                return;
        }
        int currentSizeMagazine = gunManager.getCurrentMagSize(shooter);
        if(currentSizeMagazine != -1) {
            if(currentSizeMagazine <= 0)
                return;
        } else
            currentSizeMagazine = getMagazineSize();

        // Call event
        final GunFireEvent event = new GunFireEvent(shooter, this);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return;

        // Shoot the gun
        for(int i = 0; i < getBulletsPerShot(); i++) {
            final Vector direction = shooter.getEyeLocation().getDirection();
            direction.add(new Vector(Math.random() * getAccuracy() - getAccuracy(), Math.random() * getAccuracy() - getAccuracy(), Math.random() * getAccuracy() - getAccuracy()));

            final Projectile projectile = shooter.launchProjectile(getBullet().getProjectile(), direction.multiply(getMuzzleVelocity()));
            projectile.setCustomName(getBullet().getName());
            projectile.setCustomNameVisible(false);
            GunManager.getInstance().addProjectile(projectile, shooter);
        }

        // Apply recoil
        final Location location = shooter.getLocation();
        location.setPitch(location.getPitch() * getRecoil());
        shooter.teleport(location);

        // Add to maps
        gunManager.setCurrentMagSize(shooter, --currentSizeMagazine);
        gunManager.setLastShot(shooter, System.nanoTime());
    }

    /**
     * On hit by a bullet
     * @param shooter player that shot the gun
     * @param target entity that was hit
     * @param landLocation projectile land location
     * @param projectile projectile that hit
     */
    public void hit(final LivingEntity shooter, final LivingEntity target, final Location landLocation, final Projectile projectile) {
        GunManager.getInstance().removeProjectile(projectile);

        // Call event
        final GunHitEvent event = new GunHitEvent(shooter, target, this, getShotType(landLocation, target.getLocation()));
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return;

        // Knockback
        target.setVelocity(target.getLocation().getDirection().multiply(-1 * getBullet().getKnockback()));

        // Apply damage
        double damage = Math.random() * (getBullet().getMaxDamage() - getBullet().getMinDamage()) + getBullet().getMinDamage();
        damage *= event.getDamageMultiplier();
        target.damage(damage);
    }

    /**
     * Get the shot type of a projectile
     * @param landLocation land location of the projectile
     * @param targetLocation location of the target
     * @return shot type
     */
    private ShotType getShotType(final Location landLocation, final Location targetLocation) {
        // Calculate the landing location relatively to the target body
        final double bodyLandingX = landLocation.getX() - targetLocation.getX();
        final double bodyLandingY = landLocation.getY() - targetLocation.getY();
        final double bodyLandingZ = landLocation.getZ() - targetLocation.getZ();

        if(bodyLandingY > 1.5D)
            return ShotType.HEAD_SHOT;

        if(bodyLandingY > 0.25D && bodyLandingY < 0.75D)
            return ShotType.KNEE_SHOT;

        if(bodyLandingY > 0.1D && bodyLandingY < 1.0D)
            return ShotType.LEG_SHOT;

        if(bodyLandingY <= 0.1D)
            return ShotType.FOOT_SHOT;

        if(bodyLandingX > 1.0D && bodyLandingZ > 1.0D || bodyLandingX < -1.0D && bodyLandingZ < -1.0D)
            return ShotType.ARM_SHOT;

        return ShotType.BODY_SHOT;
    }

    /**
     * Reload the gun
     * @param player player that is reloading the gun
     */
    public void reload(final LivingEntity player) {
        // Call event
        final GunReloadEvent event = new GunReloadEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return;

        GunManager.getInstance().setCurrentMagSize(player, 0); // Prevent shoot when gun is reloading
        Bukkit.getScheduler().runTaskLater(Libraries.getInstance(), () -> GunManager.getInstance().setCurrentMagSize(player, getMagazineSize()), getReloadTime());
    }

    /**
     * Convert a gun to string
     * @return converted string
     */
    @Override
    public String toString() {
        return "Gun{" +
                "name='" + name + '\'' +
                ", fireRate=" + fireRate +
                ", reloadTime=" + reloadTime +
                ", muzzleVelocity=" + muzzleVelocity +
                ", accuracy=" + accuracy +
                ", recoil=" + recoil +
                ", magazineSize=" + magazineSize +
                ", bulletsPerShot=" + bulletsPerShot +
                ", bullet=" + bullet +
                '}';
    }
}