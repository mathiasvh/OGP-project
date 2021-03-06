package worms.model;

import java.util.ArrayList;

import worms.exceptions.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class of worms involving a vector, radius, mass, action points, maximum action points,
 * a minimum radius and a name.
 * 
 * @invar Each worm can have its radius as its radius. 
 * 		| canHaveAsRadius(getRadius())
 * @invar Each worm can have its mass as its mass. 
 * 		| canHaveAsMass(getMass())
 * @invar Each worm can have its action points as its action points.
 * 		| canHaveAsActionPoints(getActionPoints())
 * @invar Each worm can have its maximum action points as its maximum action points.
 * 		| canHaveAsMaxActionPoints(getMaxActionPoints())
 * @invar Each worm can have its hit points as its hit points.
 * 		| canHaveAsHitPoints(getHitPoints())
 * @invar Each worm can have its maximum hit points as its maximum hit points.
 * 		| canHaveAsMaxHitPoints(getMaxHitPoints())
 * @invar Each worm can have its name as its name.
 * 		| isValidName(getName())
 * @invar The minimum size that applies to all worms must be a valid size.
 * 		| isValidMinRadius(getMinRadius())
 * 
 * @version 2.0
 * @author Mathias Van Herreweghe, Bachelor Informatics, https://github.com/mathiasvh/worms
 * 
 */
public class Worm extends BallisticObject {
	
	/**
	 * Initialize this new worm with given vector, radius, mass, action points, 
	 * maximum action points and a name.
	 *
	 * @param	vector
	 * 			The vector for this new worm.
	 * @param	radius
	 * 			The radius for this new worm.
	 * @param	actionPoints
	 * 			The action points for this new worm.
	 * @param	name
	 * 			The name for this new worm.
	 * @post	The new vector of this new worm is equal to the given vector. 
	 *     		| new.getVector() == vector
	 * @post	The new radius of this new worm is equal to the given radius.
	 * 			| new.getRadius() == radius
	 * @post	The new minimum radius of this new worm is equal to the fixed
	 * 			minimum value for radii.
	 * 			| new.getMinRadius() = PREDEFINED_MIN_RADIUS1
	 * @post	The new name of this new worm is equal to the given name.
	 * 			| new.getName() == name
	 * @post	The mass of this new worm is equal to the outcome of the formula
	 * 			intended for it.
	 * 			| new.getMass() == calculateMass(radius)
	 * @post	The new action points of this this new worm is equal to the given
	 * 			action points.
	 * 			| new.getActionPoints() == actionPoints
	 * @post	The maximum action points of this new worm is equal to the mass
	 * 			of this worm rounded to the nearest integer and then casted to an
	 * 			integer type.
	 * 			| new.getMaximumActionPoints() == (int) round(getMass())
	 * @throws	IllegalRadiusException(this, radius)
	 * 			The new worm can not have the given radius as its radius.
	 * 			| !this.canHaveAsRadius(radius)
	 * @throws	IllegalNameException(this, name)
	 * 			The new worm can not have the given name as its name.
	 * 			| !isValidName(name)
	 */
	@Raw
	public Worm (Vector vector, double radius, String name, World world, Program program) 
			throws IllegalNameException, IllegalRadiusException{
		super(world, vector);
		this.MIN_RADIUS = PREDEFINED_MIN_RADIUS1;
		if (!canHaveAsRadius(radius))
			throw new IllegalRadiusException(this, radius);
		setRadius(radius);
		setMass(radius);
		this.setMaxActionPoints(calculateMaxActionPoints(getMass()));
		this.setActionPoints(this.getMaxActionPoints());
		if (!isValidName(name))
			throw new IllegalNameException(this, name);
		this.name = name;
		this.setMaxHitPoints(calculateMaxHitPoints(this.getMass()));
		this.setHitPoints(this.getMaxHitPoints());
		this.weapons.add(new Rifle());
		this.weapons.add(new Bazooka());
		this.program = program;
		if (program != null)
			program.setPerformingWorm(this);
	}
	
	@Basic
	@Immutable
	public static double getPREDEFINED_MIN_RADIUS1() {
		return PREDEFINED_MIN_RADIUS1;
	}
	
	/**
	 * Variable registering the minimum radius (in meters) that applies to all worms.
	 */
	private static final double PREDEFINED_MIN_RADIUS1 = 0.25;
	
	/**
	 * Variable registering the density (kg/m³) that applies to all worms.
	 */
	private static final double DENSITY = 1062.0;
	
	/**
	 * Return the minimum radius of this worm.
	 * 	The minimum radius of a worm determines at least how big radius of
	 * 	the worm should be.
	 */
	@Basic
	@Immutable
	public double getMinRadius(){
		return this.MIN_RADIUS;
	}
	
