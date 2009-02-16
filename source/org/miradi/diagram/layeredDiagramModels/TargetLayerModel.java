/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.diagram.layeredDiagramModels;

import java.util.Vector;

import org.miradi.diagram.PersistentDiagramModel;
import org.miradi.objects.Target;
import org.miradi.project.Project;

public class TargetLayerModel extends PersistentDiagramModel
{
	public TargetLayerModel(Project projectToUse)
	{
		super(projectToUse);
	}

	public Vector<Integer> getTypesForModel()
	{
		Vector<Integer> types = new Vector();
		types.add(Target.getObjectType());
		
		return types;
	}
}
