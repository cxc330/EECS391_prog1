/********
 * File: SearchAgent.java
 * By: Christopher Gross, Chien-Hung Chen
 * Email: cjg28@case.edu, cxc330@case.edu
 * Created: 2/1/2012
 */

package edu.cwru.SimpleRTS.agent;

import java.util.*;
import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

public class SearchAgent extends Agent {

	private static final long serialVersionUID = 1L;
	static int playernum = 0;
	static String townHall = "TownHall";
	static String peasant = "Peasant";
	static String farm = "Farm";
	static String barracks = "Barracks";
	static String footman = "Footman";

	//Constructor
	public SearchAgent(int playernum) {
		super(playernum);
	}

	@Override
	public Map<Integer, Action> initialStep(StateView state) {
		return middleStep(state);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView state) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		List<Integer> allUnitIds = state.getAllUnitIds();
		List<Integer> footmanIds = findUnitType(allUnitIds, state, footman);
		List<Integer> townHallIds = findUnitType(allUnitIds, state, townHall);
		
		
		if(townHallIds.size() > 0) //Town Hall not dead
		{
			actions = aStarSearch(footmanIds.get(0), townHallIds.get(0), state);
		}	
		else 
		{
			System.out.println("Either we killed the townhall!!! ...or you didn't provide one");
		}
		
		if(actions == null)
		{
			actions = new HashMap<Integer, Action>();
		}
		