	/**
	 * Check whether the given minimum radius is a valid minimum radius.
	 * 
	 * @param	MIN_RADIUS
	 * 			The minimum radius to check.
	 * @return	True if and only if the given minimum radius is strict positive.
	 * 			| result == (MIN_RADIUS > 0)
	 */
	public static boolean isValidMinRadius(double MIN_RADIUS){
		return MIN_RADIUS > 0;
	}
	/**
	 * Variable registering the minimum radius (expressed in meters) off this worm
	 */
	private final double MIN_RADIUS;
	
	/**
	 * Check whether the given radius is a possible radius for the given worm.
	 * 
	 * @param	radius
	 * 			The radius to check.
	 * @return	True if and only if the given radius is not below the minimum
	 * 			radius that applies to the given worm.
	 * 			| result == (radius >= this.getMinRadius())
	 */
	@Raw
	public boolean canHaveAsRadius(double radius){
		return radius >= this.getMinRadius() && !Double.isInfinite(radius);
	}
	
	/**
	 * Changes the radius for this worm and also changes the mass and maximum
	 * action points for this worm which are dependant on the given radius.
	 * 
	 * @param	radius
	 * 			The new radius for this worm.
	 * @post	The new radius for this worm is equal to the given radius.
	 * 			| new.getRadius() == radius
	 * @post	The new mass for this worm is equal to the outcome of the formula
	 * 			intended for it, which is invoked in the setMass(radius) method.
	 * 			| new.getMass() == calculateMass(radius)
	 * @post	The maximum action points of this new worm is equal to the mass
	 * 			of the current worm, rounded to the nearest integer and casted to an
	 * 			integer type.
	 * 			| new.getMaxActionPoints() == calculateMaxActionPoints(getMass())
	 * @post	The maximum hit points of this new worm is equal to the mass
	 * 			of the current worm, rounded to the nearest integer and casted to an
	 * 			integer type.
	 * 			| new.getMaxHitPoints() == calculateMaxHitPoints(getMass())
	 * @throws	IllegalRadiusException(this, radius)
	 * 			This worm can not have the given radius as its radius.
	 * 			| !this.canAcceptForChangeRadius(radius)
	 */
	@Raw
	public void changeRadius(double radius) throws IllegalRadiusException{
		if (!this.canAcceptForChangeRadius(radius))
			throw new IllegalRadiusException(this, radius);
		this.setRadius(radius);
		this.setMass(radius);
		this.setMaxActionPoints(calculateMaxActionPoints(this.getMass()));
		this.setMaxHitPoints(calculateMaxHitPoints(this.getMass()));
	}
	
	/**
	 * Check whether the radius of this worm can be changed to the given radius.
	 * 
	 * @param	radius
	 * 			The radius to check.
	 * @return	True if and only if this worm can legally change its action points and
	 * 			hit points due to the change of the radius and if this worm can have 
	 * 			the given radius as its radius.
	 * 			| result == canHaveAsMaxActionPoints(
	 *			|				Worm.calculateMaxActionPoints(
	 *			|					Worm.calculateMass(radius))) 
	 *			|						&& canHaveAsRadius(radius) &&
	 *			|							this.canHaveAsMaxHitPoints(
	 *			|								Worm.calculateMaxHitPoints(
	 *			|									Worm.calculateMass(radius)))
	 */
	@Raw
	public boolean canAcceptForChangeRadius(double radius) {
		return this.canHaveAsMaxActionPoints(
				Worm.calculateMaxActionPoints(
						Worm.calculateMass(radius)))
							&& this.canHaveAsRadius(radius) &&
								this.canHaveAsMaxHitPoints(
									Worm.calculateMaxHitPoints(
											Worm.calculateMass(radius)));
	}
	
	/**
	 * Calculates the mass for a worm with a formula which is dependant 
	 * on the radius of a worm.
	 * 
	 * @param	radius
	 * 			The radius of a worm which will be used in the formula.
	 * @return	The outcome off a fixed formula which is dependant on 
	 * 			the given radius.
	 * 			| result == (DENSITY * ((4.0 / 3) * Math.PI * 
	 * 			|	Math.pow(radius, 3)))
	 */
	private static double calculateMass(double radius){
		return DENSITY * ((4.0 / 3) * Math.PI * Math.pow(radius, 3));
	}
	
	/**
	 * Set the mass for this worm to the outcome of the formula intended for it
	 * which depends on the radius of the worm.
	 * 
	 * @param	mass
	 * 			The new mass for this worm.
	 * @post	The new mass for this worm is equal to the outcome of the formula
	 * 			intended for it.
	 * 			| new.getMass() == calculateMass(radius)
	 */
	@Model
	@Raw
	public void setMass(double radius) throws IllegalMassException{
		final double mass = calculateMass(radius);
		if (!canHaveAsMass(mass))
			throw new IllegalMassException(this, mass);
		super.setMass(calculateMass(radius));
	}
	
	/**
	 * Check whether the given radius is a possible radius for the given 
	 * worm.
	 * 
	 * @param	mass
	 * 			The mass to check.
	 * @return	True if and only if the given mass is equal to the outcome 
	 * 			of the formula intended to calculate the mass.
	 * 			| result == (mass == calculateMass(this.getRadius()))
	 */
	@Raw
	public boolean canHaveAsMass(double mass){
		return mass == calculateMass(getRadius());
	}
	
