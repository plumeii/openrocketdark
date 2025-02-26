package net.sf.openrocket.gui.configdialog;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.BooleanModel;
import net.sf.openrocket.gui.adaptors.CustomFocusTraversalPolicy;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.adaptors.TransitionShapeModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.DescriptionArea;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.rocketcomponent.NoseCone;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.SymmetricComponent;
import net.sf.openrocket.rocketcomponent.Transition;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.unit.UnitGroup;

@SuppressWarnings("serial")
public class NoseConeConfig extends RocketComponentConfig {
	
	
	private DescriptionArea description;
	
	private JLabel shapeLabel;
	private JSpinner shapeSpinner;
	private JSlider shapeSlider;
	private final JCheckBox checkAutoAftRadius;
	private static final Translator trans = Application.getTranslator();
	
	// Prepended to the description from NoseCone.DESCRIPTIONS
	private static final String PREDESC = "<html>";
	
	public NoseConeConfig(OpenRocketDocument d, RocketComponent c, JDialog parent) {
		super(d, c, parent);
		
		final JPanel panel = new JPanel(new MigLayout("", "[][65lp::][30lp::]"));
		
		////  Shape selection
		{//// Nose cone shape:
			panel.add(new JLabel(trans.get("NoseConeCfg.lbl.Noseconeshape")));

			final JComboBox<Transition.Shape> typeBox = new JComboBox<>(new TransitionShapeModel(c));
			typeBox.setEditable(false);
			typeBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Transition.Shape s = (Transition.Shape) typeBox.getSelectedItem();
					if (s != null) {
						description.setText(PREDESC + s.getNoseConeDescription());
					}
					updateEnabled();
				}
			});
			panel.add(typeBox, "span, wrap rel");
			order.add(typeBox);

			////  Shape parameter:
			this.shapeLabel = new JLabel(trans.get("NoseConeCfg.lbl.Shapeparam"));
			panel.add(shapeLabel);

			final DoubleModel parameterModel = new DoubleModel(component, "ShapeParameter");

			this.shapeSpinner = new JSpinner(parameterModel.getSpinnerModel());
			shapeSpinner.setEditor(new SpinnerEditor(shapeSpinner));
			panel.add(shapeSpinner, "growx");
			order.add(((SpinnerEditor) shapeSpinner.getEditor()).getTextField());

			DoubleModel min = new DoubleModel(component, "ShapeParameterMin");
			DoubleModel max = new DoubleModel(component, "ShapeParameterMax");
			this.shapeSlider = new BasicSlider(parameterModel.getSliderModel(min, max));
			panel.add(shapeSlider, "skip, w 100lp, wrap para");

			updateEnabled();
		}

		{ /// Nose cone length:
			panel.add(new JLabel(trans.get("NoseConeCfg.lbl.Noseconelength")));

			final DoubleModel lengthModel = new DoubleModel(component, "Length", UnitGroup.UNITS_LENGTH, 0);
			JSpinner spin = new JSpinner(lengthModel.getSpinnerModel());
			spin.setEditor(new SpinnerEditor(spin));
			panel.add(spin, "growx");
			order.add(((SpinnerEditor) spin.getEditor()).getTextField());

			panel.add(new UnitSelector(lengthModel), "growx");
			panel.add(new BasicSlider(lengthModel.getSliderModel(0, 0.1, 0.7)), "w 100lp, wrap");
		}
		{
			/// Base diameter:

			panel.add(new JLabel(trans.get("NoseConeCfg.lbl.Basediam")));

			final DoubleModel aftRadiusModel = new DoubleModel(component, "AftRadius", 2.0, UnitGroup.UNITS_LENGTH, 0); // Diameter = 2*Radius
			final JSpinner radiusSpinner = new JSpinner(aftRadiusModel.getSpinnerModel());
			radiusSpinner.setEditor(new SpinnerEditor(radiusSpinner));
			panel.add(radiusSpinner, "growx");
			order.add(((SpinnerEditor) radiusSpinner.getEditor()).getTextField());

			panel.add(new UnitSelector(aftRadiusModel), "growx");
			panel.add(new BasicSlider(aftRadiusModel.getSliderModel(0, 0.04, 0.2)), "w 100lp, wrap 0px");

			checkAutoAftRadius = new JCheckBox(aftRadiusModel.getAutomaticAction());
			//// Automatic
			checkAutoAftRadius.setText(trans.get("NoseConeCfg.checkbox.Automatic"));
			panel.add(checkAutoAftRadius, "skip, span 2, wrap");
			order.add(checkAutoAftRadius);
			updateCheckboxAutoAftRadius();
		}

		{////  Wall thickness:
			panel.add(new JLabel(trans.get("NoseConeCfg.lbl.Wallthickness")));

			final DoubleModel thicknessModel = new DoubleModel(component, "Thickness", UnitGroup.UNITS_LENGTH, 0);
			final JSpinner thicknessSpinner = new JSpinner(thicknessModel.getSpinnerModel());
			thicknessSpinner.setEditor(new SpinnerEditor(thicknessSpinner));
			panel.add(thicknessSpinner, "growx");
			order.add(((SpinnerEditor) thicknessSpinner.getEditor()).getTextField());

			panel.add(new UnitSelector(thicknessModel), "growx");
			panel.add(new BasicSlider(thicknessModel.getSliderModel(0, 0.01)), "w 100lp, wrap 0px");


			final JCheckBox filledCheckbox = new JCheckBox(new BooleanModel(component, "Filled"));
			//// Filled
			filledCheckbox.setText(trans.get("NoseConeCfg.checkbox.Filled"));
			filledCheckbox.setToolTipText(trans.get("NoseConeCfg.checkbox.Filled.ttip"));
			panel.add(filledCheckbox, "skip, span 2, wrap");
			order.add(filledCheckbox);
		}

		panel.add(new JLabel(""), "growy");

		////  Description
		
		JPanel panel2 = new JPanel(new MigLayout("ins 0"));
		
		description = new DescriptionArea(5);
		description.setText(PREDESC + ((NoseCone) component).getType().getNoseConeDescription());
		panel2.add(description, "wmin 250lp, spanx, growx, wrap para");
		

		//// Material
		MaterialPanel materialPanel = new MaterialPanel(component, document, Material.Type.BULK, order);
		panel2.add(materialPanel, "span, wrap");
		panel.add(panel2, "cell 4 0, gapleft paragraph, aligny 0%, spany");
		

		//// General and General properties
		tabbedPane.insertTab(trans.get("NoseConeCfg.tab.General"), null, panel,
				trans.get("NoseConeCfg.tab.ttip.General"), 0);
		//// Shoulder and Shoulder properties
		tabbedPane.insertTab(trans.get("NoseConeCfg.tab.Shoulder"), null, shoulderTab(),
				trans.get("NoseConeCfg.tab.ttip.Shoulder"), 1);
		tabbedPane.setSelectedIndex(0);

		// Apply the custom focus travel policy to this config dialog
		order.add(closeButton);		// Make sure the close button is the last component
		CustomFocusTraversalPolicy policy = new CustomFocusTraversalPolicy(order);
		parent.setFocusTraversalPolicy(policy);
	}
	
	
	private void updateEnabled() {
		boolean e = ((NoseCone) component).getType().usesParameter();
		shapeLabel.setEnabled(e);
		shapeSpinner.setEnabled(e);
		shapeSlider.setEnabled(e);
	}

	/**
	 * Sets the checkAutoAftRadius checkbox's enabled state and tooltip text, based on the state of its next component.
	 * If there is no next symmetric component or if that component already has its auto checkbox checked, the
	 * checkAutoAftRadius checkbox is disabled.
	 */
	private void updateCheckboxAutoAftRadius() {
		if (component == null || checkAutoAftRadius == null) return;

		// Disable check button if there is no component to get the diameter from
		SymmetricComponent nextComp = ((NoseCone) component).getNextSymmetricComponent();
		if (nextComp == null) {
			checkAutoAftRadius.setEnabled(false);
			((NoseCone) component).setAftRadiusAutomatic(false);
			checkAutoAftRadius.setToolTipText(trans.get("NoseConeCfg.checkbox.ttip.Automatic_noReferenceComponent"));
			return;
		}
		if (!nextComp.usesPreviousCompAutomatic()) {
			checkAutoAftRadius.setEnabled(true);
			checkAutoAftRadius.setToolTipText(trans.get("NoseConeCfg.checkbox.ttip.Automatic"));
		} else {
			checkAutoAftRadius.setEnabled(false);
			((NoseCone) component).setAftRadiusAutomatic(false);
			checkAutoAftRadius.setToolTipText(trans.get("NoseConeCfg.checkbox.ttip.Automatic_alreadyAuto"));
		}
	}
}
