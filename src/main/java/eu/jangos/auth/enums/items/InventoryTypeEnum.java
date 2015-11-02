package eu.jangos.auth.enums.items;

/**
 * je4w is a featured server emulator for World of Warcraft 1.12.x.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * World of Warcraft, and all World of Warcraft or Warcraft art, images, and
 * lore are copyrighted by Blizzard Entertainment, Inc.
 *
 * A lot of credits goes to MaNGOS project from which several ideas (but not
 * all) were included in this project.
 *
 * Copyright (C) 2015-2015 je4w project Copyright (C) 2005-2014 MaNGOS project
 * <http://getmangos.eu>
 */
/**
 * InventoryType represents the type of inveotires existing in the game.
 *
 * @author Warkdev
 * @version v0.1 BETA
 */
public enum InventoryTypeEnum {
    /**
     * Not equipped item.
     */
    NON_EQUIPPED(0),
    
    /**
     * HEAD item.
     */
    HEAD(1),
    
    /**
     * NECK item.
     */
    NECK(2),
    
    /**
     * Shoulders item.
     */
    SHOULDERS(3),
    
    /**
     * Shirt item.
     */
    SHIRT(4),
    
    /**
     * Chest item.
     */
    CHEST(5),
    
    /**
     * Waist item.
     */
    WAIST(6),
    
    /**
     * Legs item.
     */
    LEGS(7),

    /**
     * Feet item.
     */
    FEET(8),
    
    /**
     * Wrists item.
     */
    WRISTS(9),
    
    /**
     * Hands item.
     */
    HANDS(10),
    
    /**
     * Finger item.
     */
    FINGER(11),
    
    /**
     * Trinket item.
     */
    TRINKET(12),
    
    /**
     * Weapon item.
     */
    WEAPON(13),
    
    /**
     * Shield item.
     */
    SHIELD(14),
    
    /**
     * Ranged item.
     */
    RANGED(15),
    
    /**
     * Cloak item.
     */
    CLOAK(16),
    
    /**
     * 2-hands item.
     */
    TWO_HANDS(17),

    /**
     * Bag item.
     */
    BAG(18),

    /**
     * Tabard item.
     */
    TABARD(19),

    /**
     * Robe item.
     */
    ROBE(20),

    /**
     * Main-Hand item.
     */
    MAIN_HAND(21),
    
    /**
     * Off-Hand item.
     */
    OFF_HAND(22),
    
    /**
     * Holdable item.
     */
    HOLDABLE(23),
    
    /**
     * Ammo item.
     */
    AMMO(24),
    
    /**
     * Thrown item.
     */
    THROWN(25),
    
    /**
     * Ranged Right item.
     */
    RANGED_RIGHT(26),
    
    /**
     * Quiver item.
     */
    QUIVER(27),
    
    /**
     * RELIC item.
     */
    RELIC(28);
    
    private final int value;

    /**
     * Default constructor.
     *
     * @param value
     */
    private InventoryTypeEnum(int value) {
        this.value = value;
    }

    /**
     * Return the int value of this Quality.
     * @return the backing int value.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Convert an int value into a Quality value.
     * @param value
     * @return The Quality corresponding to that value, null if there is no match.
     */
    public static InventoryTypeEnum convert(int value) {
        for (InventoryTypeEnum v : InventoryTypeEnum.values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }
    
    /**
     * Method to check whether the given value exists within this enum.
     * @param value The value to be checked.
     * @return True if the value exists.
     */
    public static boolean exists(int value){
        for(InventoryTypeEnum g : InventoryTypeEnum.values())
        {
            if(g.getValue() == value)
                return true;
        }
        return false;
    }
}
