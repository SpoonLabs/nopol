package plugin.gui;

import com.intellij.openapi.ui.ComboBox;
import fr.inria.lille.repair.common.patch.Patch;
import plugin.wrapper.ApplyPatchWrapper;

import javax.swing.*;
import java.util.List;

/**
 * Created by bdanglot on 9/20/16.
 */
public class ApplyPatchPanel extends JPanel {

	public ApplyPatchPanel(ApplyPatchWrapper parent, List<Patch> patches) {
		JComboBox<Patch> selectionPatches = new ComboBox<>(patches.toArray(new Patch[patches.size()]));
		selectionPatches.addActionListener(e -> {
			parent.setSelectedPatch((Patch) ((JComboBox) e.getSource()).getSelectedItem());
		});
		System.out.println(patches.get(0));
		parent.setSelectedPatch(patches.get(0));
		this.add(selectionPatches);
	}

}
