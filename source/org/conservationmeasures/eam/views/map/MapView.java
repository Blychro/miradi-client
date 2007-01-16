package org.conservationmeasures.eam.views.map;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;
import org.martus.swing.HtmlViewer;
import org.martus.swing.UiScrollPane;

import com.jhlabs.awt.GridLayoutPlus;

public class MapView extends UmbrellaView
{
	public MapView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		setToolBar(new MapToolBar(mainWindowToUse.getActions()));
	}

	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.MAP_VIEW_NAME;
	}

	public void becomeActive() throws Exception
	{
		super.becomeActive();
		JPanel panel = new JPanel(new GridLayoutPlus(0,1));
		panel.add(getIntroText());
		panel.add(new MapComponent(), BorderLayout.CENTER);
		add(new UiScrollPane(panel));
	}
	

	public void becomeInactive() throws Exception
	{
		super.becomeInactive();
	}

	private HtmlViewer getIntroText() throws Exception
	{
		HtmlViewer htmlViewer = new HtmlViewer("",null);
		htmlViewer.setText(EAM.loadResourceFile(this.getClass(), OVERVIEW_HTML));
		
		//TODO: Find a better way to calculate scroll bar width to subtract
		htmlViewer.setFixedWidth(htmlViewer,this.getSize().width - 20);

		return htmlViewer;
	}
	
	
	private String OVERVIEW_HTML = "Overview.html";

}

class MapComponent extends JTabbedPane
{

	public MapComponent()
	{
		setTabPlacement(JTabbedPane.TOP);
	
		String[] demoMaps = 
		{
			"base",
			"scope",
			"targets",
			"threats",
		};
		
		for(int i = 0; i < demoMaps.length; ++i)
		{
			URL imageURL = MapView.class.getResource(demoMaps[i] + ".jpg");
			JLabel image = new JLabel(new ImageIcon(imageURL));
			image.setName(demoMaps[i]);
			add(image);
		}
	}

}

