/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.objectpools;

import java.util.Arrays;
import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class CausePool extends FactorPool
{
	public CausePool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.CAUSE);
	}
	
	public void put(Cause cause) throws Exception
	{
		put(cause.getId(), cause);
	}
	
	public Cause find(BaseId id)
	{
		return (Cause)getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception
	{
		return new Cause(objectManager ,new FactorId(actualId.asInt()));
	}
	
	public Cause[] getDirectThreats()
	{
		return getDirectThreatsAsVector().toArray(new Cause[0]);
	}

	public Vector<Cause> getDirectThreatsAsVector()
	{
		return getCauses(true);
	}
	
	public Vector<Cause> getContributingFactors()
	{
		return getCauses(false);
	}

	private Vector<Cause> getCauses(boolean isDirectThreat)
	{
		Vector<Cause> causes = new Vector<Cause>();
		BaseId[] ids = getIds();
		Arrays.sort(ids);
		for(int i = 0; i < ids.length; ++i)
		{
			Cause cause = (Cause)getRawObject(ids[i]);
			if(cause.isDirectThreat() == isDirectThreat)
				causes.add(cause);
		}
		return causes;
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return Cause.createSchema(projectToUse);
	}
}
