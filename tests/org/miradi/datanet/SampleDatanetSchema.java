/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.datanet;

public class SampleDatanetSchema extends DatanetSchema
{
	public SampleDatanetSchema() throws Exception
	{
		owner = createOwnerType();
		member = createMemberType();
		other = createOtherType();
		ownerToMember = new LinkageType(OWNER_TO_MEMBER, owner, member, LinkageType.CONTAINS);
		
		addRecordType(owner);
		addRecordType(member);
		addRecordType(other);
		addLinkageType(ownerToMember);
	}
	
	RecordType createOwnerType() throws Exception
	{
		RecordType type = new RecordType(OWNER);
		type.addField(LABEL, RecordType.STRING);
		type.addField(COMMENTS, RecordType.LONG_STRING);
		return type;
	}
	
	RecordType createMemberType() throws Exception
	{
		RecordType type = new RecordType(MEMBER);
		return type;
	}
	
	RecordType createOtherType() throws Exception
	{
		RecordType type = new RecordType(OTHER);
		return type;
	}
	
	static String OWNER = "Owner";
	static String MEMBER = "Member";
	static String OTHER = "Other";
	static String OWNER_TO_MEMBER = OWNER + "-" + MEMBER;
	
	static String LABEL = "Label";
	static String COMMENTS = "Comments";
	
	private RecordType owner;
	private RecordType member;
	private RecordType other;
	private LinkageType ownerToMember;
}