	/**
	 * Check whether this worm can have the given maximum action points
	 * as its maximum action points.
	 * 
	 * @param	maxActionPoints
	 * 			The maximum action points to check.
	 * @return	True if and only if the given maximum action points is 
	 * 			strict positive, and if the given maximum action points 
	 * 			are bigger or equal to the current action points of this 
	 * 			worm.
	 * 			| result == (maxActionPoints >= 0) &&
	 * 			|	(maxActionPoints >= this.actionPoints)
	 */
	@Raw
	public boolean canHaveAsMaxActionPoints(int maxActionPoints){
		return (maxActionPoints >= 0) && 
					(maxActionPoints >= actionPoints);
	}
	
	/**
	 * Set the maximum action points for this worm to the given maximum
	 * action points.
	 * 
	 * @param	maxActionPoints
	 * 			The new maximum action points for this worm.
	 * @post	If the worm can have the given maximum action
	 * 			points as its action points, the new maximum 
	 * 			action points for this worm is equal to the 
	 * 			given maximum action points.
	 * 			| new.getMaxActionPoints() == maxActionPoints
	 */
	@Raw
	@Model
	private void setMaxActionPoints(int maxActionPoints){
		if (this.canHaveAsMaxActionPoints(maxActionPoints))
			this.maxActionPoints = maxActionPoints;
	}
	
	/**
	 * Returns the maximum action points of this worm.
	 * 	The maximum action points of a worm expresses the limit of its 
	 * 	possible action points.
	 */
	@Basic
	@Raw
	public int getMaxActionPoints(){
		return this.maxActionPoints;
	}
	
	/**
	 * Calculates the maximum action points for a worm by rounding the 
	 * mass of the worm to the nearest integer and casting it to an 
	 * integer type.
	 * 
	 * @param	mass
	 * 			The mass which will be used to calculate the maximum
	 * 			action points.
	 * @return	The given mass rounded to the nearest integer and then 
	 * 			casted to an integer.
	 * 			| result == (int) Math.round(mass)
	 */
	private static int calculateMaxActionPoints(double mass){
		return (int) Math.round(mass);
	}
	
	/**
	 * Variable registering the maximum action points of this worm.
	 */
	private int maxActionPoints;
	
	/**
	 * Use the given amount of action points of this worm.
	 * 
	 * @param	amount
	 * 			The amount of action points to be used.
	 * @post	If this worm can have the current action points 
	 * 			decremented by the given amount as its action points. 
	 * 			The new action points of this worm will be equal to 
	 * 			the old action points of this worm decremented with the 
	 * 			given amount of action points. Otherwise, nothing will happen.
	 * 			| if (this.canHaveAsActionPoints(this.getActionPoints() - amount) &&
	 *			|	amount >= 0)
	 *			|		new.getActionPoints() == old.getActionPoints() - amount
	 */
	@Raw
	public void useActionPoints(int amount){
		if (canHaveAsActionPoints(getActionPoints() - amount) &&
				amount >= 0)
					setActionPoints(getActionPoints() - amount);
		else if (isNegativeValue(amount))
			setActionPoints(getActionPoints() - getActionPoints());
			
		
	}
	
	public boolean isNegativeValue(int amount){
		return getActionPoints() - amount < 0;
	}
	
	/**
	 * Set the action points for this worm to given action points.
	 * 
	 * @param	actionPoints
	 * 			The new action points for this worm.
	 * @post	The new action points for this worm is equal to the given action
	 * 			points.
	 * 			| new.getActionPoints() == actionPoints
	 */
	@Model
	@Raw
	private void setActionPoints(int actionPoints){
		this.actionPoints = actionPoints;
	}
	
	/**
	 * Returns the action points of this worm.
	 * 	The action points of a worm expresses the current amount of usable
	 * 	action points.
	 */
	@Basic
	@Raw
	public int getActionPoints(){
		return this.actionPoints;
	}
	
	/**
	 * Check whether this worm can have the given action points
	 * as its action points.
	 * 
	 * @param	actionPoints
	 * 			The action points to check.
	 * @return	True if and only if the given amount of action points is
	 * 			positive, and if the given action points are less or
	 * 			equal to the maximum action points of this worm.
	 * 			| result == (actionPoints >= 0) &&
	 * 			|	(actionPoints <= this.maxActionPoints)
	 */
	@Raw
	public boolean canHaveAsActionPoints(int actionPoints){
		return (actionPoints >= 0) && (actionPoints <= this.maxActionPoints);
	}
	
	/**
	 * Variable registering the action points of this worm.
	 */
	private int actionPoints;
	
	/**
	 * Returns the name of this worm.
	 * 	The name of a worm expresses which worm is which to the end-user.
	 */
	@Basic
	@Raw
	public String getName(){
		return this.name;
	}
	
