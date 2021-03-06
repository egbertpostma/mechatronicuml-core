package org.muml.core.modelinstance.ui.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.muml.core.ExtendableElement;
import org.muml.core.modelinstance.ModelElementCategory;
import org.muml.core.modelinstance.ModelInstancePlugin;
import org.muml.core.modelinstance.ModelinstancePackage;
import org.muml.core.modelinstance.RootNode;
import org.muml.core.modelinstance.ui.ModelinstanceUiPlugin;

/**
 * Creates a new empty Diagram.
 * 
 * @author bingo
 * 
 */
// TODO: maybe we can reuse CreateDiagramCommand for this special case
public class CreateEmptyDiagramCommand extends AbstractTransactionalCommand {
	private Resource diagramResource;
	private EObject diagramRoot;
	private EObject diagramElement;
	private PreferencesHint preferencesHint;
	private String modelElementCategoryKey;
	private String modelId;

	@SuppressWarnings("rawtypes")
	public CreateEmptyDiagramCommand(TransactionalEditingDomain domain,
			String label, List affectedFiles, Resource diagramResource,
			EObject diagramRoot, EClass diagramElementClass, PreferencesHint preferencesHint, String modelElementCategoryKey, String modelId) {
		super(domain, label, affectedFiles);
		this.diagramResource = diagramResource;
		this.diagramRoot = diagramRoot;
		this.preferencesHint = preferencesHint;
		this.modelId = modelId;
		this.modelElementCategoryKey = modelElementCategoryKey;
		
		if (diagramElementClass == null) {
			throw new NullPointerException("Diagram Editor must specify diagram element class by providing an Extension (hint: Regenerate diagram editor)");
		}
		
		diagramElement = null;
		if (!ModelinstancePackage.Literals.MODEL_ELEMENT_CATEGORY.isSuperTypeOf(diagramElementClass)) {
			diagramElement = EcoreUtil.create(diagramElementClass);
			ModelinstanceUiPlugin.getDefault().initializeModel(diagramElement);
		}
	}

	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		ModelElementCategory category = null;
		if (diagramRoot instanceof RootNode) {
			RootNode rootNode = (RootNode) diagramRoot;
			category = ModelInstancePlugin
					.getInstance()
					.getModelElementCategoryRegistry()
					.getModelElementCategory(rootNode, modelElementCategoryKey);

		} else if (diagramRoot instanceof ModelElementCategory) {
			ModelElementCategory cat = (ModelElementCategory) diagramRoot;
			if (modelElementCategoryKey != null && modelElementCategoryKey.equals(cat.getKey())) {
				category = cat;
			}
		}
		
		if (category == null) {
			return CommandResult.newErrorCommandResult("Model file loading fail");
		}

		if (diagramElement != null) {
			category.getModelElements().add((ExtendableElement) diagramElement);
		} else {
			diagramElement = category;
		}

		Diagram diagram = ViewService.createDiagram(diagramElement,
				modelId, preferencesHint);
		if (diagram == null) {
			return CommandResult
					.newErrorCommandResult("Diagram could not be created.");
		}

		diagramResource.getContents().add(diagram);
		return CommandResult.newOKCommandResult();
	}
}
