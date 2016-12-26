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
			for (int i = 0; i < ((GameActivity) getActivity()).networkController.numberOfPlayers; i++) {
				HexCoord startPos = new HexCoord(0, 0);
				switch (i) {
					case 0:
						startPos = new HexCoord(10, -5);
						break;
					case 1:
						startPos = new HexCoord(-10, 5);
						break;
					case 2:
						startPos = new HexCoord(5, 5);
						break;
					case 3:
						startPos = new HexCoord(-5, -5);
						break;
					case 4:
						startPos = new HexCoord(-5, 10);
						break;
					case 5:
						startPos = new HexCoord(5, -10);
						break;
				}
				Creature starter = new Creature(i, startPos, battlefieldLogic.freeID++, true);
				battlefieldLogic.creatures.put(starter.iD, starter);
				battlefieldLogic.pushToDatabase(starter);
			}
		if (battlefieldLogic.owner == 0) {
			battlefieldLogic.getTurn();
		}
		return initializeForView(battleField);
	}
}
