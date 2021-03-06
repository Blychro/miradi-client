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
package org.miradi.forms.summary;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.objects.ProjectMetadata;
import org.miradi.questions.CountriesQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.views.summary.SummaryLocationPanel;

public class LocationTabForm extends FieldPanelSpec
{
	public LocationTabForm()
	{
		setTranslatedTitle(SummaryLocationPanel.getLocationPanelDescription());

		int type = ProjectMetadataSchema.getObjectType();

		addLabelAndField(type, ProjectMetadata.TAG_PROJECT_LATITUDE);
		addLabelAndField(type, ProjectMetadata.TAG_PROJECT_LONGITUDE);
		
		addCodeListField(type, ProjectMetadata.TAG_COUNTRIES, StaticQuestionManager.getQuestion(CountriesQuestion.class));
		
		addLabelAndField(type, ProjectMetadata.TAG_STATE_AND_PROVINCES);
		addLabelAndField(type, ProjectMetadata.TAG_MUNICIPALITIES);
		addLabelAndField(type, ProjectMetadata.TAG_LEGISLATIVE_DISTRICTS);
		addLabelAndField(type, ProjectMetadata.TAG_LOCATION_DETAIL);
		addLabelAndField(type, ProjectMetadata.TAG_SITE_MAP_REFERENCE);
		addLabelAndField(type, ProjectMetadata.TAG_LOCATION_COMMENTS);
	}
}
