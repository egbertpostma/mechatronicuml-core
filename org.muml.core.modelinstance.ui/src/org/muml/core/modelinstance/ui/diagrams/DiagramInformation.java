package org.muml.core.modelinstance.ui.diagrams;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * Datastructure that represents the information provided by an extension of
 * "org.muml.core.modelinstance.ui.diagraminformation".
 * 
 * @author bingo
 * 
 */
public class DiagramInformation implements IDiagramInformation {

	private Map<String, String> topLevelNodes;

	private String preferencesHint;

	private String editorId;

	private String editorName;

	private String fileExtension;

	private String modelId;

	private String modelElementCategoryKey;

	private String diagramElementPackageNsURI;

	private String diagramElementClassName;

	public DiagramInformation(IConfigurationElement configurationElement) {
		preferencesHint = configurationElement.getAttribute("preferencesHint");
		editorId = configurationElement.getAttribute("editorId");
		modelId = configurationElement.getAttribute("modelId");
		editorName = configurationElement.getAttribute("editorName");
		modelElementCategoryKey = configurationElement
				.getAttribute("modelElementCategoryKey");
		fileExtension = configurationElement.getAttribute("fileExtension");

		IConfigurationElement[] diagramElement = configurationElement
				.getChildren("diagramElement");

		if (diagramElement.length == 1) {
			diagramElementPackageNsURI = diagramElement[0]
					.getAttribute("packageNsURI");
			diagramElementClassName = diagramElement[0]
					.getAttribute("className");
		}

		topLevelNodes = new HashMap<String, String>();
		IConfigurationElement[] tlnElements = configurationElement
				.getChildren("topLevelNode");
		for (IConfigurationElement node : tlnElements) {
			String domainElement = node.getAttribute("domainElement");
			String semanticHint = node.getAttribute("semanticHint");
			topLevelNodes.put(domainElement, semanticHint);
		}
	}

	@Override
	public String getEditorName() {
		return editorName;
	}

	@Override
	public String getFileExtension() {
		return fileExtension;
	}

	@Override
	public String getModelId() {
		return modelId;
	}

	@Override
	public String getModelElementCategoryKey() {
		return modelElementCategoryKey;
	}

	@Override
	public String getEditorId() {
		return editorId;
	}

	@Override
	public String getPreferencesHint() {
		return preferencesHint;
	}

	@Override
	public Map<String, String> getTopLevelNodes() {
		return topLevelNodes;
	}

	@Override
	public EClass getDiagramElementClass() {
		EPackage ePackage = EPackage.Registry.INSTANCE
				.getEPackage(diagramElementPackageNsURI);
		EClassifier eClassifier = null;
		if (ePackage != null) {
			eClassifier = ePackage.getEClassifier(diagramElementClassName);
		}
		if (eClassifier instanceof EClass) {
			return (EClass) eClassifier;
		}
		return null;
	}

}
