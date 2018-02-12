/******************************************************************************
*  One RPG character class type (fighter, thief, wizard, etc.).
*
*  @author   Daniel R. Collins (dcollins@superdan.net)
*  @since    2014-05-22
*  @version  1.1
******************************************************************************/

public class ClassType {

	//--------------------------------------------------------------------------
	//  Fields
	//--------------------------------------------------------------------------

	/** Name of this class. */
	String name;

	/** Abbreviation for this class. */
	String abbreviation;

	/** Prime requisite ability. */
	Ability primeRequisite;

	/** Attack bonus numerator. */
	int atkBonusNumer;

	/** Attack bonus denominator. */
	int atkBonusDenom;
	
	/** Hit dice type (sides). */
	int hitDiceType;
	
	/** Hit dice maximum. */
	int hitDiceMax;
	
	/** Hit points added after max dice. */
	int advancedHpInc;

	/** Save as this class. */
	String saveAsClass;

	/** XP increment needed for advanced levels. */
	int advancedXpInc;
	
	/** Array of low-level XP requirements. */
	int[] xpReqs;
	
	/** Array of level titles. */
	String[] titles;

	//--------------------------------------------------------------------------
	//  Constructors
	//--------------------------------------------------------------------------
	
	/**
	*  Constructor (from String arrays).
	*  @param indexData Top-level data about this class.
	*  @param levelData Level-specific information (title, XP, etc.)
	*/
	public ClassType (String[] indexData, String[][] levelData) {
	
		// Master fields
		name = indexData[0];
		abbreviation = indexData[1];
		String atkBonusStr = indexData[3];
		String[] atkBonusPart = atkBonusStr.split("/");
		atkBonusNumer = Integer.parseInt(atkBonusPart[0]);
		atkBonusDenom = Integer.parseInt(atkBonusPart[1]);
		hitDiceType = CSVReader.parseInt(indexData[4]);
		hitDiceMax = CSVReader.parseInt(indexData[5]);
		advancedHpInc = CSVReader.parseInt(indexData[6]);
		saveAsClass = indexData[7];

		// Prime requisite ability
		for (Ability ability: Ability.values()) {
			if (ability.name().equals(indexData[2])) {
				primeRequisite = ability;
			}			
		}
		
		// Class-specific fields
		int arraySize = levelData.length - 1;
		titles = new String[arraySize];
		xpReqs = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			titles[i] = levelData[i+1][1];
			xpReqs[i] = CSVReader.parseInt(levelData[i+1][2]);
		}
		
		// Compute advancedXpInc from last two levels
		advancedXpInc = xpReqs[arraySize - 1] - xpReqs[arraySize - 2];
	}

	//--------------------------------------------------------------------------
	//  Methods
	//--------------------------------------------------------------------------

	/**
	*  Get the class name.
	*/
	public String getName () { return name; }

	/**
	*  Get the class abbreviation.
	*/
	public String getAbbreviation () { return abbreviation; }

	/**
	*  Get the prime requisite.
	*/
	public Ability getPrimeRequisite () { return primeRequisite; }

	/**
	*  Get the hit dice type.
	*/
	public int getHitDiceType () { return hitDiceType; }

	/**
	*  Compute the attack bonus.
	*/
	public int getAttackBonus (int level) {
		return level * atkBonusNumer / atkBonusDenom;
	}

	/**
	*  Returns added hit dice at a given level.
	*/
	public Dice getHitDiceInc (int level) {
		if (level <= 0)
			return new Dice(1, 6, 0);
		else if (level <= hitDiceMax)
			return new Dice(1, hitDiceType, 0);
		else
			return new Dice(0, hitDiceType, advancedHpInc);
	}

	/**
	*  Returns total hit dice at a given level.
	*/
	public Dice getHitDiceTotal (int level) {
		if (level <= 0)
			return new Dice(1, 6, 0);
		else if (level <= hitDiceMax)
			return new Dice(level, hitDiceType, 0);
		else
			return new Dice(hitDiceMax, hitDiceType, 
				advancedHpInc * (level - hitDiceMax));
	}

	/**
	*  Get the title for a given level.
	*/
	public String getTitleFromLevel (int level) {
		int maxTable = titles.length;
		if (level < maxTable)
			return titles[level];
		else
			return titles[maxTable - 1];
	}

	/**
	*  Get the level indicated by a given title.
	*/
	public int getLevelFromTitle (String title) {
		for (int i = 0; i < titles.length; i++) {
			if (titles[i].equals(title)) {
				return i;
			}		
		}
		return -1;
	}

	/**
	*  Compute the experience required for a level.
	*/
	public int getXpReq (int level) {
		int maxTable = xpReqs.length;
		if (level < maxTable)
			return xpReqs[level];
		else
			return xpReqs[maxTable-1]
				+ advancedXpInc * (level - maxTable + 1);
	}

	/**
	*  Get the experience required for the following level.
	*/
	public int getXpReqNext (int level) {
		return getXpReq(level + 1);	
	}

	/**
	*  Get the name of the class that we save as.
	*/
	public String getSaveAsClass () {
		return saveAsClass;
	}

	/**
	*  Identify this object as a string.
	*/
	public String toString() {
		return name + " Class "
			+ "(HD d" + hitDiceType 
			+ ", Atk " + atkBonusNumer + "/" + atkBonusDenom + ")";
	}	

	/*--------------------------------------------------------------------------
	*  TODO: 
	*  Armor & weapons allowed, special abilities (feats, skills, spells).
	*-------------------------------------------------------------------------*/
}