	/**
	 * Set the name for this worm to the given name.
	 * 
	 * @param	name
	 * 			The new name for this worm.
	 * @post	The new name for this worm is equal to the given name.
	 * 			| new.getName() == name
	 */
	@Raw
	public void setName(String name){
		if (!isValidName(name))
			throw new IllegalNameException(this, name);
		this.name = name;
	}
	
	/**
	 * Check whether a worm can have the given name as its name.
	 * 
	 * @param	name
	 * 			The name to check.
	 * @return	True if and only if the given name has a length greater or equal to
	 * 			2, if the first character is uppercase, and if the given name uses no
	 * 			other character/symbols than those provided.
	 * 			| result == (name.length() >= 2) &&
	 *			|			(Character.isUpperCase(name.charAt(0))) &&
	 *			|				name.matches("[a-zA-Z'\"[0-9] ]*")
	 */
	public static boolean isValidName(String name){
		return (name.length() >= 2) &&
							(Character.isUpperCase(name.charAt(0))) &&
								name.matches("[a-zA-Z'\"[0-9] ]*");
	}
	
	/**
	 * Variable registering the name of this worm.
	 */
	private String name;
	
	/**
	 * Check whether this worm can have the given maximum hit points
	 * as its maximum hit points.
	 * 
	 * @param	maxHitPoints
	 * 			The maximum hit points to check.
	 * @return	True if and only if the given maximum hit points is 
	 * 			strict positive, and if the given maximum hit points 
	 * 			are bigger or equal to the current hit points of this 
	 * 			worm.
	 * 			| result == (maxHitPoints >= 0) &&
	 * 			|	(maxHitPoints >= this.hitPoints)
	 */
	@Raw
	public boolean canHaveAsMaxHitPoints(int maxHitPoints){
		return (maxHitPoints >= 0) && 
					(maxHitPoints >= this.hitPoints);
	}
	
	/**
	 * Set the maximum hit points for this worm to the given maximum
	 * hit points.
	 * 
	 * @param	maxHitPoints
	 * 			The new maximum Hit points for this worm.
	 * @post	If the worm can have the given maximum hit
	 * 			points as its hit points, the new maximum 
	 * 			hit points for this worm is equal to the 
	 * 			given maximum Hit points.
	 * 			| new.getMaxHitPoints() == maxHitPoints
	 */
	@Raw
	@Model
	private void setMaxHitPoints(int maxHitPoints){
		if (this.canHaveAsMaxHitPoints(maxHitPoints))
			this.maxHitPoints = maxHitPoints;
	}
	
	/**
	 * Returns the maximum hit points of this worm.
	 * 	The maximum hit points of worm expresses the limit of its 
	 * 	possible hit points.
	 */
	@Basic
	@Raw
	public int getMaxHitPoints(){
		return this.maxHitPoints;
	}
	
	/**
	 * Calculates the maximum hit points for a worm by rounding the 
	 * mass of the worm to the nearest integer and casting it to an 
	 * integer type.
	 * 
	 * @param	mass
	 * 			The mass which will be used to calculate the maximum
	 * 			hit points.
	 * @return	The given mass rounded to the nearest integer and then 
	 * 			casted to an integer.
	 * 			| result == (int) Math.round(mass)
	 */
	private static int calculateMaxHitPoints(double mass){
		return (int) Math.round(mass);
	}
	
	/**
	 * Variable registering the maximum hit points of this worm.
	 */
	private int maxHitPoints;
	
	/**
	 * Deduct the given amount of hit points of this worm.
	 * 
	 * @param	amount
	 * 			The amount of hit points to be deducted.
	 * @post	If this worm can have the current hit points 
	 * 			decremented by the given amount as its hit points and if the amount given
	 * 			is greater than or equal to zero.
	 * @post	If and only if this worm does not have enough hit points to live, its hit points
	 * 			will be set to zero.
	 * 			The new hit points of this worm will be equal to 
	 * 			the old hit points of this worm decremented with the 
	 * 			given amount of Hit points. Otherwise nothing will happen.
	 * 			| if (this.canHaveAsHitPoints(this.getHitPoints() - amount) &&
	 *			|		amount >= 0)
	 *			|			new.getHitPoints() == old.getHitPoints() - amount
	 *			| if (!isEnoughHitPointsToLive(getHitPoints() - amount))
	 *			|	new.getHitPoints() == old.getHitPoints() - old.getHitPoints()
	 */
	@Raw
	public void deductHitPoints(int amount){
		if (this.canHaveAsHitPoints(this.getHitPoints() - amount) &&
				amount >= 0)
			this.setHitPoints(this.getHitPoints() - amount);
		else if (!isEnoughHitPointsToLive(getHitPoints() - amount)) {
			setHitPoints(getHitPoints() - getHitPoints());
		}
	}
	
	/**
	 * Set the Hit points for this worm to given Hit points.
	 * 
	 * @param	hitPoints
	 * 			The new hit points for this worm.
	 * @post	The new hit points for this worm is equal to the given hit
	 * 			points.
	 * 			| new.getHitPoints() == hitPoints
	 */
	@Model
	@Raw
	private void setHitPoints(int hitPoints){
		this.hitPoints = hitPoints;
	}
	
