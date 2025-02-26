package net.sf.openrocket.gui.configdialog;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.CustomFocusTraversalPolicy;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.adaptors.EnumModel;
import net.sf.openrocket.gui.adaptors.IntegerModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.position.AxialMethod;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.unit.UnitGroup;

public class TubeFinSetConfig extends RocketComponentConfig {
	private static final long serialVersionUID = 508482875624928676L;
	private static final Translator trans = Application.getTranslator();
	
	public TubeFinSetConfig(OpenRocketDocument d, RocketComponent c, JDialog parent) {
		super(d, c, parent);
		
		JPanel primary = new JPanel(new MigLayout("fill"));
		
		
		JPanel panel = new JPanel(new MigLayout("gap rel unrel", "[][65lp::][30lp::][]", ""));
		
		////  Number of fins
		panel.add(new JLabel(trans.get("TubeFinSetCfg.lbl.Nbroffins")));
		
		IntegerModel im = new IntegerModel(component, "FinCount", 1, 8);
		
		JSpinner spin = new JSpinner(im.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx, wrap");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());

		//// Length:
		panel.add(new JLabel(trans.get("TubeFinSetCfg.lbl.Length")));
		
		DoubleModel m = new DoubleModel(component, "Length", UnitGroup.UNITS_LENGTH, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		focusElement = spin;
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.02, 0.1)), "w 100lp, wrap para");
		
		
		//// Outer diameter:
		panel.add(new JLabel(trans.get("TubeFinSetCfg.lbl.Outerdiam")));
		
		DoubleModel od = new DoubleModel(component, "OuterRadius", 2, UnitGroup.UNITS_LENGTH, 0);
		// Diameter = 2*Radius
		
		spin = new JSpinner(od.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(od), "growx");
		panel.add(new BasicSlider(od.getSliderModel(0, 0.04, 0.2)), "w 100lp, wrap rel");
		
		JCheckBox check = new JCheckBox(od.getAutomaticAction());
		//// Automatic
		check.setText(trans.get("TubeFinSetCfg.checkbox.Automatic"));
		panel.add(check, "skip, span 2, wrap");
		order.add(check);

		////  Inner diameter:
		panel.add(new JLabel(trans.get("TubeFinSetCfg.lbl.Innerdiam")));
		
		// Diameter = 2*Radius
		m = new DoubleModel(component, "InnerRadius", 2, UnitGroup.UNITS_LENGTH, 0);
		
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(new DoubleModel(0), od)), "w 100lp, wrap rel");
		
		
		////  Wall thickness
		//// Thickness:
		panel.add(new JLabel(trans.get("TubeFinSetCfg.lbl.Thickness")));
		
		m = new DoubleModel(component, "Thickness", UnitGroup.UNITS_LENGTH, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.01)), "w 100lp, wrap 20lp");
		
		
		////  Base rotation
		//// Fin rotation:
		JLabel label = new JLabel(trans.get("TubeFinSetCfg.lbl.Finrotation"));
		//// The angle of the first fin in the fin set.
		label.setToolTipText(trans.get("TubeFinSetCfg.lbl.ttip.Finrotation"));
		panel.add(label);
		
		m = new DoubleModel(component, "BaseRotation", UnitGroup.UNITS_ANGLE);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(-Math.PI, Math.PI)), "w 100lp, wrap");
		
		primary.add(panel, "grow, gapright 20lp");
		panel = new JPanel(new MigLayout("gap rel unrel", "[][65lp::][30lp::][]", ""));
		
		//// Position relative to:
		panel.add(new JLabel(trans.get("LaunchLugCfg.lbl.Posrelativeto")));  // (note re-uses the label from LaunchLug, because they're the same
		
		final EnumModel<AxialMethod> axialMethodModel = new EnumModel<AxialMethod>(component, "AxialMethod", AxialMethod.axialOffsetMethods );
		final JComboBox<AxialMethod> axialMethodCombo = new JComboBox<AxialMethod>( axialMethodModel );
		panel.add(axialMethodCombo, "spanx, growx, wrap");
		order.add(axialMethodCombo);

		//// plus
		panel.add(new JLabel(trans.get("LaunchLugCfg.lbl.plus")), "right");
		
		final DoubleModel axialOffsetModel = new DoubleModel(component, "AxialOffset", UnitGroup.UNITS_LENGTH);
		spin = new JSpinner(axialOffsetModel.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());

		axialMethodCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				axialOffsetModel.stateChanged(new EventObject(e));
			}
		});

		panel.add(new UnitSelector(axialOffsetModel), "growx");
		panel.add(new BasicSlider(axialOffsetModel.getSliderModel(
				new DoubleModel(component.getParent(), "Length", -1.0, UnitGroup.UNITS_NONE),
				new DoubleModel(component.getParent(), "Length"))),
				"w 100lp, wrap para");
		
		
		
		//// Material
		MaterialPanel materialPanel = new MaterialPanel(component, document, Material.Type.BULK, order);
		panel.add(materialPanel, "span, wrap");
		
		primary.add(panel, "grow");
		
		//// General and General properties
		tabbedPane.insertTab(trans.get("LaunchLugCfg.tab.General"), null, primary,
				trans.get("LaunchLugCfg.tab.Generalprop"), 0);
		tabbedPane.setSelectedIndex(0);

		// Apply the custom focus travel policy to this config dialog
		order.add(closeButton);		// Make sure the close button is the last component
		CustomFocusTraversalPolicy policy = new CustomFocusTraversalPolicy(order);
		parent.setFocusTraversalPolicy(policy);
	}
	
	@Override
	public void updateFields() {
		super.updateFields();
	}
	
}
