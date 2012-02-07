package edu.cwru.SimpleRTS.agent;

import java.util.*;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class SearchAgent extends Agent {

	private static final long serialVersionUID = 1L;
	static int minGoldToCarry = 0;
	static int minWoodToCarry = 0;
	static int playernum = 0;
	static int numPeasantsToBuild = 3;
	static int numFootmanToBuild = 2;
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
		
		
		return null;
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
	
	public boolean collectResource(List<Integer> peasants, Map<Integer, Action> actionList, StateView state, Integer townHall, Type resource, int minToGather)	{
		
		Action action = null;
		
		for (Integer peasantId: peasants)
		{
			List<Integer> resourceIds = state.getResourceNodeIds(resource);
			
			if(state.getUnit(peasantId).getCargoType() == Type.getResourceType(resource) && state.getUnit(peasantId).getCargoAmount() > minToGather)
			{
				action = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townHall);
			}
			else if(resourceIds.size() > 0)
			{
				action = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
			}
			else
			{
				System.out.println("Can't collect anymore " + resource.toString());
				return false;
				//do nothing
			}
			
			if (action != null)
			{
				actionList.put(peasantId, action);
			}
		}
		return true;
	}

}
