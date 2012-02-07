package edu.cwru.SimpleRTS.agent;

import java.util.*;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

public class SearchAgent extends Agent {

	private static final long serialVersionUID = 1L;
	static int playernum = 0;
	static String townHall = "TownHall";
	static String peasant = "Peasant";
	static String farm = "Farm";
	static String barracks = "Barracks";
	static String footman = "Footman";

	public SearchAgent(int playernum) {
		super(playernum);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<Integer, Action> initialStep(StateView state) {
		// TODO Auto-generated method stub
		return middleStep(state);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView state) {
		// TODO Auto-generated method stub	
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		return actions;
	}

	@Override
	public void terminalStep(StateView state) {
		// TODO Auto-generated method stub

	}
	
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
	
	public Map<Integer, Action> aStarSearch(Integer startId, Integer goalId, StateView state)	{
		
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		UnitView startSpace = state.getUnit(startId); //starting space
		UnitView goalSpace = state.getUnit(goalId); //end space
		
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
		
		hCost.put(goalSpace, tempHCost); //add the hCost to the HashMap
		gCost.put(goalSpace, tempGCost); //add the gCost to the HashMap
		fCost.put(goalSpace, tempFCost); //add the fCost to the HashMap
		
		while (openList.size() > 0) //loop till we exhaust the openList
		{
			UnitView currentParent = getLowestCostF(openList, fCost); //finds the UnitView with the lowest fCost
			
			if (currentParent.equals(goalSpace) ) //success
			{
				actions = rebuildPath(parentNodes, currentParent, startSpace); 
				break; 
			}
			else //keep on searching
			{
				openList.remove(currentParent); //remove the object from the openList and add it to the closed list
				closedList.add(currentParent);
				
				ArrayList<UnitView> neighbors = getNeighbors(currentParent, state); //We need to implement neighbor checking and only return valid neighbor types.. i.e. movable squares
				
				for (UnitView neighbor : neighbors) //loop for all neighbors
				{
					if (!closedList.contains(neighbor)) //only go if the neighbor isn't all ready checked
					{
						tempGCost = gCostCalculator(neighbor, currentParent, gCost, parentNodes, startSpace); //Jeff implement gCost calculation
						boolean better = true;
						
						if (!openList.contains(neighbor)) //If the openList doesn't contain this neighbor
						{
							tempHCost = heuristicCostCalculator(neighbor, goalSpace); //get the costs of the starting node
							hCost.put(neighbor, tempHCost);
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
		
		return actions; //returns null if we don't find anything
	}
	
	//this calculates the distance between neighbor and currentParent + the g_score of currentParent to startSpace
	public Integer gCostCalculator(UnitView neighbor, UnitView currentParent, HashMap<UnitView, Integer> gCost, HashMap<UnitView, UnitView> parentNodes, UnitView startParent)
	{
		int total = 0;
		int xDiff = neighbor.getXPosition() - currentParent.getXPosition();
		int yDiff = neighbor.getYPosition() - currentParent.getYPosition();
		//I understand that we're supposed to take the distance between neighbor and currentParent and add it to the cumulative gCost
		//But should it be pythagorean theorem to find distance? But then the gCost won't be an Integer unless rounded
		
		UnitView u = currentParent;
		total += gCost.get(currentParent).intValue();
		while(u != startParent)
		{
			u = parentNodes.get(u);
			total += gCost.get(u).intValue();
		}
		
		return new Integer(total);
		
	}
	
	//Goes through oList and checks against Hashmap fCost to find the UnitView with the lowest fCost
	public UnitView getLowestCostF(ArrayList<UnitView> oList, HashMap<UnitView, Integer> fCost)
	{
		UnitView lowestCostF = oList.get(0);
		for(int i = 0; i < oList.size(); i++)
		{
			if(fCost.get(oList.get(i)) < fCost.get(lowestCostF))
			{
				lowestCostF = oList.get(i);
			}
		}
		return lowestCostF;
	}
	
	//returns the path from start to goal
	public Map<Integer, Action> rebuildPath(HashMap<UnitView, UnitView> parentNodes, UnitView goalParent, UnitView startParent)
	{
		ArrayList<UnitView> backwardsPath = new ArrayList<UnitView>();
		Map<Integer, Action> path = new HashMap<Integer, Action>();
		backwardsPath.add(goalParent);
		while(backwardsPath.get(backwardsPath.size()-1) != startParent)
		{
			backwardsPath.add(parentNodes.get(backwardsPath.get(backwardsPath.size()-1)));
		}
		
		for(int i = (backwardsPath.size()-1); i > 0; i--)
		{
			int xDiff = backwardsPath.get(i).getXPosition() - backwardsPath.get(i-1).getXPosition();
			int yDiff = backwardsPath.get(i).getYPosition() - backwardsPath.get(i-1).getYPosition();
			
			Direction d = Direction.EAST; //default value
			if(xDiff > 0 && yDiff > 0) //NW
				d = Direction.NORTHEAST;
			else if(xDiff == 0 && yDiff > 0) //N
				d = Direction.NORTH;
			else if(xDiff < 0 && yDiff > 0) //NE
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
			
			path.put(backwardsPath.get(i).getID(), Action.createPrimitiveMove(backwardsPath.get(i).getID(), d));
		}
		
		return path;
		
	}
	
	public ArrayList<UnitView> getNeighbors(UnitView currentParent, StateView state)
	{
		ArrayList<UnitView> neighbors = new ArrayList<UnitView>();
		if(!state.isUnitAt(currentParent.getXPosition()-1, currentParent.getYPosition()-1))
		{
			//above checks to see if there is a Unit at the currentParent's position -1, -1
			//not sure if correct, if it is, how do I add to arraylist?
		}
		return neighbors;
	}
	public Integer heuristicCostCalculator(UnitView a, UnitView b)	{
	
		int x1 = a.getXPosition();
		int x2 = b.getXPosition();
		int y1 = a.getYPosition();
		int y2 = b.getYPosition();
		
		return (DistanceMetrics.chebyshevDistance(x1, y1, x2, y2));
	}

}