	/**
	 * Returns the hit points of this worm.
	 * 	The hit points of a worm expresses the current amount of times the
	 * 	worm has been hit.
	 */
	@Basic
	@Raw
	public int getHitPoints(){
		return this.hitPoints;
	}
	
	/**
	 * Check whether this worm can have the given hit points
	 * as its hit points.
	 * 
	 * @param	hitPoints
	 * 			The hit points to check.
	 * @return	True if and only if the given amount of hit points is strict
	 * 			positive, and if the given hit points are less or
	 * 			equal to the maximum hit points of this worm and if the given hit points
	 * 			plus its current hit points are strictly greater than zero.
	 * 			| result == (hitPoints >= 0) &&
	 * 			|	(hitPoints <= this.maxHitPoints)
	 * 			| 		&& hitPoints + getHitPoints() > 0 
	 */
	@Raw
	public boolean canHaveAsHitPoints(int hitPoints){ 
		return (hitPoints >= 0) && (hitPoints <= this.maxHitPoints) && hitPoints + getHitPoints() > 0 ;
	}
	
	/**
	 * Checks whether or not the given hit points are enough to live.
	 * 
	 * @param	hitPoints
	 * 			The hit points to check.
	 * @return True if and only if the given hit points are strictly greater than zero.
	 * 			| result == hitPoints > 0
	 */
	public static boolean isEnoughHitPointsToLive(int hitPoints){
		return hitPoints > 0;
	}
	
	/**
	 * Variable registering the hit points of this worm.
	 */
	private int hitPoints;
	
	/**
	 * Calculates the necessary action points of a worm to move with a 
	 * formula intended for it.
	 * 
	 * @param	nbSteps
	 * 			The numer of steps the worm would perform.
	 * @return	The outcome off a fixed formula which is dependant on the given 
	 * 			number of steps.
	 * 			| result == ((int) Math.ceil( nbSteps *
	 *			|	Math.abs(Math.cos(this.getVector().getDirection())+ 
	 *			|		4 * Math.sin(this.getVector().getDirection()))))
	 */
	@Raw
	private int calculateActionPointsToMove(double slope){
		return (int) Math.ceil(
				Math.abs(Math.cos(slope)+ 
					4.0 * Math.sin(slope)));
	}
	
	/**
	 * Calculates the necessary action points of a worm to turn with a 
	 * formula intended for it.
	 * 
	 * @param	angle
	 * 			The angle that would be added to the current direction of this worm.
	 * @return	The outcome off a fixed formula which is dependant on the given angle.
	 * 			| result == ((int) Math.ceil(Math.abs((angle / (2 * Math.PI))
	 *			|				* 60)))
	 */
	@Raw
	private int calculateActionPointsToTurn(double angle){
		return (int) Math.ceil(Math.abs((angle / (2.0 * Math.PI))
					* 60.0));
	}
	
	/**
	 * Check whether this worm can move.
	 * 
	 * @param	slope
	 * 			The slope between the old and new position.
	 * @return	True if and only if this worm its action points are equal to or greater than
	 * 			the needed action points to perform this move and if its action points are
	 * 			strictly greater than zero and if this worm is active.
	 * 			| result == this.getActionPoints() >= this.calculateActionPointsToMove(slope)
	 *			|	&& this.getActionPoints() > 0 && isActive()
	 */
	@Raw
	private boolean canMove(double slope) {
		return this.getActionPoints() >= this.calculateActionPointsToMove(slope)
				&& this.getActionPoints() > 0 && isActive();
	}
	
	/**
	 * Returns whether or not this worm can move.
	 * 
	 * @return
	 */
	public boolean canMove() {
		return canMove(findPositionToMoveTo().getSlope(getVector().getPosition()));
	}
	
	/**
	 * Moves this worm by the given number of steps.
	 * 
	 * @param	nbSteps
	 * 			The number of steps the worm will move by.
	 * @post	The new x-coordinate for this worm is equal to a formula dependant on the
	 * 			given number of steps.
	 * 			| new.getVector().getPosition().getX() == this.getVector().getPosition().getX() +
				| 	stepDistance * Math.cos(this.getVector().getDirection())
	 * @post	The new y-coordinate for this worm is equal to a formula dependant on the
	 * 			given number of steps.
	 * 			| this.getVector().getPosition().getY() +
				| 	stepDistance * Math.sin(this.getVector().getDirection())
	 * @post	The new action points of this new worm is equal to this worm its current action
	 * 			points minus the outcome of a formula intended to calculate the cost in action points
	 * 			of the given number of steps to move.
	 * 			| new.getActionPoints() == old.getActionPoints() - this.calculateActionPointsToMove(angle)
	 * @throws	IllegalMoveException(this, nbSteps)
	 * 			This worm can not perform this move.
	 * 			| !canMove(nbSteps)
	 */
	@Raw
	public void move() throws IllegalMoveException{
		Position oldPos = new Position(getVector().getPosition().getX(), getVector().getPosition().getY());		
		Position newPos = findPositionToMoveTo();
		
		if (!canMove(newPos.getSlope(oldPos)))
			throw new IllegalMoveException(this);
		
		if (!getWorld().liesWithinWorld(newPos, getRadius()))
			getWorld().terminate(this);
		
		getVector().getPosition().setX(newPos.getX());
		getVector().getPosition().setY(newPos.getY());
		
		useActionPoints(this.calculateActionPointsToMove(newPos.getSlope(oldPos)));
		
		if (getWorld().shouldStartNextTurn())
			getWorld().startNextTurn();
	}
	
