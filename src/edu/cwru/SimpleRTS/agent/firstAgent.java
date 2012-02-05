package edu.cwru.SimpleRTS.agent;

import java.util.*;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class firstAgent extends Agent {

	private static final long serialVersionUID = 1L;
	static int minGoldToCarry = 0;
	static String townHall = "TownHall";
	static String peasant = "Peasant";

	public firstAgent(int playernum) {
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
		
		List<Integer> allUnitIds = state.getAllUnitIds();
		List<Integer> townHalls = findUnitType(allUnitIds, state, townHall);
		List<Integer> peasants = findUnitType(allUnitIds, state, peasant);
		
		Map<Integer, Action> actions = new HashMap<Integer,Action>();
		
		if(peasants.size() > 0 && townHalls.size() > 0)
		{
			collectGold(peasants, actions, state, townHalls.get(0));
		}
		else
		{
			System.out.println("Error: bad config, not enough peasants or town halls to start");
		}
		
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
	
	public void collectGold(List<Integer> peasants, Map<Integer, Action> actionList, StateView state, Integer townHall)	{
		
		Action action = null;
		
		for (Integer peasantId: peasants)
		{
			List<Integer> resourceIds = state.getResourceNodeIds(Type.GOLD_MINE);
			
			if(state.getUnit(peasantId).getCargoType() == ResourceType.GOLD && state.getUnit(peasantId).getCargoAmount() > minGoldToCarry)
			{
				action = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townHall);
			}
			else if(resourceIds.size() > 0)
			{
				action = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
			}
			else
			{
				System.out.println("Can't collect anymore gold");
				//do nothing
			}
			
			if (action != null)
			{
				actionList.put(peasantId, action);
			}
		}
	}

}