		return actions;
	}

	@Override
	public void terminalStep(StateView state) {
	}
	
	//matches units with a type and returns the list of unitIds
	public List<Integer> findUnitType(List<Integer> ids, StateView state, String name)	{
		
		List<Integer> unitIds = new ArrayList<Integer>();
		
		for (int x = 0; x < ids.size(); x++)
		{
			Integer unitId = ids.get(x);
			UnitView unit = state.getUnit(unitId);
			
			if(unit.getTemplateView().getUnitName().equals(name))
			{
				unitIds.add(unitId);
			}
		}
		
		return unitIds;
	}
	
	//A* Search Algorithm
	public Map<Integer, Action> aStarSearch(Integer startId, Integer goalId, StateView state)	{
		
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		//Start space and end space
		UnitView startSpace = state.getUnit(startId);
		UnitView goalSpace = state.getUnit(goalId);
		
		ArrayList<UnitView> openList = new ArrayList<UnitView>(); //the open list, will hold items to be searched
		ArrayList<UnitView> closedList = new ArrayList<UnitView>(); //spaces all ready searched
		
		HashMap<UnitView, UnitView> parentNodes = new HashMap<UnitView, UnitView>(); //Parent node, i.e. the node you came from hashed by the UnitView
		HashMap<UnitView, Integer> gCost = new HashMap<UnitView, Integer>(); //gCost hashed by the UnitView
		HashMap<UnitView, Integer> fCost = new HashMap<UnitView, Integer>(); //fCost hashed by the UnitView
		HashMap<UnitView, Integer> hCost = new HashMap<UnitView, Integer>(); //hCost hashed by the UnitView
		
		Integer tempHCost = heuristicCostCalculator(startSpace, goalSpace); //get the costs of the starting node
		Integer tempGCost = 0; //see above
		Integer tempFCost = tempHCost + tempGCost; //see above
		
		openList.add(startSpace); //start out with the first space
		hCost.put(startSpace, tempHCost); //add the hCost to the HashMap
		gCost.put(startSpace, tempGCost); //add the gCost to the HashMap
		fCost.put(startSpace, tempFCost); //add the fCost to the HashMap
		
		System.out.println("Start space: " + startSpace.getXPosition()  + ", " + startSpace.getYPosition());
		System.out.println("Goal space: " + goalSpace.getXPosition()  + ", " + goalSpace.getYPosition());
		
		//loop till we exhaust the openList
		while (openList.size() > 0)
		{
			UnitView currentParent = getLowestCostF(openList, fCost); //finds the UnitView with the lowest fCost
			System.out.println("Searching.. " + currentParent.getXPosition() + ", " + currentParent.getYPosition() + " There are " + openList.size() + " items on the OL");
			
			if (checkGoal(currentParent, goalSpace, state)) //success
			{
				System.out.println("Woot, found the goal");
				if(currentParent.equals(startSpace)) //The starting space is the final space, attack the townHall
				{
					Action attack = Action.createPrimitiveAttack(startSpace.getID(), goalSpace.getID());
					actions.put(startSpace.getID(), attack);
				}
				else
				{
					actions = rebuildPath(parentNodes, currentParent, startSpace); 
				}
				return actions; 
			}
			else
			{
				openList.remove(currentParent); //remove the object from the openList and add it to the closed list
				closedList.add(currentParent);
				
				ArrayList<UnitView> neighbors = getNeighbors(currentParent, state, false); //We need to implement neighbor checking and only return valid neighbor types.. i.e. movable squares
				System.out.println("Found " + neighbors.size() + " neighbors");
				for (UnitView neighbor : neighbors) //loop for all neighbors
				{
					System.out.println("Searching neighbor at : " + neighbor.getXPosition() + ", " + neighbor.getYPosition());
					
					if (checkXYList(closedList, neighbor) == (null)) //only go if the neighbor isn't all ready checked
					{
						tempGCost = gCostCalculator(neighbor, currentParent, gCost); //grab it's gCost
						
						boolean better = true; //used to check if we found a better gCost in the case of the node all ready being in the openList
						UnitView tempNeighbor = neighbor; //temp used in case the neighbor isn't in the openList yet
						
						neighbor = checkXYList(openList, neighbor); //check if the neighbor is in the openList
						
						if (neighbor == (null)) //If the openList doesn't contain this neighbor
						{
							neighbor = tempNeighbor;
							tempHCost = heuristicCostCalculator(neighbor, goalSpace); //get the costs of the starting node
							hCost.put(neighbor, tempHCost); 
							openList.add(neighbor); //add it to the openList
						}
						else if (tempGCost >= gCost.get(neighbor)) //See if we found a better gCost.. if so we're awesome
						{
							better = false;
						}
						
						if (better)
						{
							gCost.put(neighbor, tempGCost); //add the gCost to our hash
							parentNodes.put(neighbor, currentParent); //add the parent reference

							tempFCost = hCost.get(neighbor) + tempGCost; //calculate our fCost
							
							fCost.put(neighbor, tempFCost); //add the value to our hash
						} 
					}					
				}
			}
		}		
		System.out.println("No path from search space to goal...");
		return null; //returns null if we don't find anything
	}
	
	//Checks if we have reached the goal based on if a neighbor is the goalSpace
	public boolean checkGoal(UnitView neighbor, UnitView goal, StateView state)
	{
		
		ArrayList<UnitView> units = getNeighbors(neighbor, state, true);
		Integer x = goal.getXPosition();
		Integer y = goal.getYPosition();
		
		//Check each neighbor and determine if it is the goal
		for (UnitView unit : units)
		{
			Integer unitX = unit.getXPosition();
			Integer unitY = unit.getYPosition();
			
			if (x == unitX && y == unitY) //check against goal x, y
			{
				return true; //we found it!
			}
		}
		
		return false;
	}
	
	//Calculates the distance between neighbor and currentParent + the g_score of currentParent
	public Integer gCostCalculator(UnitView neighbor, UnitView currentParent, HashMap<UnitView, Integer> gCost)
	{
		return gCost.get(currentParent) + heuristicCostCalculator(currentParent, neighbor);
	}
	
	//Determines if we already have the space of values: x, y
	public UnitView checkXYList(ArrayList<UnitView> list, UnitView unit)
	{
		Integer x = unit.getXPosition();
		Integer y = unit.getYPosition();
		
		//if something from list is in unit's position, return it
		for (UnitView item : list)
		{
			if (item.getXPosition() == (x) && item.getYPosition() == (y))
				return item;
		}
		return null; //default return value
	}
	
	//Goes through oList and checks against Hashmap fCost to find the UnitView with the lowest fCost
	public UnitView getLowestCostF(ArrayList<UnitView> oList, HashMap<UnitView, Integer> fCost)
	{
		UnitView lowestCostF = oList.get(0); // set the first node as the lowest case
		
		for(int i = 0; i < oList.size(); i++) // for every item within the list
		{
			if(fCost.get(oList.get(i)) < fCost.get(lowestCostF)) //if the new node is lower than the previous
			{
				lowestCostF = oList.get(i); //set it
			}
		}
		
		return lowestCostF; //return our lowest cost
	}
	
	//returns the path from start to goal
	public Map<Integer, Action> rebuildPath(HashMap<UnitView, UnitView> parentNodes, UnitView goalParent, UnitView startParent)
	{
		ArrayList<UnitView> backwardsPath = new ArrayList<UnitView>(); //The path backwards
		Map<Integer, Action> path = new HashMap<Integer, Action>(); //The return set of actions
		backwardsPath.add(goalParent); //add the goal as our first action
		
		UnitView parentNode = parentNodes.get(goalParent);
		backwardsPath.add(parentNode);
		
		//run till we find the starting node
		while (!parentNode.equals(startParent))
		{
			parentNode = parentNodes.get(parentNode);
			backwardsPath.add(parentNode);
		}
		
		//Loops through the path, calculate the direction, and puts it in the Hashmap to return
		for(int i = (backwardsPath.size()-1); i > 0; i--)
		{
			int xDiff = backwardsPath.get(i).getXPosition() - backwardsPath.get(i-1).getXPosition();
			int yDiff = backwardsPath.get(i).getYPosition() - backwardsPath.get(i-1).getYPosition();
			
			Direction d = Direction.EAST; //default value
			
			if(xDiff < 0 && yDiff > 0) //NW
				d = Direction.NORTHEAST;
			else if(xDiff == 0 && yDiff > 0) //N
				d = Direction.NORTH;
			else if(xDiff > 0 && yDiff > 0) //NE
				d = Direction.NORTHWEST;
			else if(xDiff < 0 && yDiff == 0) //E
				d = Direction.EAST;
			else if(xDiff < 0 && yDiff < 0) //SE
				d = Direction.SOUTHEAST;
			else if(xDiff == 0 && yDiff < 0) //S
				d = Direction.SOUTH;
			else if(xDiff > 0 && yDiff < 0) //SW
				d = Direction.SOUTHWEST;
			else if(xDiff > 0 && yDiff == 0) //W
				d = Direction.WEST;
			if (i == backwardsPath.size()-1) //only put on the first action
			{
				path.put(backwardsPath.get(i).getID(), Action.createPrimitiveMove(backwardsPath.get(i).getID(), d));
			}
			System.out.println("Path action: " + backwardsPath.get(i).getXPosition() + ", " + backwardsPath.get(i).getYPosition() + " Direction: " + d.toString());
		}
		
		return path;
		
	}
	
	//creates a dummy UnitView at the requested space
	public UnitView createOpenSpace(Integer x, Integer y)
	{
		UnitTemplate template = new UnitTemplate(0); //The template, ID 0 is used because we don't care what type it is
		Unit unit = new Unit(template, y);	//The actual Unit
		
		unit.setxPosition(x); //set its x
		unit.setyPosition(y); //set its y
		
		UnitView openSpace = new UnitView(unit); //make a UnitView from it
		
		return openSpace; //return the UnitView
	}
	
	//returns an ArrayList of plausible neighbors
	public ArrayList<UnitView> getNeighbors(UnitView currentParent, StateView state, boolean unitDoesntMatter)
	{
		//NOTE: boolean unitDoesntMatter tells it whether we care about whether or not the space is occupied
		//		It should ONLY be set to true if we are checking goals or cheating...
		
		ArrayList<UnitView> neighbors = new ArrayList<UnitView>(); //The return list of all neighbors
		
		Integer x = currentParent.getXPosition();
		Integer y = currentParent.getYPosition();
		Integer xPlusOne = x + 1;
		Integer xMinusOne = x - 1;
		Integer yPlusOne = y + 1;
		Integer yMinusOne = y - 1;		
		Integer tempX = 0, tempY = 0;
		
		//checking all 8 possible spaces in a grid world
		for (int j = 0; j < 8; j++)
		{
			switch(j)
			{
				case 0: //x + 1, y
					tempX = xPlusOne;
					tempY = y;
					break;
				case 1: //x + 1, y + 1
					tempX = xPlusOne;
					tempY = yPlusOne;
					break;
				case 2: //x + 1, y - 1
					tempX = xPlusOne;
					tempY = yMinusOne;
					break;
				case 3: //x, y + 1
					tempX = x;
					tempY = yPlusOne;
					break;
				case 4: //x, y - 1
					tempX = x;
					tempY = yMinusOne;
					break;
				case 5: //x - 1, y
					tempX = xMinusOne;
					tempY = y;
					break;
				case 6: //x - 1, y + 1
					tempX = xMinusOne;
					tempY = yPlusOne;
					break;
				case 7: //x - 1, y - 1
					tempX = xMinusOne;
					tempY = yMinusOne;
					break;
				default:
					break;
			}
			
			UnitView neighbor = createOpenSpace(tempX, tempY); //make a dummy space
			
			if(checkValidNeighbor(tempX, tempY, state, unitDoesntMatter)) //check if it's a valid space
			{
				neighbors.add(neighbor);
			}
		}		
		
		return neighbors;
	}
	
	//Using chebyshev as our h(n)
	public Integer heuristicCostCalculator(UnitView a, UnitView b)	
	{
		return DistanceMetrics.chebyshevDistance(a.getXPosition(), a.getYPosition(), b.getXPosition(), b.getYPosition());
	}
	
	//returns if a space is empty and valid
	public boolean checkValidNeighbor(Integer x, Integer y, StateView state, boolean unitDoesntMatter)	
	{ 
		boolean isResource = state.isResourceAt(x, y); //check if there is a resource here
		boolean isUnit = state.isUnitAt(x, y); //check if there is a unit here
		boolean isValid = state.inBounds(x, y); //check if the square is valid
		
		boolean isNotTaken = !isResource && !isUnit; //if it is not an occupied square
		
		if ((isNotTaken || unitDoesntMatter) && isValid) //if there is no resource here and no unit and it's valid it means it's an empty square
		{
			return true;
		}
		
		return false;
	}

}