	/**
	 * Finds the best position for this worm to move to, from its current position.
	 * 
	 * @return	The best position found, maximising the distance and minimising the difference to its current
	 * 			direction with a maximum of 0.7875 rad.
	 */
	private Position findPositionToMoveTo() {		
		Position currPos = new Position(
				getVector().getPosition().getX(),
				getVector().getPosition().getY());
		Position bestPos = new Position(currPos.getX(), currPos.getY());
		double bestDistance = 0;
		final double intervalDistance = getRadius() / 100;
		final double maxDiff = 0.7875;
		final double stepSize = 0.0175;
		final int[] posNeg = new int[] {-1,1};
		boolean foundOne = false;
		
		for (double distance = getRadius(); distance >= 0.1; distance -= intervalDistance) {
			for (double currAngle = 0; currAngle <= maxDiff; currAngle += stepSize) {
				for (int sgn : posNeg){
					currPos = new Position(
							getVector().getPosition().getX() + 
								distance * Math.cos(getVector().getDirection() + sgn * currAngle),
							getVector().getPosition().getY() + 
								distance * Math.sin(getVector().getDirection() + sgn * currAngle));

					if (isBestPositionToMove(distance, bestDistance, currPos)) {
						foundOne = true;
						bestDistance = distance;
						bestPos = new Position(currPos.getX(), currPos.getY());
					}
				}
			}
		}
		
		if (!foundOne) {
			for (double distance = getRadius(); distance >= 0.1; distance -= intervalDistance) {
				currPos = new Position(
						getVector().getPosition().getX() + distance * Math.cos(getVector().getDirection()),
						getVector().getPosition().getY() + distance * Math.sin(getVector().getDirection()));
				if (getWorld().isPassableSpot(currPos, getRadius()))
					return new Position(currPos.getX(), currPos.getY());
			}
		}
		return bestPos;		
	}
	
	/**
	 * Checks whether or not the given position is the best position to move considering the given distance 
	 * 	and best distance.
	 * @param	distance
	 * 			The distance from the worm its current position to the given position.
	 * @param	bestDistance
	 * 			The best distance found so far, the greater the better.
	 * @param	currPos
	 * 			The position to check.
	 * @return	True if and only if the given distance is strictly greater than the given best distance and if
	 * 			the given position is located on an adjacent position and if the given position for this worm
	 * 			lies within this worm its world.
	 * 			| result == distance - bestDistance > 0  && 
	 *			|	getWorld().isAdjacentPosition(currPos,getRadius()) &&
	 *			|		getWorld().liesWithinWorld(currPos, getRadius())
	 */
	private boolean isBestPositionToMove(double distance, double bestDistance, Position currPos) {
		return distance - bestDistance > 0  && 
				getWorld().isAdjacentPosition(currPos,getRadius()) &&
					getWorld().liesWithinWorld(currPos, getRadius());
	}
	
	/**
	 * Checks whether or not this worm can fall.
	 * 
	 * @return True if and only if the worm can fall from its current position.
	 */
	public boolean canFall(){
		return canFall(getVector().getPosition());
	}
	
	/**
	 * Checks whether or not this worm can fall from the given position.
	 * 
	 * @param	curPos
	 * 			The position to check for this worm whether or not it can fall.
	 * @return	True if only if the given position for this worm is not adjacent position and if this worm is active.
	 * 			| result == !getWorld().isAdjacentPosition(curPos, getRadius()) && isActive()
	 *
	 */
	@Model
	private boolean canFall(Position curPos){
		return !getWorld().isAdjacentPosition(curPos, getRadius()) && isActive();
	}
	
	/**
	 * Makes this worm fall.
	 */
	public void fall(){
		boolean outOfWorld = false;
		Position oldPos = new Position(getVector().getPosition().getX(), getVector().getPosition().getY());
		while (canFall() && !outOfWorld) {
			if (!getWorld().liesWithinWorld(getVector().getPosition(), getRadius())) {
				getWorld().terminate(this);
				outOfWorld = true;
				break;
			}
			getVector().getPosition().setY(getVector().getPosition().getY() - getRadius() / 100);
		}
		final int damage = 3 * (int) Math.floor(oldPos.getY() - getVector().getPosition().getY());
		deductHitPoints(damage);
		
		if (getWorld().shouldStartNextTurn())
			getWorld().startNextTurn();
	}
	
