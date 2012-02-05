package edu.cwru.SimpleRTS.agent;

import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class firstAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected firstAgent(int playernum) {
		super(playernum);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<Integer, Action> initialStep(StateView state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView state) {
		// TODO Auto-generated method stub
		
		List<Integer> allUnitIds = state.getAllUnitIds();
		
		for (int x = 0; x < allUnitIds.size(); x++)
		{
			Integer unitId = allUnitIds.get(x);
			UnitView unit = state.getUnit(unitId);
			
			if(unit.getTemplateView().canBuild())
			{
				Action.createPrimitiveProduction(unitId, unit.getTemplateView().getID());
			}
		}
		
		return null;
	}

	@Override
	public void terminalStep(StateView state) {
		// TODO Auto-generated method stub

	}

}
