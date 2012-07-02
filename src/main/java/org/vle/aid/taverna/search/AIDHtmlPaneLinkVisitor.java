package org.vle.aid.taverna.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


import org.jdesktop.swingx.hyperlink.LinkModel;
import org.vle.aid.taverna.AIDEditDataflow;

import java.util.logging.*;
/**
 * A ActionListener using a HtmlPanel to "visit" a LinkModel.
 *
 * adds an internal HyperlinkListener to visit links contained
 * in the document.
 *
 * @author Jeanette Winzenburg
 */
public class AIDHtmlPaneLinkVisitor implements ActionListener {
    	
	private HyperlinkListener hyperlinkListener;

	private LinkModel internalLink;

	private JEditorPane editorPane = null;
	
	private static Logger logger = Logger.getLogger(AIDHtmlPaneLinkVisitor.class.getName());

	public AIDHtmlPaneLinkVisitor() {
	}

	public AIDHtmlPaneLinkVisitor(JEditorPane pane) {
	    	Logger.getLogger("org.lobobrowser").setLevel(Level.OFF);
	    	Logger.getLogger("com.steadystate").setLevel(Level.OFF);
	    	
		if (pane == null)
			pane = createDefaultEditorPane();
		
		editorPane = pane;
		
	}

	public JEditorPane getOutputComponent() {
		return editorPane;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof LinkModel) {
			final LinkModel link = (LinkModel) e.getSource();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {				    
				    openIfMyExperiment(link);
				    visit(link);
					
				}
			});
		}

	}

	protected void openIfMyExperiment(LinkModel link) {
	    	URL url =link.getURL();
	    	String urlString =url.toString();
	    	// Based on assumption, indexing format of AIDA toolkit is as follows :
	    	// http://aida.science.uva.nl:9999/search/item/IndexAllOfMyExperimentWorkflow_(772).txt?index=My%20Experiment
	    	
	    	if(urlString.contains("index=My%20Experiment")){
	    	        String workflowID =urlString.substring(urlString.indexOf("(")+1, urlString.indexOf(")"));
	    	        try {
			    url = new URL("http://www.myexperiment.org/workflows/"+workflowID+"/download");
			} catch (MalformedURLException e) {
				logger.severe(e.getMessage());
			}
			
	    	    	//AIDEditDataflow.loadRemoteWorkflow(url);
			        //AIDEditDataflow.addNestedWorkflow(url);
	    	}
	    
	}

	public void visit(LinkModel link) {
		try {		    	
			editorPane.setPage(link.getURL());
			editorPane.revalidate();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());		}
	}

	protected JEditorPane createDefaultEditorPane() {
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		return editorPane;
	}

	protected HyperlinkListener getHyperlinkListener() {
		if (hyperlinkListener == null) {
			hyperlinkListener = createHyperlinkListener();
		}
		return hyperlinkListener;
	}

	protected HyperlinkListener createHyperlinkListener() {
		HyperlinkListener l = new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
					visitInternal(e.getURL());
				}

			}

		};
		return l;
	}

	protected LinkModel getInternalLink() {
		if (internalLink == null) {
			internalLink = new LinkModel("internal");
		}
		return internalLink;
	}

	protected void visitInternal(URL url) {
		try {
			getInternalLink().setURL(url);
			visit(getInternalLink());
		} catch (Exception e) {
			// todo: error feedback
		}
	}

}