	/**
	 * Check whether this worm can turn the given angle.
	 * 
	 * @param	angle
	 * 			The angle that the worm would move.
	 * @return	True if and only if the given worm its amount of action points is
	 * 			greater than or equal to the amount of action points that are
	 * 			required to perform the turn and if the action points of this worm 
	 * 			are strict positive, and if the worm will reach a valid angle by turning
	 * 			by the given angle.
	 * 			| result == this.getActionPoints() >= this.calculateActionPointsToTurn(angle) &&
	 *			| 	this.getActionPoints() > 0 && 
	 *			| 		this.getVector().canAcceptForChangeDirection(angle)
	 */
	@Raw
	public boolean canTurn(double angle) {
		return this.getActionPoints() >= this.calculateActionPointsToTurn(angle) &&
				this.getActionPoints() > 0 && 
				this.getVector().canAcceptForChangeDirection(angle);
	}
	
	/**
	 * Turns this worm by the given angle.
	 * 
	 * @param	angle
	 * 			The angle that the worm turns by.
	 * @post	The new direction for this worm is equal to current angle of this worm
	 * 			plus the given angle.
	 * 			| new.getVector().getDirection() == old.getVector().getDirection() +
				| 	angle
	 * @post	The new action points of this new worm is equal to this worm its current action
	 * 			points minus the outcome of a formula intended to calculate the cost in action points
	 * 			of the given angle to turn by.
	 * 			| new.getActionPoints() == old.getActionPoints() - this.calculateActionPointsToTurn(angle)
	 * @throws	IllegalTurnException(this, angle)
	 * 			The new worm can not have the given angle as its angle.
	 * 			| !this.canTurn(angle)
	 */
	@Raw
	public void turn(double angle) {
		assert (!canTurn(angle)) : 
			"Precondition: acceptable direction";
		this.getVector().changeDirection(angle);
		this.useActionPoints(this.calculateActionPointsToTurn(angle));		
		
		if (getWorld().shouldStartNextTurn())
			getWorld().startNextTurn();
	}
	
	
	/**
	 * Lets this worm jump.
	 * 
	 * @post	The new x-coordinate for this worm is equal to the current x-coordinate plus the distance
	 * 			of the jump which is calculated by a formula intended for it.
	 * 			| new.getVector().getPosition().getX() == old.getVector().getPosition().getX() + distance)
	 * @post	The new action points of this new worm is equal to this worm its current action
	 * 			points minus its current action points, and will thus be equal to zero.
	 * 			| new.getActionPoints() == old.getActionPoints() - old.getActionPoints()
	 * @throws	IllegalJumpException(this)
	 * 			This worm can not perform a jump.
	 * 			| !this.canJump()
	 */
	@Raw
	@Override
	public void jump(double timeStep) throws IllegalJumpException {
		if (!this.canJump())
			throw new IllegalJumpException(this);
	
		performJump(timeStep);
		this.useActionPoints(this.getActionPoints());
		if (getWorld().shouldStartNextTurn())
			getWorld().startNextTurn();
	}	
	
	/**
	 * Calculates the jump force of a worm with a formula intended for it.
	 * 
	 * @return	The outcome off a fixed formula.
	 * 			| result == (5 * this.getActionPoints()) +
	 *			|	(this.getMass() * STD_ACCEL_EARTH)
	 */
	@Raw
	public double getJumpForce() {		
		return (5.0 * getActionPoints()) +
				(getMass() * World.getSTD_ACCEL_EARTH());
	}
	
	/**
	 * Calculates the initial jump velocity of a worm with a formula intended for it.
	 * 
	 * @param	force
	 * 			The force that will be used to calculate the initial jump velocity.
	 * @return	The outcome off a fixed formula.
	 * 			| result == ((force / getMass()) * 0.5)
	 */
	@Raw
	public double getInitialJumpVelocity(double force){
		return (force / getMass()) * 0.5;
	}
	
	/**
	 * Check whether this worm can perform a jump.
	 * 
	 * @return	True if and only if this worm its action points are strictly greater than 0 and if
	 * 			this worm is located on a passable spot. Otherwise, false.
	 * 			| return getActionPoints() > 0.0 && isActive() && 
	 *			|	getWorld().isPassableSpot(getVector().getPosition(), getRadius());
	 */
	@Raw
	public boolean canJump(){
		return getActionPoints() > 0.0 && isActive() && 
				getWorld().isPassableSpot(getVector().getPosition(), getRadius());
	}
	
	/**
	 * Checks whether or not the jump is finished for the given position.
	 * 
	 * @param	currPos
	 * 			The position to check.
	 * @return	False if this worm is inactive or if the given position for this worm makes the worm be on a adjacent
	 * 			position or if the given position for this worm lies out of this worm its world or if the given position
	 * 			for this worm is an impassable spot. False otherwise.
	 * 			| result == !isActive() || 
	 *			|	getWorld().isAdjacentPosition(currPos, getRadius()) || 
	 *			|		!getWorld().liesWithinWorld(currPos, getRadius()) || getWorld().isImpassableSpot(currPos, getRadius())
	 */
	public boolean isJumpFinished(Position currPos){
		return !isActive() || 
					getWorld().isAdjacentPosition(currPos, getRadius()) || 
						!getWorld().liesWithinWorld(currPos, getRadius()) || getWorld().isImpassableSpot(currPos, getRadius());
	}
	
