/*
 *  JOrtho
 *
 *  Copyright (C) 2005-2008 by i-net software
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version. 
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 *  
 *  Created on 05.12.2007
 */

/* 
Portions (identified below) were modified by or written by the Miradi team, 
working for Foundations of Success, Bethesda, Maryland (on behalf of 
the Conservation Measures Partnership, "CMP") and Beneficent Technology, 
Inc. ("Benetech"), Palo Alto, California.

These portions may be released under the same terms as the original 
file, specifically either under the GNU GPL, or by i-net software 
under their commercial license.
*/ 

package com.inet.jortho;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class MiradiSpellCheckerMenu extends JMenu implements HierarchyListener
{
    
    private final MiradiCheckerListener listener;

    public MiradiSpellCheckerMenu(String menuItemText, SpellCheckerOptions options){
        super( menuItemText );
        listener = new MiradiCheckerListener(this, options);
        super.addHierarchyListener(this);
    }
    
    public MiradiSpellCheckerMenu(SpellCheckerOptions options){
    	this( Utils.getResource("spelling"), options);
    }


    public void hierarchyChanged(HierarchyEvent ev) {
        // If this sub menu is added to a parent
        // then an Listener is added to request show popup events of the parent
        if(ev.getChangeFlags() == HierarchyEvent.PARENT_CHANGED && ev.getChanged() == this){
            JPopupMenu parent = (JPopupMenu)getParent();
            if(parent != null){
                parent.addPopupMenuListener(listener);
            }else{
                ((JPopupMenu)ev.getChangedParent()).removePopupMenuListener(listener);
            }
        }
    }
}