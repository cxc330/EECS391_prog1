package edu.cwru.SimpleRTS.agent;

import java.util.*;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
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
			UnitView currentParent = getLowestCostF(openList, fCost); //Jeff implement the lowest cost F finder
			
			if (currentParent.equals(goalSpace) ) //great success!
			{
				actions = rebuildPath(parentNodes, currentParent); //Jeff implement rebuilding the path we came from
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
						tempHCost = heuristicCostCalculator(neighbor, goalSpace); //get the costs of the starting node
						tempGCost = gCostCalculator(neighbor, currentParent); //Jeff implement gCost calculation
						tempFCost = tempHCost + tempGCost; //see above
					}					
				}
			}
		}
		
		
		return actions;
	}
	
	public Integer heuristicCostCalculator(UnitView a, UnitView b)	{
	
		int x1 = a.getXPosition();
		int x2 = b.getXPosition();
		int y1 = a.getYPosition();
		int y2 = b.getYPosition();
		
		return (DistanceMetrics.chebyshevDistance(x1, y1, x2, y2));
	}

}
