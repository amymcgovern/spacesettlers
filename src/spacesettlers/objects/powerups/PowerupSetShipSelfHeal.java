package spacesettlers.objects.powerups;

import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.Ship;

/**
 * Powerup that can be purchased with enough star dust collected and allows a ship to heal itself
 * for a fixed number of time steps.  This powerup costs 0 to use.
 */
public class PowerupSetShipSelfHeal implements SpaceSettlersPowerup{
    /**
     * The number of time steps that star dust allows you to self-heal.  This is a constant (but it could
     * change from project to project)
     */
    public static int SHIP_HEALING_STEPS = 500;

    /**
     * Tell the ship it is able to heal for the new number of steps
     */
    @Override
    public void applyPowerup(AbstractActionableObject actionableObject) {
        Ship ship = (Ship) actionableObject;
        ship.setHealingStepsRemaining(SHIP_HEALING_STEPS);
        ship.removePowerup(getType());
    }

    /**
     * It costs 0
     *
     * @return 0 cost
     */
    @Override
    public int getCostToUse() {
        return 0;
    }

    /**
     * @return the type from the enum
     */
    @Override
    public SpaceSettlersPowerupEnum getType() {
        return SpaceSettlersPowerupEnum.SET_SHIP_SELF_HEAL;
    }
}
