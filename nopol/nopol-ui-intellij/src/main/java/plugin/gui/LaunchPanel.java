package plugin.gui;

import org.jetbrains.annotations.NotNull;
import plugin.actors.ActorManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static plugin.actors.ActorManager.buildRemoteActor;

/**
 * Created by bdanglot on 9/21/16.
 */
public class LaunchPanel extends JPanel {

	private JLabel labelDescriptionNopol;

	private static final String LOCAL_DESCRIPTION = "Start a Nopol server locally and use it, may load your machine";
	private static final String REMOTE_DESCRIPTION = "Ask Inria's Nopol server to find a patch, goes fast but requires good network and non-confidential data";
	private static final String CUSTOM_DESCRIPTION = "Use a custom Nopol server, identified by its IP address";

	private static final String GLOBAL_DESCRIPTION = "Nopol tries to fix the failing test case, by either changing an existing if-condition or "
			+ "by adding a statement precondition (ie inserting a new if).";

	public LaunchPanel() {
		this.setVisible(true);
		this.buildGroupType();
	}

	/**
	 * Add to the Panel a Group of RadioButton for setting up Type
	 */
	private void buildGroupType() {
		ButtonGroup buttonGroup = new ButtonGroup();
		JPanel globalPanel = new JPanel();
		globalPanel.setLayout(new GridLayout(3,1));

		JLabel labelGlobalDescription = new JLabel();
		labelGlobalDescription.setText(GLOBAL_DESCRIPTION);
		globalPanel.add(labelGlobalDescription);

		this.labelDescriptionNopol = new JLabel();
		this.labelDescriptionNopol.setText(LOCAL_DESCRIPTION);
		globalPanel.add(labelDescriptionNopol);

		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.add(buildPanelLocal(buttonGroup));
		radioButtonPanel.add(buildPanelRemoteInria(buttonGroup));
		radioButtonPanel .add(buildPanelCustomRemote(buttonGroup));

		globalPanel.add(radioButtonPanel);
		this.add(globalPanel);
	}

	private JPanel buildPanelLocal(ButtonGroup buttonGroup) {
		JPanel panelLocal = new JPanel();
		JRadioButton localButton = new JRadioButton();
		localButton.setSelected(true);
		localButton.addActionListener(event -> {
			this.labelDescriptionNopol.setText(LOCAL_DESCRIPTION);
			ActorManager.runNopolLocally = true;
			buildRemoteActor("127.0.0.1", "2553");
		});
		panelLocal.add(localButton);
		panelLocal.add(new JLabel("Local"));
		buttonGroup.add(localButton);
		return panelLocal;
	}

	@NotNull
	private JPanel buildPanelCustomRemote(ButtonGroup buttonGroup) {
		JPanel panelCustom = new JPanel();
		JRadioButton customButton = new JRadioButton();
		panelCustom.add(customButton);
		panelCustom.add(new JLabel("custom:"));
		JTextField adrCustom = new JTextField();
		adrCustom.setText("127.0.0.1:2552");
		customButton.addActionListener(event -> {
			this.labelDescriptionNopol.setText(CUSTOM_DESCRIPTION);
			ActorManager.runNopolLocally = false;
			String[] input = adrCustom.getText().split(":");
			buildRemoteActor(input[0], input[1]);
		});
		adrCustom.addKeyListener(getKeyAdapter(adrCustom));
		buttonGroup.add(customButton);
		panelCustom.add(adrCustom);
		return panelCustom;
	}

	@NotNull
	private JPanel buildPanelRemoteInria(ButtonGroup buttonGroup) {
		JPanel panelInria = new JPanel();
		JRadioButton buttonInria = new JRadioButton();
		buttonInria.addActionListener(event -> {
			this.labelDescriptionNopol.setText(REMOTE_DESCRIPTION);
			ActorManager.runNopolLocally = false;
			buildRemoteActor(ActorManager.addressNopol, ActorManager.portNopol);
		});
		panelInria.add(buttonInria);
		panelInria.add(new JLabel("Remote Inria"));
		buttonGroup.add(buttonInria);
		return panelInria;
	}

	@NotNull
	private KeyAdapter getKeyAdapter(final JTextField adrCustom) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 1) {
					String[] input = adrCustom.getText().split(":");
					buildRemoteActor(input[0], input[1]);
				}
			}
		};
	}

}
