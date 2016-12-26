package project.dos;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import java.util.HashMap;

import static project.dos.BattleField.battleField;
import static project.dos.BattlefieldLogic.battlefieldLogic;

public class GraphicsFragment extends AndroidFragmentApplication {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		GameActivity activity = (GameActivity) getActivity();
		battlefieldLogic.configure(activity.networkController,
				activity.dataBaseController);
		//battlefieldLogic.configure(
		//        () -> networkController.sendMessageToAll(battlefieldLogic.message),
		//        () -> dataBaseController.insertOrEditCreature(battlefieldLogic.creatureToSetOrRemove),
		//        () -> dataBaseController.removeCreature(battlefieldLogic.creatureToSetOrRemove));
		battlefieldLogic.creatures = new HashMap<Integer, Creature>();
		battlefieldLogic.freeID = 0;
		Creature starter1 = new Creature(0, new HexCoord(-10, 1), battlefieldLogic.freeID++, true);
		Creature starter2 = new Creature(1, new HexCoord(10, -1), battlefieldLogic.freeID++, true);
		battlefieldLogic.creatures.put(starter1.iD, starter1);
		battlefieldLogic.creatures.put(starter2.iD, starter2);
		battlefieldLogic.pushToDatabase(starter1);
		battlefieldLogic.pushToDatabase(starter2);
		if (activity.networkController.isHost) {
			battlefieldLogic.getTurn();
		}
		return initializeForView(battleField);
	}
}