	/**
	 * Checks whether or not this worm can shoot.
	 * 
	 * @return	True if and only if this worm its action points are greather than or equal to zero and if
	 * 			this worm is active and if this worm its current weapon is not null and if this worm is located
	 * 			on a passable spot.
	 * 			| result == getActionPoints() >= 0 && 
	 *			|	isActive() && getCurrentWeaponName() != null && 
	 *			|		getWorld().isPassableSpot(getVector().getPosition(), getRadius())
	 */
	public boolean canShoot(){
		return getActionPoints() >= 0 && 
				isActive() && getCurrentWeaponName() != null && 
					getWorld().isPassableSpot(getVector().getPosition(), getRadius());
	}
	
	/**
	 * Lets this worm perform a shot with its current weapon.
	 * 
	 * @param	yield
	 * 			The propulsion value for this worm its weapon if applicable.
	 */
	public void shoot(int yield){
		Projectile projectile = new Projectile(getWorld(), this, yield, getCurrentWeapon());
		if (!canShoot())
			throw new IllegalShootException(this);

		getWorld().setActiveProjectile(projectile);
		useActionPoints(projectile.getWeapon().getCost());
		getWorld().removeDeadWorms();
		if (getWorld().shouldStartNextTurn())
			getWorld().startNextTurn();
	}
	
	/**
	 * Initializes this worm its properties for a new turn.
	 * 
	 * @post	The new action points of this worm is equal to its maximum action points.
	 * 			| new.getActionPoints() == old.getMaxActionPoints()
	 * @post	If and only if this worm can have its current hit points plus the amount of hit points
	 * 			rechargable each turn as its hit points, the hit points of this worm will be equal to its
	 * 			current hit points plus the amount of hit points rechargable each turn. Otherwise,
	 * 			the hit points of this worm will be set equal to its maximum hit points.
	 * 			| if (old.canHaveAsHitPoints(old.getHitPoints() + TURNBASED_RECHARGABLE_HITPOINTS))
	 *			|		new.getHitPoints() == old.getHitPoints() + TURNBASED_RECHARGABLE_HITPOINTS
	 *			| else
	 *			|	new.getHitPoints() == old.getMaxHitPoints()
	 */
	public void initializeForTurn(){
		this.setActionPoints(this.getMaxActionPoints());
		
		if (this.canHaveAsHitPoints(this.getHitPoints() + TURNBASED_RECHARGABLE_HITPOINTS))
			this.setHitPoints(this.getHitPoints() + TURNBASED_RECHARGABLE_HITPOINTS);
		else
			this.setHitPoints(this.getMaxHitPoints());
	}
	
	/**
	 * Variable registering the amount of hit points that should be recharged of a worm
	 * 	each turn.
	 */
	private static final int TURNBASED_RECHARGABLE_HITPOINTS = 10;
	
	/**
	 * Changes the index off the current weapon.
	 * @post	The new current weapon index is equal to its last value incremented with 1.
	 * 			If this new value is greater than the amount of weapons available, the index
	 * 			will be set to zero.
	 * 			| currentWeaponIdx++;
	 *			| if (currentWeaponIdx >= weapons.size())
	 *			|	currentWeaponIdx=0;
	 */
	public void selectNextWeapon() {
		currentWeaponIdx++;
		if (currentWeaponIdx >= weapons.size())
			currentWeaponIdx=0;
	}
	
	/**
	 * Returns the name of the current weapon.
	 */
	public String getCurrentWeaponName() {
		return getCurrentWeapon().getName();
	}
	
	/**
	 * Returns the current weapon.
	 */
	private Weapon getCurrentWeapon() {
		return weapons.get(currentWeaponIdx);
	}
	
	/**
	 * Variable registering the index of this worm its current weapon.
	 * 	The index indicates which weapon is currently the weapon of this worm listed in
	 * 	this worm its weapon list.
	 */
	private int currentWeaponIdx = 0;
	
	/**
	 * Variable registering all the weapons of this worm.
	 * 	The weapons express the kind of weapons this worm has equipped.
	 */
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	
	/**
	 * Checks whether or not this worm has a program assigned.
	 * 
	 * @return	True if and only if this worm its program is not null.
	 * 			| result == program != null
	 */
	public boolean hasProgram(){
		return program != null;
	}
	
	/**
	 * Runs the program of this worm.
	 */
	public void runProgram() {
		program.run();
	}
	
	/**
	 * Variable registering the program of this worm.
	 * 	The program of a worm expresses the action it will perform when executed.
	 */
	private final Program program;
}
