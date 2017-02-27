package plugin.gui;

import plugin.action.ActionGenerator;
import com.intellij.openapi.project.Project;
import fr.inria.lille.repair.common.config.NopolContext;

import javax.swing.*;
import java.awt.*;

import static plugin.Plugin.nopolContext;

/**
 * Created by bdanglot on 9/16/16.
 */
public class ConfigPanel extends JPanel {


	private final Project project;

	public ConfigPanel(Project project) {
		this.project = project;
		this.setLayout(new GridLayout());
		this.buildGroupSynthesis();
		this.buildPanelFancyRobot();
		this.setVisible(true);
	}

	/**
	 * Add to the Panel a Group of RadioButton for setting up Synthesis
	 */
	private void buildGroupSynthesis() {

		JRadioButton dynamothSynthesis = new JRadioButton();
		dynamothSynthesis.setSelected(nopolContext.getSynthesis() == NopolContext.NopolSynthesis.DYNAMOTH);
		dynamothSynthesis.setActionCommand(String.valueOf(NopolContext.NopolSynthesis.DYNAMOTH));
		dynamothSynthesis.addActionListener(event -> nopolContext.setSynthesis(NopolContext.NopolSynthesis.DYNAMOTH));

		JRadioButton smtSynthesis = new JRadioButton();
		smtSynthesis.setSelected(nopolContext.getSynthesis() == NopolContext.NopolSynthesis.SMT);
		smtSynthesis.setActionCommand(String.valueOf(NopolContext.NopolSynthesis.SMT));
		smtSynthesis.addActionListener(event -> nopolContext.setSynthesis(NopolContext.NopolSynthesis.SMT));

		ButtonGroup groupSynthesis = new ButtonGroup();
		groupSynthesis.add(smtSynthesis);
		groupSynthesis.add(dynamothSynthesis);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 3));

		panel.add(new JLabel("Synthesis: "));
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));

		panel.add(new JLabel("Dynamoth"));
		panel.add(new JLabel(""));
		panel.add(dynamothSynthesis);

		panel.add(new JLabel("SMT"));
		panel.add(new JLabel(""));
		panel.add(smtSynthesis);

		this.add(panel);
	}

	private void buildPanelFancyRobot() {
		JCheckBox enableFancyRobot = new JCheckBox("enable Fancy Robot");
		enableFancyRobot.setSelected(true);
		enableFancyRobot.addActionListener(e -> plugin.Plugin.enableFancyRobot = enableFancyRobot.isSelected());
		JButton generator = new JButton("Generate Toy Project");
		generator.addActionListener(new ActionGenerator(project));
		JPanel panelRobot = new JPanel();
		panelRobot.setLayout(new GridLayout(2, 1));
		panelRobot.add(enableFancyRobot);
		panelRobot.add(generator);
		this.add(panelRobot);
	}

}
