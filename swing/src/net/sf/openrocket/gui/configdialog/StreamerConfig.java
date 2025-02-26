package net.sf.openrocket.gui.configdialog;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
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
import net.sf.openrocket.gui.adaptors.MaterialModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.HtmlLabel;
import net.sf.openrocket.gui.components.StyledLabel;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.gui.widgets.SelectColorButton;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.material.Material;
import net.sf.openrocket.rocketcomponent.DeploymentConfiguration;
import net.sf.openrocket.rocketcomponent.DeploymentConfiguration.DeployEvent;
import net.sf.openrocket.rocketcomponent.position.AxialMethod;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.Streamer;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.unit.UnitGroup;

public class StreamerConfig extends RecoveryDeviceConfig {
	private static final long serialVersionUID = -4445736703470494588L;
	private static final Translator trans = Application.getTranslator();
	
	public StreamerConfig(OpenRocketDocument d, final RocketComponent component, JDialog parent) {
		super(d, component, parent);
		Streamer streamer = (Streamer) component;

		//	Left side
		JPanel primary = new JPanel(new MigLayout());
		
		JPanel panel = new JPanel(new MigLayout("gap rel unrel", "[][65lp::][30lp::][]", ""));
		
		////	Attributes
		panel.add(new StyledLabel(trans.get("StreamerCfg.lbl.Attributes"), StyledLabel.Style.BOLD), "wrap unrel");

		//// Strip length:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Striplength")));
		
		DoubleModel m = new DoubleModel(component, "StripLength", UnitGroup.UNITS_LENGTH, 0);
		
		JSpinner spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.6, 1.5)), "w 100lp, wrap");
		
		//// Strip width:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Stripwidth")));
		
		m = new DoubleModel(component, "StripWidth", UnitGroup.UNITS_LENGTH, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.2)), "w 100lp, wrap 10lp");

		//// Strip area:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Striparea")));
		
		m = new DoubleModel(component, "Area", UnitGroup.UNITS_AREA, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.04, 0.25)), "w 100lp, wrap");
		
		//// Aspect ratio:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Aspectratio")));
		
		m = new DoubleModel(component, "AspectRatio", UnitGroup.UNITS_NONE, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		//		panel.add(new UnitSelector(m),"growx");
		panel.add(new BasicSlider(m.getSliderModel(2, 15)), "skip, w 100lp, wrap 10lp");

		//// Material:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Material")));
		
		JComboBox<Material> streamerMaterialCombo = new JComboBox<Material>(new MaterialModel(panel, component,
				Material.Type.SURFACE));
		//// The component material affects the weight of the component.
		streamerMaterialCombo.setToolTipText(trans.get("StreamerCfg.combo.ttip.MaterialModel"));
		panel.add(streamerMaterialCombo, "spanx 3, growx, wrap 15lp");
		order.add(streamerMaterialCombo);

		// CD
		//// <html>Drag coefficient C<sub>D</sub>:
		JLabel label = new HtmlLabel(trans.get("StreamerCfg.lbl.longA1"));
		//// <html>The drag coefficient relative to the total area of the streamer.<br>
		String tip = trans.get("StreamerCfg.lbl.longB1") +
				//// "A larger drag coefficient yields a slowed descent rate.
				trans.get("StreamerCfg.lbl.longB2");
		label.setToolTipText(tip);
		panel.add(label);
		
		m = new DoubleModel(component, "CD", UnitGroup.UNITS_COEFFICIENT, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setToolTipText(tip);
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		JCheckBox check = new JCheckBox(m.getAutomaticAction());
		//// Automatic
		check.setText(trans.get("StreamerCfg.lbl.AutomaticCd"));
		check.setToolTipText(trans.get("StreamerCfg.lbl.AutomaticCd.ttip"));
		panel.add(check, "skip, span, wrap");
		order.add(check);
		
		//// The drag coefficient is relative to the area of the streamer.
		panel.add(new StyledLabel(trans.get("StreamerCfg.lbl.longC1"),
				-1), "span, wrap");
		

		//	Right side
		primary.add(panel, "grow, gapright 20lp");
		panel = new JPanel(new MigLayout("gap rel unrel", "[][65lp::][30lp::][]", ""));
		
		//// Placement
		panel.add(new StyledLabel(trans.get("StreamerCfg.lbl.Placement"), StyledLabel.Style.BOLD), "wrap unrel");

		//// Position relative to:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Posrelativeto")));
		
		final EnumModel<AxialMethod> methodModel = new EnumModel<AxialMethod>(component, "AxialMethod", AxialMethod.axialOffsetMethods );
		final JComboBox<AxialMethod> positionCombo = new JComboBox<AxialMethod>( methodModel );
		panel.add( positionCombo, "spanx, growx, wrap");
		order.add(positionCombo);
		
		//// plus
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.plus")), "right");
		
		m = new DoubleModel(component, "AxialOffset", UnitGroup.UNITS_LENGTH);
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		focusElement = spin;
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(
				new DoubleModel(component.getParent(), "Length", -1.0, UnitGroup.UNITS_NONE),
				new DoubleModel(component.getParent(), "Length"))),
				"w 100lp, wrap");
		
		
		////  Packed length:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Packedlength")));
		
		m = new DoubleModel(component, "Length", UnitGroup.UNITS_LENGTH, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.1, 0.5)), "w 100lp, wrap");
		
		
		//// Tube diameter
		//// Packed diameter:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Packeddiam")));
		
		DoubleModel od = new DoubleModel(component, "Radius", 2, UnitGroup.UNITS_LENGTH, 0);
		// Diameter = 2*Radius
		
		spin = new JSpinner(od.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(od), "growx");
		panel.add(new BasicSlider(od.getSliderModel(0, 0.04, 0.2)), "w 100lp, wrap");

		////// Automatic
		JCheckBox checkAutoPackedRadius = new JCheckBox(od.getAutomaticAction());
		checkAutoPackedRadius.setText(trans.get("ParachuteCfg.checkbox.AutomaticPacked"));
		checkAutoPackedRadius.setToolTipText(trans.get("ParachuteCfg.checkbox.AutomaticPacked.ttip"));
		panel.add(checkAutoPackedRadius, "skip, span 2, wrap 5lp");
		order.add(checkAutoPackedRadius);

		
		//// Deployment
		panel.add(new StyledLabel(trans.get("StreamerCfg.lbl.Deployment"), StyledLabel.Style.BOLD), "wrap unrel");

		//// Deploys at:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Deploysat") + " " + CommonStrings.dagger), "");
		
		DeploymentConfiguration deploymentConfig = streamer.getDeploymentConfigurations().getDefault();
		JComboBox<DeploymentConfiguration.DeployEvent> eventCombo = new JComboBox<DeploymentConfiguration.DeployEvent>(new EnumModel<DeploymentConfiguration.DeployEvent>(deploymentConfig, "DeployEvent"));
		if( (component.getStageNumber() + 1 ) == d.getRocket().getStageCount() ){
			//	This is the bottom stage.  restrict deployment options.
			eventCombo.removeItem( DeployEvent.LOWER_STAGE_SEPARATION );
		}
		eventCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateFields();
			}
		});
		panel.add( eventCombo, "spanx 3, growx, wrap");
		order.add(eventCombo);
		
		// ... and delay
		//// plus
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.plusdelay")), "right");
		
		m = new DoubleModel(deploymentConfig, "DeployDelay", 0);
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin, 3));
		panel.add(spin, "spanx, split");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		//// seconds
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.seconds")), "wrap paragraph");
		
		// Altitude:
		label = new JLabel(trans.get("StreamerCfg.lbl.Altitude") + CommonStrings.dagger);
		altitudeComponents.add(label);
		panel.add(label);
		
		m = new DoubleModel(deploymentConfig, "DeployAltitude", UnitGroup.UNITS_DISTANCE, 0);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		altitudeComponents.add(spin);
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		UnitSelector unit = new UnitSelector(m);
		altitudeComponents.add(unit);
		panel.add(unit, "growx");
		BasicSlider slider = new BasicSlider(m.getSliderModel(100, 1000));
		altitudeComponents.add(slider);
		panel.add(slider, "w 100lp, wrap");

		panel.add(new StyledLabel(CommonStrings.override_description, -1), "spanx, wrap para");
		
		primary.add(panel, "grow");
		
		updateFields();
		
		//// General and General properties
		tabbedPane.insertTab(trans.get("StreamerCfg.tab.General"), null, primary,
				trans.get("StreamerCfg.tab.ttip.General"), 0);
		//// Radial position and Radial position configuration
		tabbedPane.insertTab(trans.get("StreamerCfg.tab.Radialpos"), null, positionTab(),
				trans.get("StreamerCfg.tab.ttip.Radialpos"), 1);
		tabbedPane.setSelectedIndex(0);

		// Apply the custom focus travel policy to this config dialog
		order.add(closeButton);		// Make sure the close button is the last component
		CustomFocusTraversalPolicy policy = new CustomFocusTraversalPolicy(order);
		parent.setFocusTraversalPolicy(policy);
	}
	
	
	
	
	
	protected JPanel positionTab() {
		JPanel panel = new JPanel(new MigLayout("gap rel unrel", "[][65lp::][30lp::]", ""));
		
		//// Radial position
		//// Radial distance:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Radialdistance")));
		
		DoubleModel m = new DoubleModel(component, "RadialPosition", UnitGroup.UNITS_LENGTH, 0);
		
		JSpinner spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(0, 0.1, 1.0)), "w 100lp, wrap");

		//// Radial direction:
		panel.add(new JLabel(trans.get("StreamerCfg.lbl.Radialdirection")));
		
		m = new DoubleModel(component, "RadialDirection", UnitGroup.UNITS_ANGLE);
		
		spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "growx");
		order.add(((SpinnerEditor) spin.getEditor()).getTextField());
		
		panel.add(new UnitSelector(m), "growx");
		panel.add(new BasicSlider(m.getSliderModel(-Math.PI, Math.PI)), "w 100lp, wrap");
		
		
		//// Reset button
		JButton button = new SelectColorButton(trans.get("StreamerCfg.but.Reset"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((Streamer) component).setRadialDirection(0.0);
				((Streamer) component).setRadialPosition(0.0);
			}
		});
		panel.add(button, "spanx, right");
		order.add(button);

		return panel;
	}
}
