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

package org.miradi.questions;

import org.miradi.main.EAM;

import java.awt.*;

abstract public class AbstractResultReportStatusQuestion extends StaticChoiceQuestionSortableByNaturalOrder
{
    public AbstractResultReportStatusQuestion()
    {
        super();
    }

    @Override
    protected ChoiceItem[] createChoices()
    {
        return new ChoiceItem[] {
                new ChoiceItem(NOT_SPECIFIED, EAM.text("Not Specified"), Color.WHITE),
                new ChoiceItem(NOT_KNOWN_CODE, getNotKnownLabel(), COLOR_NOT_KNOWN),
                new ChoiceItem(NOT_YET_CODE, getNotYetLabel(), COLOR_PLANNED),
                new ChoiceItem(NOT_ACHIEVED_CODE, getNotAchievedLabel(), COLOR_ALERT),
                new ChoiceItem(PARTIALLY_ACHIEVED_CODE, getPartiallyAchievedLabel(), COLOR_CAUTION),
                new ChoiceItem(ON_TRACK_CODE, getOnTrackLabel(), COLOR_OK),
                new ChoiceItem(ACHIEVED_CODE, getAchievedLabel(), COLOR_GREAT),
                new ChoiceItem(NO_LONGER_RELEVANT_CODE, getNoLongerRelevantLabel(), COLOR_ABANDONED),
        };
    }

    abstract protected String getNotKnownLabel();

    abstract protected String getNotYetLabel();

    abstract protected String getNotAchievedLabel();

    abstract protected String getPartiallyAchievedLabel();

    abstract protected String getOnTrackLabel();

    abstract protected String getAchievedLabel();

    abstract protected String getNoLongerRelevantLabel();

    public static final String NOT_SPECIFIED = "";
    public static final String NOT_KNOWN_CODE = "NotKnown";
    public static final String NOT_YET_CODE = "NotYet";
    public static final String NOT_ACHIEVED_CODE = "NotAchieved";
    public static final String PARTIALLY_ACHIEVED_CODE = "MinorIssues";
    public static final String ON_TRACK_CODE = "OnTrack";
    public static final String ACHIEVED_CODE = "Achieved";
    public static final String NO_LONGER_RELEVANT_CODE = "NoLongerRelevant";
}
